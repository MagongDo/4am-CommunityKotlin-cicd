package com.example.community_4am_kotlin.feature.chat.controller

import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.chat.service.ChatService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping("/chat")
class ChatRoomController(
    private val chatService: ChatService,
    private val userService: UserService
) {
    @GetMapping("/room/{roomId}")
    fun chatRoom(
        @PathVariable roomId: Long?, model: Model,
        principal: Principal
    ): String {
        val user: User = userService.findByEmail(principal.name)
        val nickname: String? = user.nickname

        model.addAttribute("username", principal.name)
        model.addAttribute("roomId", roomId)
        model.addAttribute("nickname", nickname)

        return "chatting/chatRoom"
    }
}