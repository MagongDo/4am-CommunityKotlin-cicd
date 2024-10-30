package com.example.Community_4am_Kotlin.feature.chat.dto

import com.example.Community_4am_Kotlin.domain.chat.ChatRoom
import org.springframework.data.jpa.domain.AbstractAuditable_.createdDate
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import java.time.LocalDateTime

data class CreateChatForm(
    val roomName: String,
  /*  val email: String,*/
    val description: String
)
