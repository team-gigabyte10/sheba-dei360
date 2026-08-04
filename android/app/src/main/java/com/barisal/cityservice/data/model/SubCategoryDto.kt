package com.barisal.cityservice.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data transfer object representing a Sub-Category in Firestore.
 */
data class SubCategoryDto(
    val id: Any? = null,                 // Int or String from Firestore
    val categoryId: Any? = null,         // Int or String (Parent Category ID)
    val nameEn: String = "",
    val nameBn: String = "",
    val iconUrl: String? = null,
    val route: String = "",              // String (Category Name e.g. "Health Services")
    val order: Any? = null,              // Int or String
    val isActive: Boolean = true
) {
    fun getIdAsInt(): Int {
        return when (id) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull() ?: 0
            else -> 0
        }
    }

    fun getCategoryIdAsInt(): Int {
        return when (categoryId) {
            is Number -> categoryId.toInt()
            is String -> categoryId.toIntOrNull() ?: 0
            else -> 0
        }
    }

    fun getOrderAsInt(): Int {
        return when (order) {
            is Number -> order.toInt()
            is String -> order.toIntOrNull() ?: 0
            else -> 0
        }
    }
}

/**
 * UI representation of Sub-Category with helper methods and fallback Material icon support.
 */
data class SubCategoryItem(
    val id: Int,
    val categoryId: Int,
    val nameEn: String,
    val nameBn: String,
    val iconUrl: String? = null,
    val fallbackIcon: ImageVector? = null,
    val route: String
) {
    fun getDisplayName(isBengali: Boolean): String {
        return if (isBengali && nameBn.isNotBlank()) nameBn else nameEn
    }
}
