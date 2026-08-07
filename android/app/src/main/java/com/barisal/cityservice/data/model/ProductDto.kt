package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class ProductDto(
    val id: String = "",
    val productName: String = "",
    val category: String = "",
    val condition: String = "Used", // "New" / "Used" or "নতুন" / "পুরাতন"
    val price: String = "",
    val description: String = "",
    val address: String = "",
    val contactInfo: String = "",
    val latLng: String = "",
    val imageUrls: List<String> = emptyList(), // Base64 data URIs or image URLs
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
