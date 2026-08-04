package com.barisal.cityservice.data.model

import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data transfer object representing a Category fetched from Firestore or local fallback.
 */
data class CategoryDto(
    val id: Any? = null,
    val nameEn: String = "",
    val nameBn: String = "",
    val iconUrl: String? = null,
    val driveFileId: String? = null,
    val route: String = "",
    val order: Any? = null,
    val isActive: Boolean = true
) {
    fun getIdAsInt(): Int {
        return when (id) {
            is Number -> id.toInt()
            is String -> id.toIntOrNull() ?: kotlin.math.abs(id.hashCode())
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
 * UI representation of Category with bilingual helper methods and fallback Material icon support.
 */
data class CategoryItem(
    val id: Int,
    val nameEn: String,
    val nameBn: String,
    val iconUrl: String? = null,
    val fallbackIcon: ImageVector? = null,
    val route: String = ""
) {
    fun getDisplayName(isBengali: Boolean): String {
        return if (isBengali && nameBn.isNotBlank()) nameBn else nameEn
    }
}
