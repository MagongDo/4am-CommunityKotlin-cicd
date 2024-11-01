package com.example.community_4am_kotlin.feature.chat.service

import com.example.community_4am_kotlin.domain.chat.ChatRoom
import com.example.community_4am_kotlin.feature.chat.dto.CreateChatForm

interface ChatRoomService {
    fun save(createChatForm: CreateChatForm, email: String)
    fun list(): List<ChatRoom?>?
    fun findByUsername(username: String?): List<ChatRoom?>?
    fun delete(id: Long)
}