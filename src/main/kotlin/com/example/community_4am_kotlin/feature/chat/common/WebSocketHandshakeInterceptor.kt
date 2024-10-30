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
        // Spring Security의 인증 정보 가져오기
        val authentication: Authentication? = SecurityContextHolder.getContext().authentication

        authentication?.let {
            // 인증 정보를 WebSocket 세션의 속성에 추가
            attributes["accountId"] = it.name // 사용자 이름 저장 (예: 이메일)
            attributes["authorities"] = it.authorities // 권한 저장
        }

        return true // WebSocket 연결을 허용
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // 후처리 로직이 필요한 경우 여기에 작성
    }
}