package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.RestaurantDto
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

class RestaurantRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Uploads an image Uri to Firebase Storage under the 'restaurants' path,
     * returning its HTTP download URL reference string.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "restaurants"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple image Uris to Firebase Storage under the 'restaurants' path,
     * returning a list of HTTP download URL reference strings.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "restaurants"): Result<List<String>> {
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
            Log.e("RestaurantRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a Restaurant document in Firestore under collection 'restaurants'.
     */
    suspend fun saveRestaurantToFirestore(restaurant: RestaurantDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val collectionRef = firestore.collection("restaurants")
        val docId = if (restaurant.id.isNotBlank()) restaurant.id else collectionRef.document().id
        val currentUser = FirebaseAuth.getInstance().currentUser

        val restaurantToSave = restaurant.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: restaurant.userId,
            userEmail = currentUser?.email ?: restaurant.userEmail,
            userPhone = currentUser?.phoneNumber ?: restaurant.userPhone,
            userDisplayName = currentUser?.displayName ?: restaurant.userDisplayName
        )

        collectionRef.document(docId).set(restaurantToSave)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("RestaurantRepository", "Error saving restaurant to Firestore: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    fun getPendingRestaurants(): Flow<List<RestaurantDto>> = callbackFlow {
        val listener = firestore.collection("restaurants")
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc -> doc.toObject(RestaurantDto::class.java) }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveRestaurant(restaurantId: String): Result<Unit> {
        return try {
            firestore.collection("restaurants").document(restaurantId).update("isApproved", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectRestaurant(restaurantId: String): Result<Unit> {
        return try {
            firestore.collection("restaurants").document(restaurantId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Real-time Flow of all approved restaurants from Firestore.
     */
    fun getRestaurantsFlow(): Flow<List<RestaurantDto>> = callbackFlow {
        val listener = firestore.collection("restaurants")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("RestaurantRepository", "Error listening to restaurants: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(RestaurantDto::class.java)
                    }
                    trySend(list)
                } else {
                    trySend(emptyList())
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time Flow of a specific restaurant by ID.
     */
    fun getRestaurantById(restaurantId: String): Flow<RestaurantDto?> = callbackFlow {
        if (restaurantId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("restaurants").document(restaurantId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }

                val restaurant = snapshot.toObject(RestaurantDto::class.java)
                trySend(restaurant)
            }

        awaitClose { listener.remove() }
    }
}
