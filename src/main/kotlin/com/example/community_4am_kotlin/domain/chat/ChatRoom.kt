package com.example.Community_4am_Kotlin.domain.chat

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class ChatRoom(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    var id: Long?= null,

    @Column(name="room_name")
    var roomName:String,
    @Column(name="email", nullable = false)
    var email:String,
    @Column(name="description", nullable = false)
    var description:String,
    @Column( nullable = false)
    var createdDate: LocalDateTime,
    @OneToMany(mappedBy="chatRoom", cascade = [(CascadeType.MERGE)],orphanRemoval = true)
    var message:MutableList<ChatMessage> = mutableListOf()
)