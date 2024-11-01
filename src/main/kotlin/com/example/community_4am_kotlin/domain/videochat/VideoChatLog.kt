package com.example.community_4am_kotlin.domain.videochat

import jakarta.persistence.*
import jakarta.transaction.Transactional
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name="video_chat_log")
@Transactional
data class VideoChatLog (

    @Id
    var videoChatId: String,
    var userId:Long,
    var otherUserId:Long,

    @CreatedDate
    var videoChatCreatedAt: LocalDateTime? = null,

    var videoChatEndAt: LocalDateTime? = null,
    ){
}