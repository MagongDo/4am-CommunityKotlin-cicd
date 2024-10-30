package com.example.community_4am_kotlin.feature.chat.service

import com.example.Community_4am_Kotlin.domain.chat.ChatMessage
import com.example.community_4am_kotlin.feature.chat.repository.ChatRoomRepository
import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.stereotype.Service
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class RedisSubscriber(
    private val roomSessions : ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>,
    private val messageRepository: MessageRepository,
    private val chatRoomRepository: ChatRoomRepository
) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val channel = pattern?.toString() ?: return
        val content = message.body.toString()

        roomSessions[channel]?.values?.forEach { session ->
            try {
                session.sendMessage(TextMessage(content))

                val accountId = session.attributes["accountId"] as String?
                accountId?.let { saveMessage(channel, it, content) }

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