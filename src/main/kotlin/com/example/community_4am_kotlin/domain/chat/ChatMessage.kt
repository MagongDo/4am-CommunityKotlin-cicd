package com.example.Community_4am_Kotlin.domain.chat

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class ChatMessage (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long ?= null,

    @ManyToOne
    @JoinColumn(name="room_id")
    var chatRoom : ChatRoom,

    @Column(name="sender", nullable = false)
    var sender:String? = "알 수 없는 사용자",
    @Column(name="content", nullable = false)
    var content:String? = "내용이 비어있습니다.",

    @Column(name="create_at", nullable = false)
    var createAt: LocalDateTime = LocalDateTime.now(),
    )