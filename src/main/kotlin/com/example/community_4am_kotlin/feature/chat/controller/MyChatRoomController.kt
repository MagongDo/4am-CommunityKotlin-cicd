package com.example.community_4am_kotlin.feature.chat.controller

import com.example.community_4am_kotlin.domain.chat.ChatRoom
import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.chat.service.ChatRoomService
import com.example.community_4am_kotlin.feature.user.service.UserService
import com.example.community_4am_kotlin.log
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import java.security.Principal

@Controller
@RequestMapping("/chat")
class MyChatRoomController( private var userService: UserService,private var chatRoomService: ChatRoomService ) {

    //사용자 Id 추출
    @GetMapping("/page")
    fun chatMyPage(principal: Principal): String {
        val email = principal.name

        val user: User = userService.findByEmail(email)
        val userId: Long? = user.id
        return "redirect:/chat/page/$userId"
    }


    //사용자 아이디 기반으로 개인 채팅관리 페이지로 이동
    @GetMapping("/page/{userId}")
    fun chatPage(@PathVariable("userId") userId: Long?, model: Model): String {
        val user: User? = userId?.let { userService.findById(it) }
        //내가 참여한 채팅방 List
        //내가 만든 채팅방 list
        val create: List<ChatRoom?>? = chatRoomService.findByUsername(user?.username)
        log.info("list={}", create)
        //model로 넘기기
        model.addAttribute("create", create)
        return "/chatting/chatMyPage"
    }

    @DeleteMapping("/room/delete/{roomId}")
    fun deleteRoom(@PathVariable roomId: Long?): ResponseEntity<String> {
        try {
            roomId?.let { chatRoomService.delete(it) }
            return ResponseEntity("Room deleted successfully", HttpStatus.OK)
        } catch (e: Exception) {
            return ResponseEntity("Error deleting room", HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}