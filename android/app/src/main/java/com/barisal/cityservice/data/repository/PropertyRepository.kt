package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.PropertyDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class PropertyRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("property_posts") }

    /**
     * Uploads an image Uri to Firebase Storage under 'properties' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "properties"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple image Uris to Firebase Storage under 'properties' path.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "properties"): Result<List<String>> {
        return FirebaseStorageManager.uploadMultipleFiles(context, uris, folder)
    }

    /**
     * Resizes, compresses, and encodes a selected image Uri into a Base64 data URI string.
     */
    suspend fun compressImageToBase64(context: Context, uri: Uri, maxDimension: Int = 500): Result<String> = withContext(Dispatchers.IO) {
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
            Log.e("PropertyRepository", "Error compressing image: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves a Property post to Firestore collection `property_posts`.
     */
    suspend fun savePropertyPostToFirestore(dto: PropertyDto): Result<Unit> = suspendCancellableCoroutine { continuation ->
        val user = FirebaseAuth.getInstance().currentUser

        val newDocRef = if (dto.id.isNotBlank()) collectionRef.document(dto.id) else collectionRef.document()
        val postToSave = dto.copy(
            id = newDocRef.id,
            userId = dto.userId.ifEmpty { user?.uid ?: "" },
            userEmail = dto.userEmail.ifEmpty { user?.email ?: "" },
            userPhone = dto.userPhone.ifEmpty { user?.phoneNumber ?: "" },
            userDisplayName = dto.userDisplayName.ifEmpty { user?.displayName ?: "" },
            isApproved = false,
            createdAt = if (dto.createdAt != 0L) dto.createdAt else System.currentTimeMillis()
        )

        newDocRef.set(postToSave)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e("PropertyRepository", "Failed to save property post: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Real-time listener for approved property posts (`isApproved == true`).
     */
    fun getApprovedPropertyPosts(): Flow<List<PropertyDto>> = callbackFlow {
        val listener = collectionRef
            .whereEqualTo("isApproved", true)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PropertyRepository", "Error fetching approved property posts", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(PropertyDto::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("PropertyRepository", "Failed parsing document ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time listener for pending property posts (`isApproved == false`).
     */
    fun getPendingPropertyPosts(): Flow<List<PropertyDto>> = callbackFlow {
        val listener = collectionRef
            .whereEqualTo("isApproved", false)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PropertyRepository", "Error fetching pending property posts", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val posts = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        doc.toObject(PropertyDto::class.java)?.copy(id = doc.id)
                    } catch (e: Exception) {
                        Log.e("PropertyRepository", "Failed parsing pending doc ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                trySend(posts)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Approves a pending property post.
     */
    suspend fun approvePropertyPost(postId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(postId)
            .update("isApproved", true)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e("PropertyRepository", "Failed to approve property post", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Deletes / rejects a property post.
     */
    suspend fun deletePropertyPost(postId: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(postId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e("PropertyRepository", "Failed to delete property post", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
