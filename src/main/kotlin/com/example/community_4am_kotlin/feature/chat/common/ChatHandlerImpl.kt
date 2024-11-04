package com.example.community_4am_kotlin.feature.chat.common

import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import com.example.community_4am_kotlin.feature.chat.service.ChatService
import com.example.community_4am_kotlin.feature.chat.service.MessageBrokerService
import com.example.community_4am_kotlin.log
import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.shaded.gson.JsonObject
import com.nimbusds.jose.shaded.gson.JsonParser
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatHandlerImpl(
    private val chatService: ChatService,
    private val messageBrokerService: MessageBrokerService,
    private val roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>,
    private val messageRepository: MessageRepository,
    private val gson: com.google.gson.Gson
) : TextWebSocketHandler() ,ChatHandler {

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        //content 를 또 객체로 담아서? 중첩 직렬화가 진행된걸까?
        session.let { wsSession ->
            val payload = message.payload
            log.info("Received payload: $payload") // payload 전체 확인

            val roomId = wsSession.uri?.toString()?.split("/ws/chat/")?.getOrNull(1) ?: return
            val sender = wsSession.principal?.name ?: "unknown"
            log.info("Sender: $sender")

            // payload에서 content와 message 필드를 직접 추출
            val messageContent = try {
                // Gson 대신 JsonParser를 사용하여 직접 추출
                val jsonObject = JsonParser.parseString(payload).asJsonObject
                val contentObject = jsonObject["content"]?.asJsonObject
                contentObject?.get("message")?.asString ?: ""
            } catch (e: Exception) {
                log.error("Failed to parse JSON payload: $payload", e)
                ""
            }

            if (messageContent.isNotEmpty()) {
                // messageContent만 포함하는 JSON 생성
                val messageJson = mapOf(
                    "type" to "message",
                    "roomId" to roomId,
                    "message" to messageContent,
                    "sender" to sender
                ).let { gson.toJson(it) }

                // 메시지 전송
                messageBrokerService.publishToChannel(roomId, messageJson)
            } else {
                log.warn("Message content is empty.")
            }
        }
        }


    override fun afterConnectionEstablished(session: WebSocketSession) {
        val roomId = session.uri.toString().split("/ws/chat/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val accountId = session.attributes["accountId"].toString()

        chatService.handleUserConnection(session, roomId, accountId, roomSessions)
        chatService.memberListUpdated(roomId, roomSessions)

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomId: String = extractRoomId(session)
        val sessionId = session.id
        val accountId = session.attributes["accountId"].toString()
        val sessionsInRoom = roomSessions[roomId]

        if (roomSessions.containsKey(roomId) && roomSessions[roomId]!!.containsKey(session.id)) {
            // 중복 처리 방지
            roomSessions[roomId]?.remove(session.id)
            chatService.handleUserDisconnection(session, roomId, accountId)
        }

    }

    // URI에서 roomId 추출
    private fun extractRoomId(session: WebSocketSession): String {
        return session.uri.toString().split("/ws/chat/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
    }
}