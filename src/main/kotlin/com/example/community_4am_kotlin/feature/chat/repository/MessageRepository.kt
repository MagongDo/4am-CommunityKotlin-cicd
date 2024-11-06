package com.example.community_4am_kotlin.feature.chat.repository

import com.example.community_4am_kotlin.domain.chat.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MessageRepository : JpaRepository<ChatMessage, String>{
    fun deleteByChatRoom_Id(roomId : Long)
}