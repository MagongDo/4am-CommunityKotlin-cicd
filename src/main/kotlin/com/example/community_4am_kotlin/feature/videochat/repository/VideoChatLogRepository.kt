package com.example.community_4am_kotlin.feature.videochat.repository

import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface  VideoChatLogRepository: JpaRepository<VideoChatLog, String> {

    fun findAllByVideoChatId(roomId: String): List<VideoChatLog>

}