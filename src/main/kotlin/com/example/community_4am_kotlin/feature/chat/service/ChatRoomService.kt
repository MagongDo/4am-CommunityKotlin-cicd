package com.example.community_4am_kotlin.feature.chat.service

import com.example.Community_4am_Kotlin.domain.chat.ChatRoom
import com.example.Community_4am_Kotlin.feature.chat.dto.CreateChatForm
import jakarta.validation.constraints.Email

interface ChatRoomService {
    fun save(createChatForm: CreateChatForm , email: String)
    fun list(): List<ChatRoom?>?
    fun findByUsername(username: String?): List<ChatRoom?>?
    fun delete(id: Long)
}