package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class RideDriverDto(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val vehicleType: String = "bike", // "bike", "car", "cng", "pickup", "ambulance"
    val vehicleModel: String = "",
    val plateNumber: String = "",
    val licenseNumber: String = "",
    val rating: Double = 5.0,
    val totalRides: Int = 0,
    val currentLat: Double = 23.8103,
    val currentLng: Double = 90.4125,
    val isOnline: Boolean = false,
    val profileImageUrl: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class RideRequestDto(
    val requestId: String = "",
    val customerId: String = "",
    val customerName: String = "",
    val customerPhone: String = "",
    val pickupAddress: String = "",
    val pickupLat: Double = 0.0,
    val pickupLng: Double = 0.0,
    val dropAddress: String = "",
    val dropLat: Double = 0.0,
    val dropLng: Double = 0.0,
    val vehicleType: String = "bike",
    val estimatedDistanceKm: Double = 0.0,
    val estimatedTimeMins: Int = 0,
    val fareAmount: Int = 0,
    val status: String = "PENDING", // "PENDING", "ACCEPTED", "ARRIVED", "STARTED", "COMPLETED", "CANCELLED"
    val assignedDriverId: String = "",
    val assignedDriverName: String = "",
    val assignedDriverPhone: String = "",
    val assignedDriverPlate: String = "",
    val assignedDriverVehicle: String = "",
    val assignedDriverRating: Double = 5.0,
    val assignedDriverLat: Double = 0.0,
    val assignedDriverLng: Double = 0.0,
    val otpCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
