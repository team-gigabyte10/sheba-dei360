package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.DoctorDto
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

class DoctorRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }

    /**
     * Uploads an image Uri to Firebase Storage under 'doctors' or specified folder path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "doctors"): Result<String> {
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
            Log.e("DoctorRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a doctor document in Firestore under:
     * doctors (collection) -> categoryName (document) -> doctor_list (subcollection) -> docId (document fields)
     */
    suspend fun saveDoctorToFirestore(doctor: DoctorDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = doctor.categoryName.ifBlank { "General" }
        val categoryDocRef = firestore.collection("doctors").document(category)
        val doctorSubCollection = categoryDocRef.collection("doctor_list")

        val docId = if (doctor.id.isNotBlank()) doctor.id else doctorSubCollection.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val doctorToSave = doctor.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: doctor.userId,
            userEmail = currentUser?.email ?: doctor.userEmail,
            userPhone = currentUser?.phoneNumber ?: doctor.userPhone,
            userDisplayName = currentUser?.displayName ?: doctor.userDisplayName
        )

        categoryDocRef.set(
            mapOf("categoryName" to category, "updatedAt" to System.currentTimeMillis()),
            SetOptions.merge()
        )

        doctorSubCollection.document(docId)
            .set(doctorToSave)
            .addOnSuccessListener {
                doctorSubCollection.document(docId).update(
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
                Log.e("DoctorRepository", "Error saving doctor to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved doctors from Firestore.
     */
    fun getDoctorsByCategory(categoryName: String): Flow<List<DoctorDto>> = callbackFlow {
        val query = if (categoryName.isNotBlank() && categoryName != "All" && categoryName != "সব") {
            firestore.collection("doctors").document(categoryName).collection("doctor_list")
        } else {
            firestore.collectionGroup("doctor_list")
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("DoctorRepository", "Error listening to doctor updates: ${error.message}", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val approvedDoctors = snapshot.documents.mapNotNull { doc ->
                    try {
                        val doctor = doc.toObject(DoctorDto::class.java)
                        val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                doc.getBoolean("approved") == true ||
                                doctor?.isApproved == true
                        if (isApprovedBool) {
                            doctor?.copy(isApproved = true)
                        } else null
                    } catch (e: Exception) {
                        Log.e("DoctorRepository", "Failed to parse doctor document: ${e.message}")
                        null
                    }
                }
                trySend(approvedDoctors)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) doctors for Admin Approval.
     */
    fun getPendingDoctors(): Flow<List<DoctorDto>> = callbackFlow {
        val listener = firestore.collectionGroup("doctor_list").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("DoctorRepository", "Error listening to pending doctors: ${error.message}", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                val pendingDoctors = snapshot.documents.mapNotNull { doc ->
                    try {
                        val doctor = doc.toObject(DoctorDto::class.java)
                        val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                doc.getBoolean("approved") == true ||
                                doctor?.isApproved == true
                        if (!isApprovedBool) {
                            doctor?.copy(isApproved = false)
                        } else null
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(pendingDoctors)
            }
        }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves a doctor post in Firestore (sets BOTH isApproved = true and approved = true).
     */
    suspend fun approveDoctor(categoryName: String, docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = categoryName.ifBlank { "General" }
        val updateMap = mapOf<String, Any>(
            "isApproved" to true,
            "approved" to true
        )

        // Primary: Try direct category path (fastest & no index needed)
        val directRef = firestore.collection("doctors").document(category).collection("doctor_list").document(docId)
        directRef.update(updateMap)
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener {
                // Secondary: Fallback to collectionGroup document search if category path differed
                firestore.collectionGroup("doctor_list").get()
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
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Doctor document not found in Firestore")))
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
            }
    }

    /**
     * Rejects/deletes an unapproved doctor post from Firestore.
     */
    suspend fun rejectDoctor(categoryName: String, docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val category = categoryName.ifBlank { "General" }
        val directRef = firestore.collection("doctors").document(category).collection("doctor_list").document(docId)

        directRef.delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener {
                firestore.collectionGroup("doctor_list").get()
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
                            if (continuation.isActive) continuation.resume(Result.failure(Exception("Doctor document not found in Firestore")))
                        }
                    }
                    .addOnFailureListener { e ->
                        if (continuation.isActive) continuation.resume(Result.failure(e))
                    }
            }
    }

    fun getAllDoctors(): Flow<List<DoctorDto>> = getDoctorsByCategory("")
}
