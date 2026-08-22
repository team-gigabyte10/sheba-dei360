package com.barisal.cityservice.core.utils

import android.util.Base64

/**
 * Extension to convert an image URL or image data string
 * to a type that Coil's AsyncImage or rememberAsyncImagePainter can render natively.
 * - HTTP/HTTPS URLs are passed through directly as Strings for Coil's network loader.
 * - Legacy Base64 data URIs are decoded into ByteArrays as a fallback.
 */
fun String?.toCoilModel(): Any? {
    if (this.isNullOrBlank()) return null
    val trimmed = this.trim()
    if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true)) {
        return trimmed
    }
    if (trimmed.startsWith("data:image/", ignoreCase = true) && trimmed.contains("base64,", ignoreCase = true)) {
        val base64Data = trimmed.substringAfter("base64,")
        return try {
            Base64.decode(base64Data, Base64.DEFAULT)
        } catch (e: Exception) {
            trimmed
        }
    }
    return trimmed
}
