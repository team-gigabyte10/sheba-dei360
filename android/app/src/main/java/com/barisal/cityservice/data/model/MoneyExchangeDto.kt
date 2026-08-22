package com.barisal.cityservice.data.model

import com.google.firebase.firestore.DocumentId

data class MoneyExchangeDto(
    @DocumentId
    val id: String = "",
    val agencyName: String = "",
    val subCategory: String = "",
    val proprietorOrManager: String = "",
    val licenseNo: String = "",
    val availableCurrencies: String = "", // e.g. USD, SAR, MYR, EUR, AED
    val location: String = "", // Area / District (e.g. Barishal Sadar)
    val address: String = "", // Counter / Office Address
    val contact: String = "",
    val whatsapp: String = "",
    val description: String = "",
    val image: String = "", // Base64 or URL
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isApproved: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
