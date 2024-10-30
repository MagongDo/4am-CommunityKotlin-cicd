package com.example.community_4am_kotlin.feature.chat.service

import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.context.annotation.Lazy
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.listener.RedisMessageListenerContainer
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter
import org.springframework.stereotype.Service

@Service
class MessageBrokerService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val redisMessageListenerContainer: RedisMessageListenerContainer,
    @Lazy private val redisSubscriber: RedisSubscriber
) {

    fun subscribeToChannel(roomId : String) : Unit {
        val topic = ChannelTopic(roomId) // roomId를 기준으로 채널을 동적으로 생성
        val adapter = MessageListenerAdapter(redisSubscriber, "onMessage")
        redisMessageListenerContainer.addMessageListener(adapter, topic)
    }

    fun publishToChannel(roomId: String, message: String) {
        val topic = ChannelTopic(roomId)
        redisTemplate.convertAndSend(topic.topic,message)
    }

}