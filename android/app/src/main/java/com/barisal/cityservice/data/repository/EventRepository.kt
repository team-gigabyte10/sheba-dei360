package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.EventProviderDto
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

class EventRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("event_services") }

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
            Log.e("EventRepository", "Base64 image compression failed: ${e.message}", e)
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
     * Saves an Event Service posting in Firestore collection "event_services".
     */
    suspend fun saveEventService(eventProvider: EventProviderDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (eventProvider.id.isNotBlank()) eventProvider.id else collectionRef.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val itemToSave = eventProvider.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: eventProvider.userId,
            userEmail = currentUser?.email ?: eventProvider.userEmail,
            userPhone = currentUser?.phoneNumber ?: eventProvider.userPhone,
            userDisplayName = currentUser?.displayName ?: eventProvider.userDisplayName
        )

        collectionRef.document(docId).set(itemToSave)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("EventRepository", "Failed to save event service: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Observe approved event services from Firestore.
     */
    fun getEventServicesFlow(): Flow<List<EventProviderDto>> = callbackFlow {
        val listenerRegistration = collectionRef
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventRepository", "Error getting event services: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(EventProviderDto::class.java)
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Observe pending unapproved event services for Admin Approval.
     */
    fun getPendingEventServices(): Flow<List<EventProviderDto>> = callbackFlow {
        val listenerRegistration = collectionRef
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("EventRepository", "Error getting pending event services: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(EventProviderDto::class.java)
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    /**
     * Approve an event service posting by Admin.
     */
    suspend fun approveEventService(id: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(id).update("isApproved", true)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("EventRepository", "Failed to approve event service: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Reject or delete an event service posting by Admin.
     */
    suspend fun rejectEventService(id: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(id).delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e("EventRepository", "Failed to reject event service: ${e.message}", e)
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
