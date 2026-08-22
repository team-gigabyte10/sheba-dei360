package com.barisal.cityservice.data.model

import com.google.firebase.firestore.DocumentId

data class HajjTourDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val categoryKey: String = "hajj", // "hajj" for Hajj & Umrah, "tour" for Tour & Travels
    val subCategory: String = "",
    val agencyName: String = "",
    val proprietorOrManager: String = "",
    val licenseNo: String = "", // e.g. RL-1234 or Hajj License No.
    val packagePrice: String = "",
    val duration: String = "", // e.g. 14 Days / 10 Nights
    val departureLocation: String = "",
    val destination: String = "",
    val location: String = "", // Area / City (e.g. Barishal Sadar)
    val contact: String = "",
    val whatsapp: String = "",
    val description: String = "",
    val bannerImage: String = "", // Base64 or URL
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isApproved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
