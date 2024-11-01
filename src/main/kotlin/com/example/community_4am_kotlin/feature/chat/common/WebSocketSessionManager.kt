package com.example.community_4am_kotlin.feature.chat.common


import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.stereotype.Component

@Component
class WebSocketSessionManager (
    private val redisTemplate: RedisTemplate<String, String>,
){
    // .opsForValue() : 문자열(String) 데이터를 Redis에 저장하고 조회할 수 있는 기능을 제공하는 메서드
    // 키- 값 데이터 조회를 위한 객체 (set,get)으로 운용
    private val valueOps: ValueOperations<String, String> = redisTemplate.opsForValue()

    //레디스에세션 웹 소켓 세션 id를 저장하기 위한 메서드
     fun cacheSession(accountId: String, roomId : String, sessionId: String ) {
         valueOps.set("$accountId:$roomId", sessionId)
     }
    // 조회하기 (IfExists : 존재하면 가져온다)
    fun getSessionIfExists(accountId: String, roomId: String): String? {
       return valueOps.get("session:$accountId:$roomId")
    }
    // 삭제하기
    fun deleteSession(accountId: String, roomId: String) {
        redisTemplate.delete("session:$accountId:$roomId")
    }
}