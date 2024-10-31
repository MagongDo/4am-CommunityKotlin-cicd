package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.feature.chat.common.ChatHandler
import com.example.community_4am_kotlin.feature.chat.common.WebSocketHandshakeInterceptor
import com.example.community_4am_kotlin.feature.videochat.handler.RandomVideoChatHandler
import com.sun.nio.sctp.NotificationHandler
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry
import java.util.concurrent.ConcurrentHashMap

@Configuration
@EnableWebSocket // 웹소켓의 기능을 활성화하는 어노테이션
class WebSocketConfig(
    private val webSocketHandshakeInterceptor: WebSocketHandshakeInterceptor,
    @Lazy
    private val chatHandler: ChatHandler,
    private val randomVideoChatHandler: RandomVideoChatHandler,
//    private val notificationHandler: NotificationHandler,
//    private val sessionHandshakeInterceptor: SessionHandshakeInterceptor
) : WebSocketConfigurer {

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(chatHandler, "/ws/chat/*")
            .addInterceptors(webSocketHandshakeInterceptor)
            .setAllowedOrigins("*")

        // 랜덤 화상채팅 WebSocket 핸들러 추가
        registry.addHandler(randomVideoChatHandler, "/ws/random-video-chat")
            .setAllowedOrigins("*")

//        registry.addHandler(notificationHandler, "/ws/notifications")
//            .addInterceptors(sessionHandshakeInterceptor)
//            .setAllowedOrigins("*") // CORS 설정 필요 시 조정

    }

    @Bean
    fun socketTextHandler(): WebSocketHandler = chatHandler

    @Bean
    fun roomSessions() : ConcurrentHashMap<String, MutableMap<String, WebSocketSession>> = ConcurrentHashMap()

}