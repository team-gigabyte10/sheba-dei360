package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.HouseRentDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import com.barisal.cityservice.core.utils.FirebaseStorageManager
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

class HouseRentRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("house_rents") }

    /**
     * Uploads an image Uri to Firebase Storage under the 'house_rents' path,
     * returning its HTTP download URL reference string.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "house_rents"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple image Uris to Firebase Storage under the 'house_rents' path,
     * returning a list of HTTP download URL reference strings.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "house_rents"): Result<List<String>> {
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
            Log.e("HouseRentRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Compresses multiple images to Base64 in sequence.
     */
    suspend fun compressMultipleImages(context: Context, uris: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        uris.mapNotNull { uri ->
            val res = compressImageToBase64(context, uri)
            res.getOrNull()
        }
    }

    /**
     * Saves or updates a house rent listing in Firestore collection "house_rents".
     */
    suspend fun saveHouseRentToFirestore(houseRent: HouseRentDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (houseRent.id.isNotBlank()) houseRent.id else collectionRef.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val houseRentToSave = houseRent.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: houseRent.userId,
            userEmail = currentUser?.email ?: houseRent.userEmail,
            userPhone = currentUser?.phoneNumber ?: houseRent.userPhone,
            userDisplayName = currentUser?.displayName ?: houseRent.userDisplayName
        )

        collectionRef.document(docId)
            .set(houseRentToSave)
            .addOnSuccessListener {
                collectionRef.document(docId).update(
                    mapOf(
                        "isApproved" to false,
                        "approved" to false
                    )
                )
                if (continuation.isActive) {
                    continuation.resume(Result.success(true))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("HouseRentRepository", "Error saving house rent to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Toggles Rent Status (isRented) in Firestore ("ভাড়া হয়ে গেছে").
     */
    suspend fun updateRentStatus(docId: String, isRented: Boolean): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .update("isRented", isRented)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Real-time flow of approved non-expired house rent listings from Firestore.
     */
    fun getHouseRents(houseType: String = ""): Flow<List<HouseRentDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HouseRentRepository", "Error listening to house rents: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val currentTime = System.currentTimeMillis()
                    val approvedList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(HouseRentDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true

                            val isNotExpired = item == null || item.expiresAt == 0L || item.expiresAt > currentTime

                            if (isApprovedBool && isNotExpired) {
                                if (houseType.isBlank() || houseType == "সব" || houseType == "All" || item?.houseType.equals(houseType, ignoreCase = true)) {
                                    item?.copy(isApproved = true)
                                } else null
                            } else null
                        } catch (e: Exception) {
                            Log.e("HouseRentRepository", "Failed to parse house rent document: ${e.message}")
                            null
                        }
                    }
                    trySend(approvedList)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) house rents for Admin Approval.
     */
    fun getPendingHouseRents(): Flow<List<HouseRentDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("HouseRentRepository", "Error listening to pending house rents: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(HouseRentDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true
                            if (!isApprovedBool) {
                                item?.copy(isApproved = false)
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(pendingList)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves a house rent post in Firestore.
     */
    suspend fun approveHouseRent(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true
                )
            )
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Fetches a single house rent listing by document ID in real time.
     */
    fun getHouseRentById(houseId: String): Flow<HouseRentDto?> = callbackFlow {
        val listener = collectionRef.document(houseId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val dto = snapshot.toObject(HouseRentDto::class.java)
                trySend(dto)
            }
        awaitClose { listener.remove() }
    }

    /**
     * Rejects/deletes an unapproved house rent post from Firestore.
     */
    suspend fun rejectHouseRent(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
