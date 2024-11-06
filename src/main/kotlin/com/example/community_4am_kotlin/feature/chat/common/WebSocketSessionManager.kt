package com.example.community_4am_kotlin.feature.chat.common


import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class WebSocketSessionManager (
    private val redisTemplate: RedisTemplate<String, String>,
){
    // .opsForValue() : 문자열(String) 데이터를 Redis에 저장하고 조회할 수 있는 기능을 제공하는 메서드
    // 키- 값 데이터 조회를 위한 객체 (set,get)으로 운용
    private val valueOps: ValueOperations<String, String> = redisTemplate.opsForValue()

    //레디스에세션 웹 소켓 세션 id를 저장하기 위한 메서드
     fun cacheSession(accountId: String, roomId : String, sessionId: String,expiration: Long = 1800) {
        val key = "session:${accountId}:${roomId}"  // 이스케이프 없이 저장
        redisTemplate.opsForValue().set(key, sessionId)
     }
    // 조회하기 (IfExists : 존재하면 가져온다)
    fun getSessionIfExists(accountId: String, roomId: String): String? {
        val key = "\"session:$accountId:$roomId\""  // 이스케이프된 형태
        return redisTemplate.opsForValue().get(key)
    }
    // 삭제하기
    fun deleteSession(accountId: String, roomId: String) {
        redisTemplate.delete("session:$accountId:$roomId")
    }

    fun isAlreadyEntered(accountId: String, roomId: String): Boolean {
        return redisTemplate.opsForValue().get("entered:$accountId:$roomId") == "true"
    }

    fun setEntered(accountId: String, roomId: String) {
        redisTemplate.opsForValue().set("entered:$accountId:$roomId", "true")
    }
}