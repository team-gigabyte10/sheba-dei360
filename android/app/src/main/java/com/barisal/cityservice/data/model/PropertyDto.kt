package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class PropertyDto(
    val id: String = "",
    val propertyType: String = "flat", // "flat" = ফ্ল্যাট বিক্রি, "land" = জমি বিক্রি
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val thana: String = "",
    val zilla: String = "",
    val latLng: String = "",
    val price: String = "",
    val phone: String = "",
    val images: List<String> = emptyList(), // Base64 or URLs

    // Flat Attributes
    val roomSize: String = "", // e.g. "1200 sqft"
    val floor: String = "", // e.g. "5th Floor"
    val beds: Int = 0,
    val baths: Int = 0,
    val dining: Int = 0,
    val drawing: Int = 0,
    val balcony: Int = 0,

    // Land Attributes
    val area: String = "", // e.g. "5 Katha" / "১০ শতাংশ"
    val roadWidth: String = "", // e.g. "20 ft"
    val landType: String = "", // e.g. "Residential" / "Commercial" / "Agricultural"
    val registrationStatus: String = "", // e.g. "All Papers Up-to-Date"

    // Metadata
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
