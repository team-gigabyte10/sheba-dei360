package com.barisal.cityservice.data.repository

import android.util.Log
import com.barisal.cityservice.data.model.BloodDonorDto
import com.barisal.cityservice.data.model.BloodRequestDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class BloodRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val donorCollection by lazy { firestore.collection("blood_donors") }
    private val requestCollection by lazy { firestore.collection("blood_requests") }

    /**
     * Saves or updates a blood donor profile in Firestore collection "blood_donors".
     */
    suspend fun saveDonorToFirestore(donor: BloodDonorDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (donor.id.isNotBlank()) donor.id else donorCollection.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val donorToSave = donor.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: donor.userId,
            userEmail = currentUser?.email ?: donor.userEmail,
            userPhone = currentUser?.phoneNumber ?: donor.userPhone,
            userDisplayName = currentUser?.displayName ?: donor.userDisplayName
        )

        donorCollection.document(docId)
            .set(donorToSave)
            .addOnSuccessListener {
                donorCollection.document(docId).update(
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
                Log.e("BloodRepository", "Error saving blood donor to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved blood donors from Firestore.
     */
    fun getBloodDonors(): Flow<List<BloodDonorDto>> = callbackFlow {
        val listener = donorCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BloodRepository", "Error listening to blood donors: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val approvedDonors = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(BloodDonorDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true
                            if (isApprovedBool) {
                                item?.copy(isApproved = true)
                            } else null
                        } catch (e: Exception) {
                            Log.e("BloodRepository", "Failed to parse blood donor document: ${e.message}")
                            null
                        }
                    }
                    trySend(approvedDonors)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) blood donors for Admin Approval.
     */
    fun getPendingBloodDonors(): Flow<List<BloodDonorDto>> = callbackFlow {
        val listener = donorCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BloodRepository", "Error listening to pending blood donors: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingDonors = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(BloodDonorDto::class.java)
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
                    trySend(pendingDonors)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves a blood donor profile in Firestore (sets BOTH isApproved = true and approved = true).
     */
    suspend fun approveBloodDonor(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        donorCollection.document(docId)
            .update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true
                )
            )
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Rejects/deletes an unapproved blood donor profile from Firestore.
     */
    suspend fun rejectBloodDonor(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        donorCollection.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Saves or updates an urgent blood request in Firestore collection "blood_requests".
     */
    suspend fun saveRequestToFirestore(request: BloodRequestDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (request.id.isNotBlank()) request.id else requestCollection.document().id
        val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        val requestToSave = request.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: request.userId,
            userEmail = currentUser?.email ?: request.userEmail,
            userPhone = currentUser?.phoneNumber ?: request.userPhone,
            userDisplayName = currentUser?.displayName ?: request.userDisplayName
        )

        requestCollection.document(docId)
            .set(requestToSave)
            .addOnSuccessListener {
                requestCollection.document(docId).update(
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
                Log.e("BloodRepository", "Error saving blood request to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved urgent blood requests from Firestore.
     */
    fun getBloodRequests(): Flow<List<BloodRequestDto>> = callbackFlow {
        val listener = requestCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BloodRepository", "Error listening to blood requests: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val approvedRequests = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(BloodRequestDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true
                            if (isApprovedBool) {
                                item?.copy(isApproved = true)
                            } else null
                        } catch (e: Exception) {
                            Log.e("BloodRepository", "Failed to parse blood request document: ${e.message}")
                            null
                        }
                    }
                    trySend(approvedRequests)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) urgent blood requests for Admin Approval.
     */
    fun getPendingBloodRequests(): Flow<List<BloodRequestDto>> = callbackFlow {
        val listener = requestCollection.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("BloodRepository", "Error listening to pending blood requests: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingRequests = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(BloodRequestDto::class.java)
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
                    trySend(pendingRequests)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves an urgent blood request post in Firestore (sets BOTH isApproved = true and approved = true).
     */
    suspend fun approveBloodRequest(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        requestCollection.document(docId)
            .update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true
                )
            )
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Rejects/deletes an unapproved urgent blood request post from Firestore.
     */
    suspend fun rejectBloodRequest(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        requestCollection.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
