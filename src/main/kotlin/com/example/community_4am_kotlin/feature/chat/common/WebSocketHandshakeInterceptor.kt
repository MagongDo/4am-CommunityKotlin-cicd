package com.example.community_4am_kotlin.feature.chat.common

import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class WebSocketHandshakeInterceptor : HandshakeInterceptor {

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        SecurityContextHolder.getContext().authentication?.let { authentication ->
            // 인증 정보를 WebSocket 세션의 속성에 추가
            attributes["accountId"] = authentication.name // 사용자 이메일 저장
            attributes["authorities"] = authentication.authorities // 권한 저장
        }

        return true // WebSocket 연결을 허용
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {

    }
}