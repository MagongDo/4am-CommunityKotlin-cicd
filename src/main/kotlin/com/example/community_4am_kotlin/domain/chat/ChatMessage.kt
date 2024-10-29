package com.example.Community_4am_Kotlin.domain.chat

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class ChatMessage (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var id: Long,
    @ManyToOne
    @JoinColumn(name="room_id")
    private var chatRoom : ChatRoom,

    @Column(name="sender", nullable = false)
    private var sender:String,
    @Column(name="content", nullable = false)
    private var content:String,

    @Column(name="create_at", nullable = false)
    private var createAt: LocalDateTime

    ){
    fun ChatMessage(chatRoom: ChatRoom, sender: String, content: String, createAt: LocalDateTime){
        this.chatRoom = chatRoom
        this.sender=sender
        this.content = content
        this.createAt = createAt

    }
}