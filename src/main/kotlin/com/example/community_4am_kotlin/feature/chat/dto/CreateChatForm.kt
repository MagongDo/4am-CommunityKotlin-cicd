package com.example.Community_4am_Kotlin.feature.chat.dto

import com.example.Community_4am_Kotlin.domain.chat.ChatRoom
import java.time.LocalDateTime

data class CreateChatForm(
    var roomName: String,
    var email: String,
    var description: String
) {
    fun toEntity(email: String): ChatRoom {
        return ChatRoom(
            id = null,
            roomName = roomName,
            email = email,
            description = description,
            createdDate = LocalDateTime.now()
        )
    }
}