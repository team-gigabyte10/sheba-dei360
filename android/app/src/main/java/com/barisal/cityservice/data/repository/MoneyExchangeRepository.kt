package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.barisal.cityservice.data.model.MoneyExchangeDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class MoneyExchangeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("money_exchange_services")

    /**
     * Uploads image Uri to Firebase Storage under 'money_exchange' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "money_exchange"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    suspend fun saveMoneyExchangePost(item: MoneyExchangeDto): Result<Boolean> {
        return try {
            val docRef = collectionRef.document()
            val finalItem = item.copy(id = docRef.id, isApproved = false, timestamp = System.currentTimeMillis())
            docRef.set(finalItem).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getApprovedMoneyExchangePosts(): Flow<List<MoneyExchangeDto>> = callbackFlow {
        val listener = collectionRef
            .whereEqualTo("approved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MoneyExchangeDto::class.java)
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    fun getPendingMoneyExchangePosts(): Flow<List<MoneyExchangeDto>> = callbackFlow {
        val listener = collectionRef
            .whereEqualTo("approved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MoneyExchangeDto::class.java)
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveMoneyExchangePost(id: String): Result<Boolean> {
        return try {
            collectionRef.document(id).update("approved", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectMoneyExchangePost(id: String): Result<Boolean> {
        return try {
            collectionRef.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoneyExchangePostById(id: String): MoneyExchangeDto? {
        return try {
            val doc = collectionRef.document(id).get().await()
            if (doc.exists()) doc.toObject(MoneyExchangeDto::class.java) else null
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
