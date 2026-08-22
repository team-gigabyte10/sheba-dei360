package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.HealthServiceDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class HealthServiceRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Uploads an image Uri to Firebase Storage under 'health_services' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "health_services"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

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
            Log.e("HealthServiceRepo", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a health service document in Firestore under:
     * health_services (collection) -> categoryKey (document) -> service_list (subcollection) -> docId (document fields)
     */
    suspend fun saveHealthServiceToFirestore(service: HealthServiceDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = service.categoryKey.ifBlank { "general" }
        val categoryDocRef = firestore.collection("health_services").document(category)
        val serviceSubCollection = categoryDocRef.collection("service_list")

        val docId = if (service.id.isNotBlank()) service.id else serviceSubCollection.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val serviceToSave = service.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: service.userId,
            userEmail = currentUser?.email ?: service.userEmail,
            userPhone = currentUser?.phoneNumber ?: service.userPhone,
            userDisplayName = currentUser?.displayName ?: service.userDisplayName
        )

        categoryDocRef.set(
            mapOf("categoryKey" to category, "updatedAt" to System.currentTimeMillis()),
            SetOptions.merge()
        )

        serviceSubCollection.document(docId)
            .set(serviceToSave)
            .addOnSuccessListener {
                serviceSubCollection.document(docId).update(
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
                Log.e("HealthServiceRepo", "Error saving health service to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved health services from Firestore.
     */
    fun getHealthServicesByCategory(categoryKey: String): Flow<List<HealthServiceDto>> = callbackFlow {
        val query = if (categoryKey.isNotBlank() && categoryKey != "all") {
            firestore.collection("health_services").document(categoryKey).collection("service_list")
        } else {
            firestore.collectionGroup("service_list")
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("HealthServiceRepo", "Error listening to health service updates: ${error.message}", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val approvedServices = snapshot.documents.mapNotNull { doc ->
                    try {
                        val item = doc.toObject(HealthServiceDto::class.java)
                        val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                doc.getBoolean("approved") == true ||
                                item?.isApproved == true
                        if (isApprovedBool) {
                            item?.copy(isApproved = true)
                        } else null
                    } catch (e: Exception) {
                        Log.e("HealthServiceRepo", "Failed to parse health service document: ${e.message}")
                        null
                    }
                }
                trySend(approvedServices)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) health services for Admin Approval.
     */
    fun getPendingHealthServices(): Flow<List<HealthServiceDto>> = callbackFlow {
        val listener = firestore.collectionGroup("service_list").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("HealthServiceRepo", "Error listening to pending health services: ${error.message}", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val pendingServices = snapshot.documents.mapNotNull { doc ->
                    try {
                        val item = doc.toObject(HealthServiceDto::class.java)
                        val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                doc.getBoolean("approved") == true ||
                                item?.isApproved == true
                        if (!isApprovedBool) {
                            item?.copy(isApproved = false)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(pendingServices)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves a health service post in Firestore (sets BOTH isApproved = true and approved = true).
     */
    suspend fun approveHealthService(categoryKey: String, docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = categoryKey.ifBlank { "general" }
        val updateMap = mapOf<String, Any>(
            "isApproved" to true,
            "approved" to true
        )

        val directRef = firestore.collection("health_services").document(category).collection("service_list").document(docId)
        directRef.update(updateMap)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener {
                firestore.collectionGroup("service_list").get()
                    .addOnSuccessListener { snapshot ->
                        val matchingDoc = snapshot.documents.firstOrNull { it.id == docId || it.getString("id") == docId }
                        if (matchingDoc != null) {
                            matchingDoc.reference.update(updateMap)
                                .addOnSuccessListener {
                                    if (continuation.isActive) continuation.resume(Result.success(true))
                                }
                                .addOnFailureListener { e ->
                                    if (continuation.isActive) continuation.resume(Result.failure(e))
                                }
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Health Service document not found in Firestore")))
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
            }
    }

    /**
     * Rejects/deletes an unapproved health service post from Firestore.
     */
    suspend fun rejectHealthService(categoryKey: String, docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = categoryKey.ifBlank { "general" }
        val directRef = firestore.collection("health_services").document(category).collection("service_list").document(docId)

        directRef.delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener {
                firestore.collectionGroup("service_list").get()
                    .addOnSuccessListener { snapshot ->
                        val matchingDoc = snapshot.documents.firstOrNull { it.id == docId || it.getString("id") == docId }
                        if (matchingDoc != null) {
                            matchingDoc.reference.delete()
                                .addOnSuccessListener {
                                    if (continuation.isActive) continuation.resume(Result.success(true))
                                }
                                .addOnFailureListener { e ->
                                    if (continuation.isActive) continuation.resume(Result.failure(e))
                                }
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Health Service document not found in Firestore")))
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
            }
    }

    fun getAllHealthServices(): Flow<List<HealthServiceDto>> = getHealthServicesByCategory("")
}
