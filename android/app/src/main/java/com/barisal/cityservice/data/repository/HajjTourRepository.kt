package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.barisal.cityservice.data.model.HajjTourDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class HajjTourRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("hajj_tour_services")

    /**
     * Uploads image Uri to Firebase Storage under 'hajj_tours' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "hajj_tours"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    suspend fun saveHajjTourPost(item: HajjTourDto): Result<Boolean> {
        return try {
            val docRef = collectionRef.document()
            val finalItem = item.copy(id = docRef.id, isApproved = false, timestamp = System.currentTimeMillis())
            docRef.set(finalItem).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getApprovedHajjTourPosts(categoryKey: String = ""): Flow<List<HajjTourDto>> = callbackFlow {
        var query: Query = collectionRef.whereEqualTo("approved", true)
        if (categoryKey.isNotBlank()) {
            query = query.whereEqualTo("categoryKey", categoryKey)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(HajjTourDto::class.java)
                }.sortedByDescending { it.timestamp }
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    fun getPendingHajjTourPosts(): Flow<List<HajjTourDto>> = callbackFlow {
        val listener = collectionRef
            .whereEqualTo("approved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(HajjTourDto::class.java)
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveHajjTourPost(id: String): Result<Boolean> {
        return try {
            collectionRef.document(id).update("approved", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectHajjTourPost(id: String): Result<Boolean> {
        return try {
            collectionRef.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getHajjTourPostById(id: String): HajjTourDto? {
        return try {
            val doc = collectionRef.document(id).get().await()
            if (doc.exists()) doc.toObject(HajjTourDto::class.java) else null
        } catch (e: Exception) {
            null
        }
    }

    fun compressImageToBase64(context: Context, imageUri: Uri): Result<String> {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) return Result.failure(Exception("Failed to decode image"))

            val scaledBitmap = scaleBitmapToMax(bitmap, 800)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
            val byteArray = outputStream.toByteArray()
            val base64String = Base64.encodeToString(byteArray, Base64.DEFAULT)
            Result.success(base64String)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun scaleBitmapToMax(bitmap: Bitmap, maxDimension: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        if (width <= maxDimension && height <= maxDimension) return bitmap
        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int
        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}
