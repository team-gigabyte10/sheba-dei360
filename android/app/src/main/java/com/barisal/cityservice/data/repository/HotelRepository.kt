package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.HotelDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await
import com.barisal.cityservice.core.utils.FirebaseStorageManager
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

class HotelRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Uploads a cover or room image Uri to Firebase Storage under the 'hotels' path,
     * returning its HTTP download URL reference string.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "hotels"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple room image Uris to Firebase Storage under the 'hotels' path,
     * returning a list of HTTP download URL reference strings.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "hotels"): Result<List<String>> {
        return FirebaseStorageManager.uploadMultipleFiles(context, uris, folder)
    }

    /**
     * Resizes, compresses, and encodes a selected image Uri into a Base64 data URI string.
     */
    suspend fun compressImageToBase64(context: Context, uri: Uri, maxDimension: Int = 400): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open image file stream"))
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                return@withContext Result.failure(Exception("Failed to decode image bitmap"))
            }

            val width = originalBitmap.width
            val height = originalBitmap.height
            val (newWidth, newHeight) = if (width > height) {
                if (width > maxDimension) {
                    val ratio = height.toFloat() / width.toFloat()
                    Pair(maxDimension, (maxDimension * ratio).toInt())
                } else {
                    Pair(width, height)
                }
            } else {
                if (height > maxDimension) {
                    val ratio = width.toFloat() / height.toFloat()
                    Pair((maxDimension * ratio).toInt(), maxDimension)
                } else {
                    Pair(width, height)
                }
            }

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            if (scaledBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val bytes = outputStream.toByteArray()
            scaledBitmap.recycle()

            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64String"
            Result.success(dataUri)
        } catch (e: Exception) {
            Log.e("HotelRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a Hotel document in Firestore under collection 'hotels'.
     */
    suspend fun saveHotelToFirestore(hotel: HotelDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val collectionRef = firestore.collection("hotels")
        val docId = if (hotel.id.isNotBlank()) hotel.id else collectionRef.document().id
        val currentUser = FirebaseAuth.getInstance().currentUser

        val hotelToSave = hotel.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: hotel.userId,
            userEmail = currentUser?.email ?: hotel.userEmail,
            userPhone = currentUser?.phoneNumber ?: hotel.userPhone,
            userDisplayName = currentUser?.displayName ?: hotel.userDisplayName
        )

        collectionRef.document(docId).set(hotelToSave)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("HotelRepository", "Error saving hotel to Firestore: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    fun getPendingHotels(): Flow<List<HotelDto>> = callbackFlow {
        val listener = firestore.collection("hotels")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc -> doc.toObject(HotelDto::class.java) }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveHotel(hotelId: String): Result<Unit> {
        return try {
            firestore.collection("hotels").document(hotelId).update("isApproved", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectHotel(hotelId: String): Result<Unit> {
        return try {
            firestore.collection("hotels").document(hotelId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time Flow of all approved hotels from Firestore.
     */
    fun getHotelsFlow(): Flow<List<HotelDto>> = callbackFlow {
        val listener = firestore.collection("hotels")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HotelRepository", "Error listening to hotels: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(HotelDto::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time Flow of a specific hotel by ID.
     */
    fun getHotelById(hotelId: String): Flow<HotelDto?> = callbackFlow {
        if (hotelId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("hotels").document(hotelId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val hotel = snapshot.toObject(HotelDto::class.java)
                trySend(hotel)
            }

        awaitClose { listener.remove() }
    }
}
