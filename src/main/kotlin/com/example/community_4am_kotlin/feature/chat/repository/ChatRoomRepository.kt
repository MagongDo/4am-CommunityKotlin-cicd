package com.example.community_4am_kotlin.feature.chat.repository

import com.example.Community_4am_Kotlin.domain.chat.ChatRoom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatRoomRepository : JpaRepository<ChatRoom, Long>