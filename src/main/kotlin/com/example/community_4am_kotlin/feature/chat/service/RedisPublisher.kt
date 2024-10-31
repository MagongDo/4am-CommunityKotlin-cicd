package com.example.community_4am_kotlin.feature.chat.service

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.stereotype.Service

@Service
class RedisPublisher (
    private val redisTemplate: RedisTemplate<String, String>
){
    fun publish(roomId : String ,message: String) {
        val topic = ChannelTopic(roomId)
        redisTemplate.convertAndSend(topic.topic, message)
    }
}