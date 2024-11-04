package com.example.community_4am_kotlin.feature.videochat.handler

import com.example.community_4am_kotlin.config.jwt.TokenProvider
import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatLogDTO
import com.example.community_4am_kotlin.feature.videochat.service.RedisService
import com.example.community_4am_kotlin.feature.videochat.service.VideoChatService
import com.example.community_4am_kotlin.log
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.stereotype.Component
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.net.URI
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Component
class RandomVideoChatHandler(
    private val redisService: RedisService,
    private val videoChatService: VideoChatService,
    private val tokenProvider: TokenProvider
) : TextWebSocketHandler() {

    companion object {
        private val textChatSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>> = ConcurrentHashMap()
        private val videoChatSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>> = ConcurrentHashMap()
        private val waitingUsers: MutableList<WebSocketSession> = CopyOnWriteArrayList()
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        log.info("handleTextMessage Received message: {}", message.payload)
        log.info("handleTextMessage Received session: {}", session)

        try {
            // Signaling 데이터 처리
            val payload = message.payload
            val data: Map<*, *>? = ObjectMapper().readValue(payload, Map::class.java)

            // 방 ID 추출
            val roomId = session.attributes["roomId"] as? String
            log.info("roomId: {}", roomId)

            // message 내용 추출
            val chatMessage = data?.get("message") as? String
            log.info("chatMessage : {}", chatMessage)

            // 방 ID가 null인 경우 메시지 처리 중단
            if (roomId == null) {
                log.info("매칭되지 않은 사용자입니다. 매칭이 완료될 때까지 메시지를 처리하지 않습니다.")
                // 매칭 대기 중이라는 메시지를 클라이언트로 보낼 수도 있음
                session.sendMessage(TextMessage("""{"type": "waiting", "message": "매칭 대기 중입니다..."}"""))
                return
            }

            // 상대방 세션 아이디 추출
            val otherSessionId = getOtherSessionId(roomId, session.id)
            log.info("상대방 세션 아이디: {}", otherSessionId)

            // 채팅 또는 signaling 처리
            when (data?.get("type")) {
                "chat" -> {
                    sendMessageToTextChatSessions(session, TextMessage(payload))

                    // 상대방 세션에서 userId 가져오기
                    val sessionsInRoom = textChatSessions[roomId]
                    sessionsInRoom?.get(otherSessionId)?.let { otherSession ->
                        val otherUserId = otherSession.attributes["userId"] as? Long
                        val currentUserId = session.attributes["userId"] as? Long
                        log.info("currentUserId : {}", currentUserId)
                        log.info("otherUserId : {}", otherUserId)
                        if (roomId.toLongOrNull() != null && currentUserId != null && otherUserId != null && chatMessage != null) {
                            redisService.saveVideoChatMessageLog(roomId.toLong().toString(), currentUserId, otherUserId, chatMessage)
                        }
                    }
                }
                "offer", "answer", "candidate" -> {
                    sendMessageToVideoChatSessions(roomId, session, TextMessage(payload))
                }
                else -> {
                    log.info("지원하지 않는 메시지 타입: {}", data?.get("type") ?: String)
                }
            }
        } catch (e: Exception) {
            log.error("handleTextMessage 오류 발생", e)
        }
    }

    // 텍스트 채팅 세션에 메시지 전송
    private fun sendMessageToTextChatSessions(senderSession: WebSocketSession, message: TextMessage) {
        // 세션에서 방 ID 추출
        val roomId = senderSession.attributes["roomId"] as? String ?: return

        // 방 ID에 해당하는 텍스트 채팅 세션에서 메시지 전송
        val sessionsInRoom = textChatSessions[roomId]
        if (sessionsInRoom != null) {
            for (webSocketSession in sessionsInRoom.values) {
                if (webSocketSession.id != senderSession.id) {
                    sendMessage(webSocketSession, message)
                }
            }
        } else {
            log.info("해당 방 ID에 대한 텍스트 채팅 세션이 존재하지 않습니다: {}", roomId)
        }
    }

    // 상대방 세션 아이디를 가져오는 메서드
    private fun getOtherSessionId(roomId: String, currentSessionId: String): String? {
        val sessionsInRoom = videoChatSessions[roomId]
        return sessionsInRoom?.keys?.find { it != currentSessionId }
    }

    // 화상 채팅 세션에 메시지 전송
    private fun sendMessageToVideoChatSessions(roomId: String, senderSession: WebSocketSession, message: TextMessage) {
        log.info("sendMessageToVideoChatSessions : {}", senderSession)
        log.info("sendMessageToVideoChatSessions : {}", message)

        // 지정된 방 ID에 해당하는 세션을 가져옴
        val sessionsInRoom = videoChatSessions[roomId]
        if (sessionsInRoom != null) {
            for (webSocketSession in sessionsInRoom.values) {
                if (webSocketSession.id != senderSession.id) {
                    sendMessage(webSocketSession, message)
                }
            }
        } else {
            log.info("해당 방에 대한 세션이 없습니다: {}", roomId)
        }
    }

    // 백엔드에서 프론트로 데이터 전송 메서드
    private fun sendMessage(session: WebSocketSession, message: TextMessage) {
        synchronized(session) {
            if (session.isOpen) {
                try {
                    session.sendMessage(message)
                } catch (e: IOException) {
                    log.error("메시지 전송 중 오류 발생", e)
                }
            }
        }
    }

    // 대기 중인 사용자 매칭
    private fun matchUsers() {
        // 대기열에 2명 이상이 있는 경우 매칭
        log.info("현재 대기 중인 사용자 수: {}", waitingUsers.size)
        if (waitingUsers.size >= 2) {
            Collections.shuffle(waitingUsers) // 대기열 랜덤 셔플
            val user1 = waitingUsers.removeAt(0)
            val user2 = waitingUsers.removeAt(0)

            val roomId = UUID.randomUUID().toString() // 고유한 방 ID 생성
            log.info("matchUsers : {}", roomId)

            // 세션 속성에서 userId 가져오기
            val user1Id = user1.attributes["userId"] as Long
            val user2Id = user2.attributes["userId"] as Long

            // 매칭된 사용자에게 방 ID와 연결 메시지 전송
            notifyUsersOfMatch(user1, user2, roomId)

            // 화상 및 텍스트 채팅 세션에 추가
            addSessionToRooms(user1, user2, roomId)

            // 각 세션에 방 ID를 저장 (추가)
            saveRoomIdToSession(user1, roomId)
            saveRoomIdToSession(user2, roomId)

            // 디버깅 코드: 유저 둘이 같은 방에 들어가 있는지 확인
            verifyUsersInSameRoom(roomId, user1, user2)

            // user1에 대한 로그 생성
            videoChatService.videoChatStartTimeLog(
                VideoChatLogDTO(
                    VideoChatLog(
                        videoChatId = roomId,
                        userId = user1Id,
                        otherUserId = user2Id
                    )
                )
            )

            // user2에 대한 로그 생성
            videoChatService.videoChatStartTimeLog(
                VideoChatLogDTO(
                    VideoChatLog(
                        videoChatId = roomId,
                        userId = user2Id,
                        otherUserId = user1Id
                    )
                )
            )


        }
    }

    // 매칭된 사용자들에게 매칭 메시지 전송
    private fun notifyUsersOfMatch(user1: WebSocketSession, user2: WebSocketSession, roomId: String) {
        val user1Id = user1.attributes["userId"] as Long
        val user2Id = user2.attributes["userId"] as Long
        // user1은 offerer, user2는 answerer 역할 할당
        sendMessage(
            user1,
            TextMessage("""{"type": "match", "roomId": "$roomId", "role": "offerer", "otherUserId": "$user2Id", "message": "상대방과 연결되었습니다."}""")
        )
        sendMessage(
            user2,
            TextMessage("""{"type": "match", "roomId": "$roomId", "role": "answerer", "otherUserId": "$user1Id", "message": "상대방과 연결되었습니다."}""")
        )

        // 역할 정보를 세션에 저장
        user1.attributes["role"] = "offerer"
        user2.attributes["role"] = "answerer"
    }

    // 방에 유저 추가
    private fun addSessionToRooms(user1: WebSocketSession, user2: WebSocketSession, roomId: String) {
        videoChatSessions.computeIfAbsent(roomId) { ConcurrentHashMap() }.apply {
            put(user1.id, user1)
            put(user2.id, user2)
        }

        textChatSessions.computeIfAbsent(roomId) { ConcurrentHashMap() }.apply {
            put(user1.id, user1)
            put(user2.id, user2)
        }

        // 디버깅: 방에 몇 명이 들어왔는지 출력
        log.info("방에 추가된 유저 수 (비디오 세션): {}", videoChatSessions[roomId]?.size)
        log.info("방에 추가된 유저 수 (텍스트 세션): {}", textChatSessions[roomId]?.size)
    }

    // 방 ID를 세션에 저장
    private fun saveRoomIdToSession(session: WebSocketSession, roomId: String) {
        log.info("saveRoomIdToSession(roomId) : {}", roomId)
        session.attributes["roomId"] = roomId
        log.info("session 체크 : {}", session)
    }

    // 방에 들어간 유저 확인
    private fun verifyUsersInSameRoom(roomId: String, user1: WebSocketSession, user2: WebSocketSession) {
        val videoSessions = videoChatSessions[roomId]
        val textSessions = textChatSessions[roomId]
        log.info("두 유저가 들어간 roomId : {}", roomId)

        // 비디오 세션 확인
        if (videoSessions != null && videoSessions.size == 2) {
            log.info("비디오 세션에 두 유저가 모두 들어감: {}, {}", user1.id, user2.id)
        } else {
            log.info("비디오 세션에 문제가 있음. 현재 세션 수: {}", videoSessions?.size ?: 0)
        }

        // 텍스트 세션 확인
        if (textSessions != null && textSessions.size == 2) {
            log.info("텍스트 세션에 두 유저가 모두 들어감: {}, {}", user1.id, user2.id)
        } else {
            log.info("텍스트 세션에 문제가 있음. 현재 세션 수: {}", textSessions?.size ?: 0)
        }
    }

    // 사용자가 연결되었을 때 호출
    override fun afterConnectionEstablished(session: WebSocketSession) {
        log.info("User connected: {}", session.id)
        var userId: Long = 0L
        val uri: URI? = session.uri
        if (uri != null) {
            val builder = UriComponentsBuilder.fromUri(uri)
            val accessToken: String? = builder.build().queryParams.getFirst("accessToken")
            if (accessToken != null) {
                userId = tokenProvider.getUserId(accessToken)!!
                session.attributes["userId"] = userId
            }
        }

        // 이미 대기열에 있지 않으면 추가
        if (!waitingUsers.contains(session)) {
            waitingUsers.add(session)
            log.info("현재 대기 중인 사용자 수: {}", waitingUsers.size)

            // 매칭 시도
            matchUsers()
        }
    }

    // 사용자가 연결을 종료했을 때 호출
    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        val roomId = session.attributes["roomId"] as? String
        log.info("Closed roomId : {}", roomId)
        if (roomId != null) {
            // 방에서 나가는 사용자가 있는 경우 상대방에게 "나감" 메시지 전송
            val sessionsInRoom = textChatSessions[roomId]
            if (sessionsInRoom != null) {
                for (webSocketSession in sessionsInRoom.values) {
                    if (webSocketSession.id != session.id) {
                        sendMessage(
                            webSocketSession,
                            TextMessage("""{"type": "system", "message": "상대방이 채팅에서 나갔습니다."}""")
                        )
                    }
                }
            }

            // 종료시간 업데이트
            videoChatService.videoChatEndTimeLog(roomId)

            // 비디오 및 텍스트 채팅 세션에서 해당 사용자를 제거
            videoChatSessions[roomId]?.remove(session.id)
            textChatSessions[roomId]?.remove(session.id)

            // 방에 아무도 남아있지 않으면 방 자체를 제거
            if (videoChatSessions[roomId]?.isEmpty() == true) {
                videoChatSessions.remove(roomId)
            }
            if (textChatSessions[roomId]?.isEmpty() == true) {
                textChatSessions.remove(roomId)
            }

            log.info("세션이 방에서 제거되었습니다: {}", session.id)
        }

        // 대기열에서 세션 제거
        if (waitingUsers.remove(session)) {
            log.info("사용자{}가 대기열에서 제거되었습니다.", session.id)
        }
    }
}