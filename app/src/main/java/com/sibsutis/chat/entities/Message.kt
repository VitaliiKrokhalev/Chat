package com.sibsutis.chat.entities

data class Message(
    val topicId: String,
    val id: String,
    val content: String = "",
    val isMine: Boolean = false,
    val isMedia: Boolean = false
)