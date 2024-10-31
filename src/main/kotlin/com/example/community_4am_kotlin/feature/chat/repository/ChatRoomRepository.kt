package com.example.community_4am_kotlin.feature.chat.repository

import com.example.community_4am_kotlin.domain.chat.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long>