package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class EventProviderDto(
    val id: String = "",
    val name: String = "",           // Provider / Business Name
    val ownerName: String = "",      // Owner / Manager Name
    val categoryName: String = "",   // Category e.g. "ক্যাটারিং সার্ভিস", "ফটোগ্রাফার"
    val phone: String = "",          // Primary Phone
    val whatsappPhone: String = "",  // WhatsApp Phone
    val zilla: String = "বরিশাল",
    val thana: String = "",
    val addressBn: String = "",
    val addressEn: String = "",
    val latLng: String = "",         // "lat,lng" e.g. "22.7010,90.3535"
    val startingPackage: Int = 15000,// Starting package price in BDT
    val priceUnit: String = "ইভেন্ট", // "ইভেন্ট", "জন (প্লেট)", "দিন"
    val experienceYears: Int = 3,
    val rating: Double = 5.0,
    val reviewCount: Int = 1,
    val imageUrls: List<String> = emptyList(), // Base64 or uploaded image URIs
    val videoUrl: String = "",       // Promo video link
    val features: List<String> = emptyList(),  // e.g. ["AC Venue", "Sound System", "Outdoor Service"]
    val categoryDetails: Map<String, String> = emptyMap(), // Dynamic category specific fields
    val details: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
