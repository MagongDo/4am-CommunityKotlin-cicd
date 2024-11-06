package com.example.community_4am_kotlin.config

import org.slf4j.LoggerFactory
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor

@Component
class SessionHandshakeInterceptor : HandshakeInterceptor {

    private val log = LoggerFactory.getLogger(SessionHandshakeInterceptor::class.java)

    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>
    ): Boolean {
        // Spring Security 세션을 통해 사용자 이름(author)을 가져옵니다.
        val principal = request.principal
        if (principal != null) {
            val author = principal.name // 보통 이메일로 설정
            attributes["author"] = author
            log.info("HandshakeInterceptor: author=$author")
        } else {
            log.warn("HandshakeInterceptor: Principal is null")
        }
        return true
    }
    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?
    ) {
        // 추가적인 로직 필요 시 구현
    }
}
