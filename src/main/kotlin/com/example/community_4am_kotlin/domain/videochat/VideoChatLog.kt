package com.example.community_4am_Kotlin.domain.videochat

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
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var video_chat_id: String,
    var user_id:Long,
    var other_user_id:Long,

    @CreatedDate
    var video_chat_created_at: LocalDateTime,
    @LastModifiedDate
    var video_chat_modified_at: LocalDateTime
    ){
}