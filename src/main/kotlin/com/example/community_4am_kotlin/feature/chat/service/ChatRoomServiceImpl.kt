package com.example.community_4am_kotlin.feature.chat.service

import com.example.community_4am_kotlin.domain.chat.ChatRoom
import com.example.community_4am_kotlin.feature.chat.dto.CreateChatForm
import com.example.community_4am_kotlin.feature.chat.repository.ChatRoomRepository
import com.example.community_4am_kotlin.feature.chat.repository.MessageRepository
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatRoomServiceImpl(private val chatRoomRepository: ChatRoomRepository, private val messageRepository: MessageRepository) : ChatRoomService {
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
        messageRepository.deleteByChatRoom_Id(id)
        chatRoomRepository.deleteById(id)
    }
}