package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class RentCarDto(
    val id: String = "",
    val title: String = "", // e.g. Toyota Noah Microbus Rental
    val subCategory: String = "", // Private Car, Microbus, Pickup, Truck, Ride Sharing, Van Vara, Auto Vara
    val driverName: String = "",
    val contact: String = "",
    val price: String = "", // e.g. 3500
    val priceUnit: String = "per_day", // per_hour, per_day, trip
    val vehicleModel: String = "", // e.g. Toyota Noah 2018
    val seatingCapacity: String = "", // e.g. 7 Persons
    val hasAC: Boolean = true,
    val address: String = "",
    val zilla: String = "বরিশাল",
    val thana: String = "",
    val coverImage: String = "", // Base64 data URI or image URL
    val images: List<String> = emptyList(),
    val isApproved: Boolean = false,
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
