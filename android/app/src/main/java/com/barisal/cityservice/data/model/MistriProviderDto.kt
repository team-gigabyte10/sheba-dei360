package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class MistriProviderDto(
    val id: String = "",
    val name: String = "",
    val categoryName: String = "",
    val phone: String = "",
    val whatsapp: String = "",
    val experienceYears: Int = 0,
    val minCharge: Int = 0,
    val pricingUnit: String = "প্রতি সার্ভিস", // e.g. "প্রতি ঘণ্টা", "প্রতি দিন", "প্রতি সার্ভিস"
    val addressBn: String = "",
    val addressEn: String = "",
    val zilla: String = "",
    val latLng: String = "",
    val description: String = "",
    val profileImageUrl: String = "",
    val workSampleImages: List<String> = emptyList(),
    val categorySpecificFields: Map<String, String> = emptyMap(),
    val rating: Double = 5.0,
    val reviewCount: Int = 0,
    val isAvailable: Boolean = true,
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
