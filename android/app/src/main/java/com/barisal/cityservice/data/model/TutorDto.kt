package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class TutorDto(
    val id: String = "",
    val name: String = "",
    val date: String = "",
    val bio: String = "",
    val classRange: String = "",
    val daysPerWeek: String = "",
    val subject: String = "",
    val selectedSubjects: List<String> = emptyList(),
    val salary: String = "",
    val gender: String = "",
    val address: String = "",
    val thana: String = "",
    val phone: String = "",
    val postType: String = "tutor", // "tutor" = পড়াতে চাই, "student" = শিক্ষক চাই
    val profileImageUrl: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
