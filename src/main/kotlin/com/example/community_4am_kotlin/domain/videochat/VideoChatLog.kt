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
data class VideoChatLog (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var videoChatId: String,
    var userId:Long? = null,
    var otherUserId:Long?,

    @CreatedDate
    var videoChatCreateAt: LocalDateTime? = null,

    var videoChatEndAt: LocalDateTime? = null,
    ){

}