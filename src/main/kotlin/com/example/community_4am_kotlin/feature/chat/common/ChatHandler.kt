package com.example.community_4am_kotlin.feature.chat.common

import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession

@Component
interface ChatHandler : WebSocketHandler {
    @Throws(Exception::class)
    fun handleTextMessage(session: WebSocketSession?, message: TextMessage?)

    @Throws(Exception::class)
    override fun afterConnectionEstablished(session: WebSocketSession)

    @Throws(Exception::class)
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus)

}

