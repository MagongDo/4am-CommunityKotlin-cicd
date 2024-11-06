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
        // Redis 채널 구독 (방에 사용자들이 전송하는 메시지 관리)
        log.info("Redis 채널 구독 시작: roomId=$roomId")
        messageBrokerService.subscribeToChannel(roomId)
        log.info("Redis 채널 구독 완료: roomId=$roomId")

        // Redis에서 기존 세션 ID 가져오기
        log.info("Redis에서 기존 세션 ID 조회: accountId=$accountId, roomId=$roomId")
        val existingSessionId = sessionManager.getSessionIfExists(accountId, roomId)

        // Redis에서 사용자의 입장 상태 확인
        val isAlreadyEntered = sessionManager.isAlreadyEntered(accountId, roomId)

        if (existingSessionId != null) {
            // 기존 세션이 존재할 때는 재사용하고 입장 메시지를 보내지 않음
            log.info("기존 세션 발견: accountId=$accountId, 기존 sessionId=$existingSessionId. 세션 재사용 중")
            roomSessions.computeIfAbsent(roomId) { ConcurrentHashMap() }[existingSessionId] = session
            log.info("기존 세션 재사용 완료: accountId=$accountId, sessionId=$existingSessionId")
        } else {
            // 기존 세션이 없고 입장 메시지를 보내야 할 경우
            if (!isAlreadyEntered) {
                log.info("기존 세션 없음. 새로운 세션 생성 중: accountId=$accountId, 새로운 sessionId=${session.id}")
                roomSessions.computeIfAbsent(roomId) { ConcurrentHashMap() }[session.id] = session
                sessionManager.cacheSession(accountId, roomId, session.id)
                sessionManager.setEntered(accountId, roomId)  // 입장 상태 플래그 설정
                log.info("새로운 세션 저장 완료: accountId=$accountId, sessionId=${session.id}")

                // 입장 메시지 생성 및 전송
                val welcomeMessage = mapOf(
                    "type" to "message",
                    "roomId" to roomId,
                    "message" to "${accountId}님이 입장했습니다.",
                    "sender" to "시스템"
                ).let { Gson().toJson(it) }
                messageBrokerService.publishToChannel(roomId, welcomeMessage)
                log.info("입장 메시지 전송 완료: roomId=$roomId, message=$welcomeMessage")
            } else {
                log.info("사용자가 이미 입장한 상태입니다: accountId=$accountId, roomId=$roomId")
            }
        }

        // Redis 정보를 기반으로 멤버 목록 업데이트
        memberListUpdated(roomId, roomSessions)
    }

    override fun handleUserDisconnection(
        session: WebSocketSession,
        roomId: String,
        accountId: String
    ) {
        // 메모리 및 Redis에서 세션 정보 삭제
        roomSessions[roomId]?.remove(session.id)
        sessionManager.deleteSession(accountId, roomId)
        log.info("사용자 퇴장 처리: accountId=$accountId, sessionId=${session.id}")

        // 퇴장 메시지 전송
        val leaveMessage = mapOf(
            "type" to "message",
            "roomId" to roomId,
            "message" to "${accountId}님이 퇴장하셨습니다.",
            "sender" to "시스템"
        ).let { Gson().toJson(it) }
        messageBrokerService.publishToChannel(roomId, leaveMessage)
    }
    override fun memberListUpdated(
        roomId: String,
        roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>
    ) {

        // 해당 방의 세션 목록을 가져와 멤버 리스트 생성
        roomSessions[roomId]?.let { sessionsInRoom ->
            val memberList = sessionsInRoom.values.mapNotNull { session ->
                val username = session.attributes["accountId"] as String?
                username?.let { userService.findByEmail(it).nickname }
            }

            // 멤버 목록 JSON 생성 및 전송
            val memberListJson = mapOf(
                "type" to "memberList",
                "members" to memberList
            ).let { Gson().toJson(it) }

            sessionsInRoom.values.forEach { session ->
                try {
                    session.sendMessage(TextMessage(memberListJson))
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun getUserCount(roomId: String): Int =
        roomSessions[roomId]?.size ?: 0
}