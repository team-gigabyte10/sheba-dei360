package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class HotelRoomDto(
    val id: String = "",
    val roomType: String = "", // e.g. Single AC, Double Deluxe, Family Suite
    val pricePerNight: String = "",
    val bedType: String = "", // Single, King, Queen
    val capacity: String = "", // e.g. 2 Persons
    val amenities: List<String> = emptyList(), // WiFi, AC, TV, Breakfast included
    val roomImages: List<String> = emptyList() // Base64 data URIs or image URLs
)

@IgnoreExtraProperties
data class HotelDto(
    val id: String = "",
    val name: String = "",
    val type: String = "", // e.g. 3 Star, Residential, Resort
    val address: String = "",
    val zilla: String = "বরিশাল",
    val thana: String = "",
    val contact: String = "",
    val pricePerNight: String = "",
    val rating: String = "4.5",
    val reviewsCount: String = "(0)",
    val coverImage: String = "",
    val hotelImages: List<String> = emptyList(),
    val rooms: List<HotelRoomDto> = emptyList(),
    val isApproved: Boolean = false,
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
