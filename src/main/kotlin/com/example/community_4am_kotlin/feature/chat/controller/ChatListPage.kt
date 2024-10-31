package com.example.community_4am_kotlin.feature.chat.controller

import com.example.community_4am_kotlin.feature.chat.dto.CreateChatForm
import com.example.community_4am_kotlin.feature.chat.service.ChatRoomService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping("/chat")
class ChatListPage(private val chatRoomService : ChatRoomService, private val userService : UserService) {


    @GetMapping("/list")
    fun index(model: Model): String {
        model.addAttribute("list", chatRoomService.list())
        return "chatting/chatRoomList"
    }

    @GetMapping("/create")
    fun create(): String {
        return "chatting/chatRoomCreate"
    }


    @PostMapping("/createRoom")
    fun createRoom(createChatForm: CreateChatForm, principal: Principal): String {
        val email = principal.name
        chatRoomService.save(createChatForm,email)
        return "redirect:/chat/list"
    }

}