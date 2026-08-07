package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class HealthServiceDto(
    val id: String = "",
    val categoryKey: String = "", // hospital, diagnostic, ambulance, pharmacy, blood, home_care
    val name: String = "",
    val type: String = "", // e.g. "Government Hospital", "ICU Ambulance", "O+ Blood Donor"
    val address: String = "",
    val zilla: String = "",
    val contactInfo: String = "",
    val latLng: String = "",
    val imageUrl: String = "",
    val details: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
