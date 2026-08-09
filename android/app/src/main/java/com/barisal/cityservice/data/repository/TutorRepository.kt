package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.TutorDto
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

class TutorRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("tutor_posts") }

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
            Log.e("TutorRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a tutor post in Firestore collection "tutor_posts".
     */
    suspend fun saveTutorPostToFirestore(tutorPost: TutorDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (tutorPost.id.isNotBlank()) tutorPost.id else collectionRef.document().id
        val currentUser = FirebaseAuth.getInstance().currentUser
        val postToSave = tutorPost.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: tutorPost.userId,
            userEmail = currentUser?.email ?: tutorPost.userEmail,
            userPhone = currentUser?.phoneNumber ?: tutorPost.userPhone,
            userDisplayName = currentUser?.displayName ?: tutorPost.userDisplayName
        )

        collectionRef.document(docId)
            .set(postToSave)
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
                Log.e("TutorRepository", "Error saving tutor post to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved tutor posts from Firestore.
     */
    fun getApprovedTutorPosts(postType: String = ""): Flow<List<TutorDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TutorRepository", "Error listening to tutor posts: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val approvedList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(TutorDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true

                            if (isApprovedBool) {
                                item?.copy(id = doc.id, isApproved = true)
                            } else null
                        } catch (e: Exception) {
                            Log.e("TutorRepository", "Error parsing TutorDto: ${e.message}")
                            null
                        }
                    }.filter { dto ->
                        postType.isBlank() || dto.postType.equals(postType, ignoreCase = true)
                    }

                    trySend(approvedList)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Real-time flow of pending (unapproved) tutor posts for admin review.
     */
    fun getPendingTutorPosts(): Flow<List<TutorDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("TutorRepository", "Error listening to pending tutor posts: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(TutorDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true

                            if (!isApprovedBool) {
                                item?.copy(id = doc.id)
                            } else null
                        } catch (e: Exception) {
                            Log.e("TutorRepository", "Error parsing pending TutorDto: ${e.message}")
                            null
                        }
                    }

                    trySend(pendingList)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Approves a pending tutor post.
     */
    suspend fun approveTutorPost(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
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
                Log.e("TutorRepository", "Error approving tutor post: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Deletes a tutor post document.
     */
    suspend fun deleteTutorPost(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("TutorRepository", "Error deleting tutor post: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
