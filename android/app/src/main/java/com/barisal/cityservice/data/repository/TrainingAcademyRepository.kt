package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.barisal.cityservice.data.model.TrainingAcademyDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

class TrainingAcademyRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = firestore.collection("training_academies")

    /**
     * Compress Uri image to compressed Base64 string.
     */
    fun compressImageToBase64(context: Context, imageUri: Uri, maxDimension: Int = 800, quality: Int = 70): Result<String> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
                ?: return Result.failure(Exception("Could not decode image"))
            
            val width = originalBitmap.width
            val height = originalBitmap.height
            val scale = if (width > height) {
                if (width > maxDimension) maxDimension.toFloat() / width else 1f
            } else {
                if (height > maxDimension) maxDimension.toFloat() / height else 1f
            }

            val resizedBitmap = if (scale < 1f) {
                Bitmap.createScaledBitmap(originalBitmap, (width * scale).toInt(), (height * scale).toInt(), true)
            } else {
                originalBitmap
            }

            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            Result.success("data:image/jpeg;base64,$base64String")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Save training course post to Firestore.
     */
    suspend fun saveTrainingPostToFirestore(dto: TrainingAcademyDto): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
            val docId = if (dto.id.isNotBlank()) dto.id else UUID.randomUUID().toString()
            val updatedDto = dto.copy(
                id = docId,
                isApproved = false,
                userId = currentUser?.uid ?: dto.userId,
                userEmail = currentUser?.email ?: dto.userEmail,
                userPhone = currentUser?.phoneNumber ?: dto.userPhone,
                userDisplayName = currentUser?.displayName ?: dto.userDisplayName
            )
            collection.document(docId).set(updatedDto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPendingTrainingPosts(): Flow<List<TrainingAcademyDto>> = callbackFlow {
        val listener = collection
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc -> doc.toObject(TrainingAcademyDto::class.java) }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveTrainingPost(courseId: String): Result<Unit> {
        return try {
            collection.document(courseId).update("isApproved", true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectTrainingPost(courseId: String): Result<Unit> {
        return try {
            collection.document(courseId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get real-time training course posts flow.
     */
    fun getTrainingPostsFlow(): Flow<List<TrainingAcademyDto>> = callbackFlow {
        val listener = collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(TrainingAcademyDto::class.java)
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get single training course post details by ID.
     */
    suspend fun getTrainingPostById(id: String): TrainingAcademyDto? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(TrainingAcademyDto::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
