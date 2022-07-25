package com.sibsutis.chat.entities

import android.graphics.Bitmap

data class Conversation(
    val userId: String,
    val name: String,
    val avatar: Bitmap,
    val lastMessage: String
)