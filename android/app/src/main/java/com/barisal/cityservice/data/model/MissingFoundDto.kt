package com.barisal.cityservice.data.model

import com.google.firebase.firestore.DocumentId

data class MissingFoundDto(
    @DocumentId
    val id: String = "",
    val noticeType: String = "missing", // "missing" for নিখোঁজ, "found" for পাওয়া গেছে
    val title: String = "",
    val subCategory: String = "", // e.g. Person, Child, Documents, Wallet, Phone, etc.
    val nameOrItem: String = "",
    val ageOrDetails: String = "", // Age if person, or color/model if object
    val incidentDate: String = "", // Date when missing or found
    val location: String = "", // Area / Location of incident
    val contact: String = "",
    val whatsapp: String = "",
    val rewardOrNote: String = "", // Reward info or special note
    val description: String = "",
    val photo: String = "", // Base64 or URL
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isApproved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
