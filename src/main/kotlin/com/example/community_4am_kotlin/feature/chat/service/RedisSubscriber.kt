package com.example.community_4am_kotlin.feature.chat.service

import com.example.community_4am_kotlin.domain.chat.ChatMessage
import com.example.community_4am_kotlin.log
import com.example.community_4am_kotlin.feature.chat.repository.ChatRoomRepository
import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime
import java.io.IOException
import java.security.Principal
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.log

@Service
class RedisSubscriber(
    private val roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>,
    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository,
    private val gson: Gson

) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val roomId = pattern?.let { String(it, Charsets.UTF_8) } ?: return
        val content = String(message.body, Charsets.UTF_8)
        log.info("roomId: $roomId, content: $content")


        roomSessions[roomId]?.values?.forEach { session ->
            try {
                val accountId = session.attributes["accountId"] as String?
                session.sendMessage(TextMessage(content))
                log.info("보낸 메세지 ${content}")
                // 메시지 저장
                accountId?.let { saveMessage(roomId, it, content) }
                log.info("메세지가 저장되었습니다")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        println("Redis에서 수신한 메시지: $content")
    }

    fun saveMessage(channel: String, accountId: String, content: String) {
        val roomId = channel.toLongOrNull() ?: return

        chatRoomRepository.findById(roomId).orElse(null)?.let { chatRoom ->
            val chatMessage = ChatMessage(
                chatRoom = chatRoom,
                content = content,
                sender = accountId,
                createAt = LocalDateTime.now()
            )
            messageRepository.save(chatMessage)
            log.info("Message saved: $content in room $roomId by $accountId")
        }
    }
}

