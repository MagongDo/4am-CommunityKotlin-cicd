package com.example.community_4am_kotlin.feature.notification.service.handler

import com.example.Community_4am_Kotlin.domain.notification.CoustomAlarm
import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.feature.notification.event.CustomAlarmReceivedEvent
import com.example.community_4am_kotlin.feature.notification.event.NotificationEvent
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Component
class NotificationHandler(
    private val objectMapper: ObjectMapper,
    private val eventPublisher: ApplicationEventPublisher
) : TextWebSocketHandler() {

    companion object {
        private val sessions: ConcurrentHashMap<String, WebSocketSession> = ConcurrentHashMap()
    }

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession) {
        val author = session.attributes["author"] as? String
        if (author != null) {
            sessions[author] = session
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("author 정보 없음"))
        }
    }

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val author = session.attributes["author"] as? String
        if (author != null) {
            sessions.remove(author)
        }
    }

    @Throws(Exception::class)
    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        val payload = message.payload
        val author = session.attributes["author"] as? String

        try {
            val customAlarmNotification = objectMapper.readValue(payload, CoustomAlarm::class.java)
            customAlarmNotification.userId = author // CustomAlarm 엔티티에 setUserId 메소드가 있어야 함

            eventPublisher.publishEvent(CustomAlarmReceivedEvent(this, customAlarmNotification))

            val response = "Alarm received for user: ${customAlarmNotification.userId}"
            session.sendMessage(TextMessage(response))
        } catch (e: Exception) {
            session.sendMessage(TextMessage("Error processing your alarm."))
        }
    }

    fun sendNotification(recipient: String?=null, message: String, alarmType: AlarmType) {
        val session = sessions[recipient]
        if (session != null && session.isOpen) {
            try {
                val payload = objectMapper.writeValueAsString(
                    mapOf(
                        "message" to message,
                        "alarmType" to alarmType.toString()
                    )
                )
                session.sendMessage(TextMessage(payload))
            } catch (e: IOException) {
                // 예외 처리 로직 추가 가능
            }
        }
    }

    @EventListener
    fun handleNotificationEvent(event: NotificationEvent) {
        sendNotification(event.recipient, event.message, event.alarmType)
    }
}