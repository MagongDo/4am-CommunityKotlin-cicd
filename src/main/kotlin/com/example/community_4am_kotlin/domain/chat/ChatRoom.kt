package com.example.community_4am_Kotlin.domain.chat

import com.example.community_4am_Kotlin.domain.chat.ChatMessage
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class ChatRoom(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private var id: Long?,

    @Column(name="room_name")
    private var roomName:String,
    @Column(name="email", nullable = false)
    private var email:String,
    @Column(name="description", nullable = false)
    private var description:String,
    @Column( nullable = false)
    private var createdDate: LocalDateTime,
    @OneToMany(mappedBy="chatRoom", cascade = [(CascadeType.MERGE)],orphanRemoval = true)
    private var message:MutableList<ChatMessage> = mutableListOf()
){

}