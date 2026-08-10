package com.barisal.cityservice.data.model

import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class MenuItemDto(
    val id: String = "",
    val itemName: String = "",
    val price: String = "",
    val description: String = "",
    val category: String = "", // e.g. Starters, Main Course, Drinks, Desserts
    val imageUrl: String = "" // Base64 data URI or image URL
)

@IgnoreExtraProperties
data class RestaurantDto(
    val id: String = "",
    val name: String = "",
    val cuisineType: String = "", // e.g. Bengali Food, Biriyani, Fast Food, Chinese
    val address: String = "",
    val zilla: String = "বরিশাল",
    val thana: String = "",
    val contact: String = "",
    val deliveryAvailable: Boolean = true,
    val rating: String = "4.6",
    val reviewsCount: String = "(0)",
    val coverImage: String = "",
    val restaurantImages: List<String> = emptyList(),
    val menuItems: List<MenuItemDto> = emptyList(),
    val isApproved: Boolean = false,
    val userId: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val userDisplayName: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Long = System.currentTimeMillis()
)
