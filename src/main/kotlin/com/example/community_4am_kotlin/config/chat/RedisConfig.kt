package com.example.community_4am_kotlin.config.chat

import com.example.community_4am_kotlin.feature.chat.service.RedisSubscriber
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

@Configuration
class RedisConfig {

    @Bean
    @Primary
    fun redisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>()
        redisTemplate.connectionFactory = redisConnectionFactory
        // JSON 직렬화 설정
        val serializer = Jackson2JsonRedisSerializer(Any::class.java)
        redisTemplate.setDefaultSerializer(serializer)
        return redisTemplate
    }

    @Bean
    fun VideoRedisTemplate(redisConnectionFactory: RedisConnectionFactory?): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.valueSerializer = StringRedisSerializer()

        return redisTemplate
    }

    //Redis Pub/Sub 메시지를 수신하기 위한 리스너 컨테이너
    //RonnectionFactory는 Redis와의 연결을 설정
    //addMessageListener() 메서드는 특정 토픽에 대해 메시지 리스너를 등록
    //listenerAdapter는 메시지를 실제로 처리하는 리스너 객체입니다.
    //topic()은 메시지를 수신할 Redis 채널(토픽)을 지정합니다. 이 코드에서는 chat이라는 채널을 구독하고 있습니다.
    @Bean
    fun redisMessageListenerContainer(
        redisConnectionFactory: RedisConnectionFactory?,
        messageListenerAdapter: MessageListenerAdapter?
    ): RedisMessageListenerContainer {
        val container = RedisMessageListenerContainer()
        container.setConnectionFactory(redisConnectionFactory!!)
        container.addMessageListener(messageListenerAdapter!!, ChannelTopic("chatRoom")) // 토픽 설정

        return container
    }

    //메시지가 수신될 때마다 onMessage() 메서드가 호출
    @Bean
    fun listenerAdapter(redisSubscriber: RedisSubscriber): MessageListenerAdapter = MessageListenerAdapter(redisSubscriber, "onMessage")


    //고정된 하나의 토픽을 사용할 때만 적용됩으로 동적 토픽을 사용할 땐 불필요..?
    @Bean
    fun topic(): ChannelTopic = ChannelTopic("topic")


}