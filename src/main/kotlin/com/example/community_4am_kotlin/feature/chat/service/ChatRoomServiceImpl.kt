package com.example.community_4am_kotlin.feature.chat.service

import com.example.community_4am_kotlin.domain.chat.ChatRoom
import com.example.community_4am_kotlin.feature.chat.common.WebSocketSessionManager
import com.example.community_4am_kotlin.feature.chat.dto.CreateChatForm
import com.example.community_4am_kotlin.feature.chat.repository.ChatRoomRepository
import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import org.springframework.stereotype.Service
import org.springframework.web.socket.WebSocketSession
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatRoomServiceImpl(private val chatRoomRepository: ChatRoomRepository,private val messageRepository: MessageRepository ,private val roomSessions: ConcurrentHashMap<String, MutableMap<String, WebSocketSession>>, private val webSocketSessionManager: WebSocketSessionManager) : ChatRoomService {
    //반환 Unit == void
    override fun save(createChatForm: CreateChatForm, email : String) : Unit {
        createChatForm?.let {
            val chatRoom = ChatRoom(
                roomName = createChatForm.roomName,
                email = email,
                description = createChatForm.description,
                createdDate = LocalDateTime.now()
            )
            chatRoomRepository.save(chatRoom)
        }

    }

    override fun list(): List<ChatRoom?>? = chatRoomRepository.findAll()

    override fun findByUsername(username: String?): List<ChatRoom?>? {
        val list: List<ChatRoom> = chatRoomRepository.findAll()
        return list.filter { it.email == username }
    }

    //이부분 다시 리펙토링 진행하기 : cascade
    override fun delete(id: Long) {
        // 데이터베이스에서 해당 채팅방의 메시지 및 채팅방 자체 삭제
        messageRepository.deleteByChatRoom_Id(id)
        chatRoomRepository.deleteById(id)

        // 메모리와 Redis에서 해당 채팅방의 세션 정보 삭제
        roomSessions[id.toString()]?.keys?.forEach { sessionId ->
            webSocketSessionManager.deleteSession(sessionId, id.toString())  // Redis에서 세션 정보 삭제
        }
        roomSessions.remove(id.toString()) // 메모리에서 채팅방 세션 정보 삭제
    }
}