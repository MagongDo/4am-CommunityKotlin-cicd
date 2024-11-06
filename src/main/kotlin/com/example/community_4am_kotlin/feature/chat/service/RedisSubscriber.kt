package com.example.community_4am_kotlin.feature.chat.service

import com.example.community_4am_kotlin.domain.chat.ChatMessage
import com.example.community_4am_kotlin.log
import com.example.community_4am_kotlin.feature.chat.repository.ChatRoomRepository
import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
    private val chatMessageService: ChatMessageService,
    private val gson: Gson

) : MessageListener {

    override fun onMessage(message: Message, pattern: ByteArray?) {
        log.info("onMessage 메서드 호출됨") // 메서드 호출 확인
        val roomId = pattern?.let { String(it, Charsets.UTF_8) } ?: return
        var content = String(message.body, Charsets.UTF_8)

        // 이스케이프 제거
        // 정규 표현식으로 이중 이스케이프를 모두 제거
        val cleanedMessage = content.replace("""\\+""".toRegex(), "")
        log.info("roomId: $roomId, unescaped content: $content")

        // WebSocket 및 Redis로 전송할 내용
        val messageContent = content

        // WebSocket으로 전송
        roomSessions[roomId]?.values?.forEach { session ->
            try {
                session.sendMessage(TextMessage(content))  // 원본 JSON 문자열 사용
                log.info("보낸 메세지: $content")

                // 메시지 저장
                val accountId = session.attributes["accountId"] as String?
                accountId?.let {
                    saveMessage(roomId, it, messageContent)
                    chatMessageService.saveMessage(roomId, cleanedMessage)
                }
                log.info("메세지가 저장되었습니다")
            } catch (e: IOException) {
                log.error("Failed to send message to session", e)
            }
        }
    }

    fun saveMessage(channel: String, accountId: String, messageDate: String) {
        val roomId = channel.toLongOrNull() ?: return

        chatRoomRepository.findById(roomId).orElse(null)?.let { chatRoom ->
            val chatMessage = ChatMessage(
                chatRoom = chatRoom,
                content = messageDate,
                sender = accountId,
                createAt = LocalDateTime.now()
            )
            messageRepository.save(chatMessage)
            log.info("Message saved: $messageDate in room $roomId by $accountId")
        }
    }
}

