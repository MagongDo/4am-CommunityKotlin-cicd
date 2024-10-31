package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.feature.chat.service.RedisSubscriber
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession

@Configuration
class RedisConfig {

    /**
     * RedisTemplate 설정
     * 레디스에 정보를 저장하고 읽기 위해 사용하는 기본 도구
     * key-value 형태의 데이터를 처리하며 레디스에 메시지를 주거나 데이터를 저장할 때 사용
     * Redis 연결 팩토리를 사용하여 레디스 서버와의 연결을 설정
     */
    @Bean
    @Primary
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, String> =
        RedisTemplate<String, String>().apply {
            connectionFactory = redisConnectionFactory
            // JSON 직렬화 설정
            val serializer = Jackson2JsonRedisSerializer<Any>(Any::class.java)
            setDefaultSerializer(serializer)
            // defaultSerializer = serializer
        }

    /**
     * VideoRedisTemplate 설정
     * 특정 용도의 RedisTemplate을 별도로 설정할 때 사용
     * key와 value를 문자열로 직렬화
     */
    @Bean
    fun videoRedisTemplate(redisConnectionFactory: RedisConnectionFactory): RedisTemplate<String, Any> =
        RedisTemplate<String, Any>().apply {
            connectionFactory = redisConnectionFactory
            keySerializer = StringRedisSerializer()
            valueSerializer = StringRedisSerializer()
        }

    /**
     * Redis Pub/Sub 메시지를 수신하기 위한 리스너 컨테이너 설정
     * Redis와의 연결을 설정하고, 특정 토픽에 대해 메시지 리스너를 등록
     * listenerAdapter는 메시지를 실제로 처리하는 리스너 객체
     * 여기서는 "chatRoom"이라는 토픽을 구독
     */
    @Bean
    fun redisMessageListenerContainer(
        redisConnectionFactory: RedisConnectionFactory,
        messageListenerAdapter: MessageListenerAdapter
    ): RedisMessageListenerContainer =
        RedisMessageListenerContainer().apply {
            setConnectionFactory(redisConnectionFactory)
            addMessageListener(messageListenerAdapter, ChannelTopic("chatRoom")) // 토픽 설정
        }

    /**
     * 메시지가 수신될 때마다 onMessage() 메서드가 호출되도록 MessageListenerAdapter 설정
     * RedisSubscriber의 onMessage 메서드를 호출하도록 설정
     */
    @Bean
    fun listenerAdapter(redisSubscriber: RedisSubscriber): MessageListenerAdapter =
        MessageListenerAdapter(redisSubscriber, "onMessage")

    /**
     * 고정된 하나의 토픽을 사용할 때만 적용됨
     * 동적 토픽을 사용할 땐 불필요할 수 있음
     */
    @Bean
    fun topic(): ChannelTopic = ChannelTopic("topic")
}