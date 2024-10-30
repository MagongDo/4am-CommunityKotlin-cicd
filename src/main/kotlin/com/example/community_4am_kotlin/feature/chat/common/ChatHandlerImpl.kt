package com.example.community_4am_kotlin.feature.chat.common

import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import com.example.community_4am_kotlin.feature.chat.service.ChatService
import com.example.community_4am_kotlin.feature.chat.service.MessageBrokerService
import com.nimbusds.jose.shaded.gson.Gson
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.util.concurrent.ConcurrentHashMap

@Component
class ChatHandlerImpl(
    private val chatService : ChatService,
    private val messageBrokerService : MessageBrokerService,
    private val roomSessions : ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>,
    private val messageRepository : MessageRepository
) : TextWebSocketHandler() ,ChatHandler {
    override fun handleTextMessage(session: WebSocketSession?, message: TextMessage?) {
        // session과 message가 null이 아닐 때만 실행
        session?.let { wsSession ->
            message?.payload?.let { payload ->
                // 방 ID 가져오기
                val roomId = wsSession.uri?.toString()?.split("/ws/chat/")?.getOrNull(1) ?: return

                // JSON 파싱 및 타입 명확화
                val type = object : com.google.gson.reflect.TypeToken<MutableMap<String, Any>>() {}.type
                val messageData: MutableMap<String, Any> = Gson().fromJson(payload, type)

                // WebSocketSession에서 사용자 정보 가져오기
                val sender = wsSession.principal?.name ?: "unknown"

                // 메시지에 sender 정보 추가
                messageData["sender"] = sender

                // 메시지 재구성 (sender 포함)
                val formattedMessage = Gson().toJson(messageData)

                // 메시지를 Redis로 발행
                messageBrokerService.publishToChannel(roomId, formattedMessage)
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