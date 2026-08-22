package com.barisal.cityservice.core.utils

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

object FirebaseStorageManager {

    private const val TAG = "FirebaseStorageManager"
    private val storage: FirebaseStorage get() = FirebaseStorage.getInstance()

    /**
     * Uploads a single file (image, PDF, doc, audio, etc.) from an Android Uri to Firebase Storage
     * and returns the public download URL string reference.
     *
     * @param context Context used to resolve mime type if needed
     * @param uri Uri of the local file or image
     * @param folderPath Storage directory path (e.g., "hotels", "products", "documents")
     * @param customFileName Optional custom file name. If null, a unique UUID-based name is generated.
     * @param onProgress Optional progress listener returning values between 0.0f and 1.0f
     */
    suspend fun uploadFile(
        context: Context,
        uri: Uri,
        folderPath: String,
        customFileName: String? = null,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = customFileName ?: "${UUID.randomUUID()}_${System.currentTimeMillis()}"
            val storageRef: StorageReference = storage.reference.child(folderPath).child(fileName)

            val uploadTask = storageRef.putFile(uri)
            if (onProgress != null) {
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()).coerceIn(0f, 1f)
                    onProgress(progress)
                }
            }

            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading file to Firebase Storage ($folderPath): ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Uploads raw ByteArray data (e.g. compressed image bytes) to Firebase Storage
     * and returns the public download URL string.
     */
    suspend fun uploadBytes(
        bytes: ByteArray,
        folderPath: String,
        customFileName: String? = null,
        onProgress: ((Float) -> Unit)? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val fileName = customFileName ?: "${UUID.randomUUID()}_${System.currentTimeMillis()}.jpg"
            val storageRef: StorageReference = storage.reference.child(folderPath).child(fileName)

            val uploadTask = storageRef.putBytes(bytes)
            if (onProgress != null) {
                uploadTask.addOnProgressListener { taskSnapshot ->
                    val progress = (taskSnapshot.bytesTransferred.toFloat() / taskSnapshot.totalByteCount.toFloat()).coerceIn(0f, 1f)
                    onProgress(progress)
                }
            }

            uploadTask.await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            Result.success(downloadUrl)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading bytes to Firebase Storage ($folderPath): ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Uploads multiple Uris concurrently to Firebase Storage under the given folder path,
     * returning a list of download URL string references.
     */
    suspend fun uploadMultipleFiles(
        context: Context,
        uris: List<Uri>,
        folderPath: String,
        onProgress: ((Float) -> Unit)? = null
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            if (uris.isEmpty()) return@withContext Result.success(emptyList())

            val progressTracker = FloatArray(uris.size) { 0f }

            val deferredUrls = uris.mapIndexed { index, uri ->
                async {
                    uploadFile(
                        context = context,
                        uri = uri,
                        folderPath = folderPath,
                        onProgress = { fileProgress ->
                            progressTracker[index] = fileProgress
                            val totalProgress = progressTracker.sum() / uris.size
                            onProgress?.invoke(totalProgress)
                        }
                    )
                }
            }

            val results = deferredUrls.awaitAll()
            val failure = results.firstOrNull { it.isFailure }
            if (failure != null) {
                return@withContext Result.failure(failure.exceptionOrNull() ?: Exception("Failed uploading one or more files"))
            }

            val urls = results.mapNotNull { it.getOrNull() }
            Result.success(urls)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading multiple files ($folderPath): ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a file from Firebase Storage given its HTTPS download URL reference.
     */
    suspend fun deleteFileByUrl(fileUrl: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            if (fileUrl.isBlank() || !fileUrl.startsWith("http")) {
                return@withContext Result.success(true)
            }
            val storageRef = storage.getReferenceFromUrl(fileUrl)
            storageRef.delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting file from Firebase Storage ($fileUrl): ${e.message}", e)
            Result.failure(e)
        }
    }
}
