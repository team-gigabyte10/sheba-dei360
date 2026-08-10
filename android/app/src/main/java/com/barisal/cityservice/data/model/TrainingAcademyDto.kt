package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class TrainingAcademyDto(
    val id: String = "",
    val title: String = "", // e.g. Car Driving Master Class, Computer Office Application
    val subCategory: String = "", // Car Driving Training, Computer Training, Technical Training, Language Learning, Job & Career, Others
    val academyName: String = "", // e.g. Barisal IT & Driving Academy
    val courseFee: String = "", // e.g. 5000
    val duration: String = "", // e.g. 3 Months
    val contact: String = "",
    val trainerName: String = "",
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
