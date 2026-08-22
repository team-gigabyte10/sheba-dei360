package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.barisal.cityservice.data.model.LegalServiceDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.UUID

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class LegalServiceRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = firestore.collection("legal_services")

    /**
     * Uploads image Uri to Firebase Storage under 'legal_services' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "legal_services"): Result<String> {
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
     * Save legal service post to Firestore. Default isApproved = false.
     */
    suspend fun saveLegalService(service: LegalServiceDto): Result<Boolean> {
        return try {
            val docId = if (service.id.isNotBlank()) service.id else UUID.randomUUID().toString()
            val currentUser = auth.currentUser
            val updatedService = service.copy(
                id = docId,
                isApproved = false, // Always requires admin approval
                userId = currentUser?.uid ?: service.userId,
                userEmail = currentUser?.email ?: service.userEmail,
                userDisplayName = currentUser?.displayName ?: service.userDisplayName
            )
            collection.document(docId).set(updatedService).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get approved legal services flow for public listing screen.
     * Optionally filter by categoryKey ("legal" or "deed_amin").
     */
    fun getApprovedLegalServices(categoryKey: String? = null): Flow<List<LegalServiceDto>> = callbackFlow {
        val listener = collection
            .whereEqualTo("isApproved", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    var list = snapshot.toObjects(LegalServiceDto::class.java)
                    if (!categoryKey.isNullOrBlank()) {
                        list = list.filter { it.categoryKey == categoryKey }
                    }
                    list = list.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Get single legal service by ID.
     */
    suspend fun getLegalServiceById(serviceId: String): LegalServiceDto? {
        return try {
            val doc = collection.document(serviceId).get().await()
            doc.toObject(LegalServiceDto::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Get pending legal services flow for admin approval panel.
     */
    fun getPendingLegalServices(): Flow<List<LegalServiceDto>> = callbackFlow {
        val listener = collection
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.toObjects(LegalServiceDto::class.java)
                        .sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Approve a legal service post.
     */
    suspend fun approveLegalService(serviceId: String): Result<Boolean> {
        return try {
            collection.document(serviceId).update("isApproved", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Reject/delete a legal service post.
     */
    suspend fun rejectLegalService(serviceId: String): Result<Boolean> {
        return try {
            collection.document(serviceId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
