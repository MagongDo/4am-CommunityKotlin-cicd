package com.example.community_4am_kotlin.feature.chat.service


import com.example.community_4am_kotlin.feature.chat.common.WebSocketSessionManager
import com.example.community_4am_kotlin.feature.user.service.UserService
import com.example.community_4am_kotlin.log
import com.nimbusds.jose.shaded.gson.Gson
import com.nimbusds.jose.shaded.gson.JsonObject
import org.springframework.stereotype.Service
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatServiceImpl(
    private val sessionManager: WebSocketSessionManager,
    private val messageBrokerService: MessageBrokerService,
    private val userService : UserService,
    private val roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>
) : ChatService {

    override fun handleUserConnection(
        session: WebSocketSession,
        roomId: String,
        accountId: String,
        roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>
    ) {
        // Redis 채널 구독 설정
        messageBrokerService.subscribeToChannel(roomId)

        // 사용자 정보 조회
        val user = userService.findByEmail(accountId)
        val nickname = user.nickname
        val sessionId = session.id
        log.info("nickname : $nickname")

        //중복 세션이 있는지 확인하기
        val redisSession = sessionManager.getSessionIfExists(accountId,roomId)
        if(redisSession != null) {
            session.close(CloseStatus.NORMAL)
            return
        }
        // 세션 정보 저장
        roomSessions.computeIfAbsent(roomId) { ConcurrentHashMap() }[sessionId] = session
        sessionManager.cacheSession(accountId,roomId,sessionId)

        // 환영 메시지 전송
 /*       val welcomeMessage = "${nickname}님이 입장했습니다. 모두 환영해주세요"*/
       //전체적으로 동일한 Json 전달방식을 사용하기 위함
        // 환영 메시지를 JSON 객체로 구성
       val welcomeMessageData = mapOf(
           "type" to "message",
           "roomId" to roomId,
           "message" to "${nickname}님 환영합니다.",
           "sender" to "시스템"
       ).let { Gson().toJson(it) }

        messageBrokerService.publishToChannel(roomId, welcomeMessageData)
    }

    override fun handleUserDisconnection(session: WebSocketSession, roomId: String, accountId: String) {
        val nickname = userService.findByEmail(accountId).nickname ?: "사용자를 알 수 없음"

        // 퇴장 메시지 생성
        val messageJson = mapOf(
            "type" to "message",
            "roomId" to roomId,
            "message" to "${nickname}님이 퇴장하셨습니다.",
            "sender" to "시스템"
        ).let { Gson().toJson(it) } // JSON 변환

        // 메시지 전송
        messageBrokerService.publishToChannel(roomId, messageJson)

        // 세션 정보 제거
        roomSessions[roomId]?.remove(session.id)
        sessionManager.deleteSession(accountId,roomId)

    }
    override fun memberListUpdated(
        roomId: String,
        roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>
    ) {

        // 해당 방의 세션 목록을 가져옵니다.
        roomSessions[roomId]?.let { sessionsInRoom ->

            // 현재 방의 모든 사용자 이름 목록 생성
            val memberList = sessionsInRoom.values.mapNotNull { session ->
                val username = session.attributes["accountId"] as String? // 키 이름 확인
                username?.let { userService.findByEmail(it).nickname }
            }

            // 멤버 목록을 JSON 객체로 생성
            val memberListJson = mapOf(
                "type" to "memberList",       // 클라이언트가 데이터 유형을 구분할 수 있도록 "type" 필드 추가
                "members" to memberList        // 멤버 리스트 추가
            ).let { Gson().toJson(it) }

            // 각 세션에 멤버 목록을 전송
            sessionsInRoom.values.forEach { session ->
                try {
                    session.sendMessage(TextMessage(memberListJson))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }



    override fun getUserCount(roomId: String): Int = if (roomSessions.containsKey(roomId)) roomSessions[roomId]!!.size else 0;
}