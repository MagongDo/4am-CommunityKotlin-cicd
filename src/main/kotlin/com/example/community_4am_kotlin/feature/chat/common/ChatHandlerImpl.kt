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

        // 방 Id 가져오기
        val roomId =
            session!!.uri.toString().split("/ws/chat/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        val payload = message!!.payload
        // JSON 파싱 및 재구성
        val messageData: MutableMap<*, *>? = Gson().fromJson(payload, MutableMap::class.java)

        val chatMessage = messageData?.get("chatMessage") as String?
        // WebSocketSession에서 사용자 정보 가져오기
        val sender = session.principal?.name // 사용자 이름 또는 이메일 가져오기
        // 메시지에 sender 정보 추가
       /* messageData?.set("sender", sender)*/
        // 메시지 재구성 (sender 포함)
        val formattedMessage = Gson().toJson(messageData)
        // 메세지를 레디스로 발행하기 (레디스에 sender 정보 포함)
        messageBrokerService.publishToChannel(roomId, formattedMessage)
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