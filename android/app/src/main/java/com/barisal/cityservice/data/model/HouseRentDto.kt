package com.barisal.cityservice.data.model

import com.google.firebase.firestore.PropertyName

data class FlatDetailsDto(
    val houseNo: String = "",
    val levelNo: String = "",   // e.g. "3rd Floor / ৩য় তলা"
    val flatNo: String = "",    // e.g. "Flat B-2"
    val bedrooms: String = "",  // e.g. "3 Bed"
    val bathrooms: String = "", // e.g. "2 Bath"
    val balconies: String = ""  // e.g. "2 Balconies"
)

data class HouseRentDto(
    val id: String = "",
    val title: String = "",
    val houseType: String = "", // e.g. "ফ্ল্যাট ভাড়া", "ব্যাচেলর রুম/সিট", "মেয়েদের মেস", "সাবলেট", "হোস্টেল", "অফিস স্পেস", "দোকান", "গ্যারেজ"
    val rentAmount: String = "",
    val address: String = "",
    val zilla: String = "",
    val thana: String = "",
    val contactInfo: String = "",
    val latLng: String = "",
    val imageUrls: List<String> = emptyList(), // Multiple Base64 photos
    val flatDetails: FlatDetailsDto = FlatDetailsDto(),
    val validityDays: Int = 30, // 7, 15, 30, 60, 90 days
    val expiresAt: Long = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
    val isRented: Boolean = false, // Rent Status: false = Available, true = Rent Completed (ভাড়া হয়ে গেছে)
    val details: String = "",
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    @get:PropertyName("isApproved") @field:PropertyName("isApproved")
    val isApproved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
