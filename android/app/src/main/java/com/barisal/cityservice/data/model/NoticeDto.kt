package com.barisal.cityservice.data.model

import com.google.firebase.firestore.DocumentId

data class NoticeDto(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val content: String = "",
    val date: String = "", // e.g. "১১ আগস্ট ২০২৬" or formatted date
    val priority: String = "normal", // "urgent" or "normal"
    val isPinned: Boolean = false,
    val publisher: String = "Admin",
    val timestamp: Long = System.currentTimeMillis()
)
