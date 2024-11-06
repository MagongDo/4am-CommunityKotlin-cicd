package com.example.community_4am_kotlin.feature.chat.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.example.community_4am_kotlin.log
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class ChatMessageService(
    private val redisTemplate: RedisTemplate<String, String>,
    private val jacksonObjectMapper: ObjectMapper,
) {  private val objectMapper = jacksonObjectMapper()


    fun saveMessage(roomId: String, message: String) {
        val key = "chatroom:$roomId"

        // 정규 표현식으로 이중 이스케이프를 모두 제거
        val cleanedMessage = message.replace("""\\+""".toRegex(), "")

        println("message to save: $cleanedMessage")
        redisTemplate.opsForList().rightPush(key, cleanedMessage)
    }

    fun getMessage(roomId: Long):  List<Map<String, String>> {
        val key = "chatroom:$roomId"
        val messages = redisTemplate.opsForList().range(key, 0, -1) ?: listOf()

        log.info("Retrieved messages from Redis: $messages")

        return messages.map { message ->
            try {
                log.info("Processing message: $message")

                // 양 끝의 쌍따옴표 제거 후 JSON 파싱
                val sanitizedMessage = message.substring(1, message.length - 1)
                objectMapper.readValue(sanitizedMessage, Map::class.java) as Map<String, String>
            } catch (e: Exception) {
                log.error("Error deserializing message: $message", e)
                emptyMap() // 오류 발생 시 빈 Map 반환 또는 다른 오류 처리
            }
        }
    }
    }

    //삭제는 추후에 하기
