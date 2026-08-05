package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class ChamberDto(
    val chamberName: String = "",
    val address: String = "",
    val visitingTime: String = ""
)

data class DoctorDto(
    val id: String = "",
    val categoryName: String = "",
    val name: String = "",
    val specialization: String = "",
    val education: String = "",
    val chambers: List<ChamberDto> = emptyList(),
    val treatments: String = "",
    val latLng: String = "",
    val contactInfo: String = "",
    val zilla: String = "",
    val imageUrl: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
