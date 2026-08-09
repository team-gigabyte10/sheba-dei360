package com.barisal.cityservice.data.repository

import com.barisal.cityservice.data.model.RideDriverDto
import com.barisal.cityservice.data.model.RideRequestDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RideRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val driversCollection = firestore.collection("ride_drivers")
    private val requestsCollection = firestore.collection("ride_requests")

    fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    fun getCurrentUserEmail(): String {
        return auth.currentUser?.email ?: ""
    }

    fun getCurrentUserPhone(): String {
        return auth.currentUser?.phoneNumber ?: ""
    }

    // --- DRIVER METHODS ---

    suspend fun registerDriver(driver: RideDriverDto): Result<Boolean> {
        return try {
            val docId = if (driver.id.isBlank()) driversCollection.document().id else driver.id
            val finalDriver = driver.copy(
                id = docId,
                userId = getCurrentUserId(),
                userEmail = getCurrentUserEmail(),
                userPhone = getCurrentUserPhone(),
                isApproved = false
            )
            driversCollection.document(docId).set(finalDriver).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getDriverProfile(driverId: String): Flow<RideDriverDto?> = callbackFlow {
        if (driverId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = driversCollection.document(driverId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val driver = snapshot?.toObject(RideDriverDto::class.java)
                trySend(driver)
            }
        awaitClose { listener.remove() }
    }

    suspend fun updateDriverOnlineStatus(driverId: String, isOnline: Boolean): Result<Boolean> {
        return try {
            driversCollection.document(driverId).update("isOnline", isOnline).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDriverLocation(driverId: String, lat: Double, lng: Double): Result<Boolean> {
        return try {
            driversCollection.document(driverId).update(
                mapOf(
                    "currentLat" to lat,
                    "currentLng" to lng
                )
            ).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getOnlineDrivers(vehicleType: String = ""): Flow<List<RideDriverDto>> = callbackFlow {
        var query = driversCollection
            .whereEqualTo("isApproved", true)
            .whereEqualTo("isOnline", true)

        if (vehicleType.isNotBlank()) {
            query = query.whereEqualTo("vehicleType", vehicleType)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val drivers = snapshot?.documents?.mapNotNull { it.toObject(RideDriverDto::class.java) } ?: emptyList()
            trySend(drivers)
        }
        awaitClose { listener.remove() }
    }

    // --- CUSTOMER RIDE REQUEST METHODS ---

    suspend fun createRideRequest(request: RideRequestDto): Result<String> {
        return try {
            val docRef = requestsCollection.document()
            val requestId = docRef.id
            val otp = (1000..9999).random().toString()
            val finalRequest = request.copy(
                requestId = requestId,
                customerId = getCurrentUserId(),
                customerName = request.customerName.ifBlank { "প্যাসেঞ্জার / Customer" },
                otpCode = otp,
                status = "PENDING",
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            docRef.set(finalRequest).await()
            Result.success(requestId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun listenToRideRequest(requestId: String): Flow<RideRequestDto?> = callbackFlow {
        if (requestId.isBlank()) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = requestsCollection.document(requestId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val req = snapshot?.toObject(RideRequestDto::class.java)
                trySend(req)
            }
        awaitClose { listener.remove() }
    }

    fun listenToPendingRideRequests(vehicleType: String = ""): Flow<List<RideRequestDto>> = callbackFlow {
        var query = requestsCollection
            .whereEqualTo("status", "PENDING")

        if (vehicleType.isNotBlank()) {
            query = query.whereEqualTo("vehicleType", vehicleType)
        }

        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            val reqs = snapshot?.documents?.mapNotNull { it.toObject(RideRequestDto::class.java) } ?: emptyList()
            trySend(reqs)
        }
        awaitClose { listener.remove() }
    }

    suspend fun acceptRideRequest(requestId: String, driver: RideDriverDto): Result<Boolean> {
        return try {
            requestsCollection.document(requestId).update(
                mapOf(
                    "status" to "ACCEPTED",
                    "assignedDriverId" to driver.id,
                    "assignedDriverName" to driver.name,
                    "assignedDriverPhone" to driver.phone,
                    "assignedDriverPlate" to driver.plateNumber,
                    "assignedDriverVehicle" to driver.vehicleModel,
                    "assignedDriverRating" to driver.rating,
                    "assignedDriverLat" to driver.currentLat,
                    "assignedDriverLng" to driver.currentLng,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRideStatus(requestId: String, status: String): Result<Boolean> {
        return try {
            requestsCollection.document(requestId).update(
                mapOf(
                    "status" to status,
                    "updatedAt" to System.currentTimeMillis()
                )
            ).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelRideRequest(requestId: String): Result<Boolean> {
        return updateRideStatus(requestId, "CANCELLED")
    }

    // --- ADMIN METHODS ---

    fun getPendingDrivers(): Flow<List<RideDriverDto>> = callbackFlow {
        val listener = driversCollection
            .whereEqualTo("isApproved", false)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                val drivers = snapshot?.documents?.mapNotNull { it.toObject(RideDriverDto::class.java) } ?: emptyList()
                trySend(drivers)
            }
        awaitClose { listener.remove() }
    }

    suspend fun approveDriver(driverId: String): Result<Boolean> {
        return try {
            driversCollection.document(driverId).update("isApproved", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun rejectDriver(driverId: String): Result<Boolean> {
        return try {
            driversCollection.document(driverId).delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
