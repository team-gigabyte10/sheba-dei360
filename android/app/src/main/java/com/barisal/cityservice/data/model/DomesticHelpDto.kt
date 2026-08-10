package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class DomesticHelpDto(
    val id: String = "",
    val title: String = "", // e.g. "অভিজ্ঞ রান্নার বুয়া প্রয়োজন", "ফুল টাইম গৃহকর্মী"
    val subCategory: String = "", // "ফুল-টাইম গৃহকর্মী", "পার্ট-টাইম গৃহকর্মী", "রান্নার বুয়া", "শিশু দেখাশোনা", "বয়স্ক সেবা", "বাসা পরিষ্কার", "অন্যান্য"
    val providerName: String = "", // e.g. "রহিমা খাতুন", "বরিশাল কেয়ার গিভারস"
    val workType: String = "", // "মাসিক", "দৈনিক", "ঘণ্টা চুক্তি"
    val expectedSalary: String = "", // e.g. "৫,০০০ টাকা / মাস"
    val location: String = "",
    val contact: String = "",
    val experience: String = "", // e.g. "৩ বছরের অভিজ্ঞতা"
    val description: String = "",
    val coverImage: String = "", // Base64 data URI
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
