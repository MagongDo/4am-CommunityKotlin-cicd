package com.example.community_4am_kotlin.feature.videochat.service

import com.example.community_4am_kotlin.log
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    @Qualifier("videoRedisTemplate") private val redisTemplate: RedisTemplate<String, Any>,
    private val objectMapper: ObjectMapper // ObjectMapper 주입
) {
    /**
     * 비디오 채팅 메시지 로그를 Redis에 저장하는 메서드
     *
     * @param videoChatId 비디오 채팅 방 ID
     * @param userId 메시지를 보낸 사용자 ID
     * @param otherUserId 상대방 사용자 ID
     * @param message 메시지 내용
     */
    @Throws(JsonProcessingException::class)
    fun saveVideoChatMessageLog(
        videoChatId: String,
        userId: Long,
        otherUserId: Long,
        message: String
    ) {
        val key = "RedisVideoChatMessageLog : $videoChatId"

        // 대화 내역을 Map으로 생성
        val chatLog = mapOf(
            "video_chat_id" to videoChatId,
            "user_id" to userId,
            "other_user_id" to otherUserId,
            "video_chat_message" to message,
            "video_chat_message_sent_at" to LocalDateTime.now().toString()
        )

        // Map을 JSON으로 직렬화
        val serializedChatLog: String = try {
            objectMapper.writeValueAsString(chatLog)
        } catch (e: JsonProcessingException) {
            log.error("JSON 직렬화 오류: {}", e.message)
            throw e
        }

        // Redis List에 대화 내역 저장
        redisTemplate.opsForList().rightPush(key, serializedChatLog)

        // TTL 설정: 한달 (30일)
        redisTemplate.expire(key, 30, TimeUnit.DAYS)
    }

    /**
     * 비디오 채팅 메시지 로그를 Redis에서 가져오는 메서드
     *
     * @param videoChatId 비디오 채팅 방 ID
     * @return 대화 내역 리스트
     */
    @Throws(JsonProcessingException::class)
    fun getVideoChatMessageLog(videoChatId: String): List<Any> {
        val key = "RedisVideoChatMessageLog : $videoChatId"

        // Redis에서 저장된 대화 내역을 리스트로 가져옴
        val chatLogs: List<Any>? = redisTemplate.opsForList().range(key, 0, -1) // 모든 리스트 항목을 가져옴

        return if (chatLogs.isNullOrEmpty()) {
            emptyList()
        } else {
            chatLogs
        }
    }




}