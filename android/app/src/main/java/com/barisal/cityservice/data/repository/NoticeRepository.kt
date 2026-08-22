package com.barisal.cityservice.data.repository

import android.content.Context
import android.net.Uri
import com.barisal.cityservice.core.utils.FirebaseStorageManager
import com.barisal.cityservice.data.model.NoticeDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class NoticeRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collectionRef = firestore.collection("admin_notices")

    /**
     * Uploads notice attachments/documents/images to Firebase Storage
     * and returns the public download URL string.
     */
    suspend fun uploadNoticeFile(context: Context, uri: Uri): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, "notices/attachments")
    }

    suspend fun saveNotice(notice: NoticeDto): Result<Boolean> {
        return try {
            val docRef = collectionRef.document()
            val finalNotice = notice.copy(id = docRef.id, timestamp = System.currentTimeMillis())
            docRef.set(finalNotice).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllNotices(): Flow<List<NoticeDto>> = callbackFlow {
        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val list = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(NoticeDto::class.java)
                }.sortedWith(compareByDescending<NoticeDto> { it.isPinned }.thenByDescending { it.timestamp })
                trySend(list)
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun deleteNotice(id: String): Result<Boolean> {
        return try {
            collectionRef.document(id).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
