package com.barisal.cityservice.core.utils

import android.util.Base64

/**
 * Extension to convert an iconUrl (which might be a base64 data URI)
 * to a type that Coil's AsyncImage or rememberAsyncImagePainter can render natively (e.g. ByteArray).
 */
fun String?.toCoilModel(): Any? {
    if (this == null || this.isBlank()) return null
    if (this.startsWith("data:image/") && this.contains("base64,")) {
        val base64Data = this.substringAfter("base64,")
        return try {
            Base64.decode(base64Data, Base64.DEFAULT)
        } catch (e: Exception) {
            this
        }
    }
    return this
}
