package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class LegalServiceDto(
    val id: String = "",
    val title: String = "", // e.g. "হাইকোর্টের অভিজ্ঞ আইনজীবী", "লাইসেন্সপ্রাপ্ত সাব-রেজিস্ট্রি দলিল লেখক"
    val categoryKey: String = "legal", // "legal" for "আইনি সেবা", "deed_amin" for "দলিল লেখক/আমিন"
    val subCategory: String = "", // e.g. "দেওয়ানী মামলা", "ফৌজদারী মামলা", "জমি রেজিস্ট্রেশন/দলিল", "আমিন/জমি পরিমাপ"
    val name: String = "", // Practitioner / Service Provider Name
    val designation: String = "", // e.g. "অ্যাডভোকেট, জজ কোর্ট", "সার্টিফাইড জমি পরিমাপক (আমিন)"
    val chamberOrOffice: String = "", // Chamber / Office Address
    val experience: String = "", // e.g. "১০ বছরের অভিজ্ঞতা"
    val feeInfo: String = "", // e.g. "আলোচনা সাপেক্ষে", "৳ ৫০০ থেকে শুরু"
    val location: String = "", // e.g. "সদর, বরিশাল"
    val contact: String = "",
    val whatsapp: String = "",
    val description: String = "",
    val profileImage: String = "", // Base64 data URI
    val isApproved: Boolean = false,
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
