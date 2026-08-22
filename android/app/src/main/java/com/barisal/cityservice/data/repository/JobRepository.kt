package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.barisal.cityservice.data.model.JobDto
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

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class JobRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = firestore.collection("jobs")

    /**
     * Uploads image Uri to Firebase Storage under 'jobs' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "jobs"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

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
     * Save job circular post to Firestore. Default isApproved = false.
     */
    suspend fun saveJob(job: JobDto): Result<Boolean> {
        return try {
            val docId = if (job.id.isNotBlank()) job.id else UUID.randomUUID().toString()
            val currentUser = auth.currentUser
            val updatedJob = job.copy(
                id = docId,
                isApproved = false, // Always requires admin approval
                userId = currentUser?.uid ?: job.userId,
                userEmail = currentUser?.email ?: job.userEmail,
                userDisplayName = currentUser?.displayName ?: job.userDisplayName
            )
            collection.document(docId).set(updatedJob).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get approved jobs flow for public listing screen.
     */
    fun getApprovedJobs(): Flow<List<JobDto>> = callbackFlow {
        val listener = collection
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(JobDto::class.java)
                        .sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get pending jobs flow for admin approval panel.
     */
    fun getPendingJobs(): Flow<List<JobDto>> = callbackFlow {
        val listener = collection
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(JobDto::class.java)
                        .sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Approve a job post.
     */
    suspend fun approveJob(jobId: String): Result<Boolean> {
        return try {
            collection.document(jobId).update("isApproved", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reject/delete a job post.
     */
    suspend fun rejectJob(jobId: String): Result<Boolean> {
        return try {
            collection.document(jobId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
