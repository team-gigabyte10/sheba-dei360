package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class BloodDonorDto(
    val id: String = "",
    val name: String = "",
    val bloodGroup: String = "", // e.g. A+, A-, B+, B-, O+, O-, AB+, AB-
    val lastDonation: String = "",
    val zilla: String = "",
    val thana: String = "",
    val address: String = "",
    val contactInfo: String = "",
    val details: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

data class BloodRequestDto(
    val id: String = "",
    val patientName: String = "",
    val bloodGroup: String = "",
    val bagsNeeded: String = "1",
    val hospitalName: String = "",
    val zilla: String = "",
    val thana: String = "",
    val requiredDate: String = "",
    val contactInfo: String = "",
    val details: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
