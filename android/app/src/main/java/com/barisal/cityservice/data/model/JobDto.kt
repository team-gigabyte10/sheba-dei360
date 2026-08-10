package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class JobDto(
    val id: String = "",
    val title: String = "", // e.g. "কম্পিউটার অপারেটর", "শো-রুম সেলস এক্সিকিউটিভ"
    val subCategory: String = "", // "প্রতিষ্ঠানে চাকরি", "শো-রুমে চাকরি", "দোকানে চাকরি", "অন্যান্য চাকরি"
    val organizationName: String = "", // e.g. "বরিশাল ডিজিটাল হাব", "এপেক্স শোরুম"
    val jobType: String = "", // "ফুল টাইম", "পার্ট টাইম", "চুক্তিভিত্তিক"
    val salary: String = "", // e.g. "১৫,০০০ - ২০,০০০ টাকা"
    val location: String = "",
    val contact: String = "",
    val experience: String = "", // e.g. "১ বছরের অভিজ্ঞতা"
    val educationalRequirement: String = "", // e.g. "এইচএসসি / ডিগ্রি"
    val description: String = "",
    val deadline: String = "", // e.g. "২৫ আগস্ট, ২০২৬"
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
