package com.example.community_4am_kotlin.feature.friend.controller

import com.example.community_4am_kotlin.domain.friend.Friend
import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.friend.dto.FriendDTO
import com.example.community_4am_kotlin.feature.friend.service.FriendService
import com.example.community_4am_kotlin.feature.notification.service.NotificationService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/friends")
class FriendController(
    private val friendService: FriendService,
    private val userService: UserService,
    private val notificationService: NotificationService
) {

    // 온라인/오프라인 상태에 따라 정렬된 친구 목록 조회
    @GetMapping("/list")
    fun getSortedFriendList(@AuthenticationPrincipal principal: Principal): ResponseEntity<List<FriendDTO>> {
        val user = userService.findByEmail(principal.name)
        val friends = friendService.getSortedFriendList(user.id)

        val friendDtos = friends.map { friend ->
            FriendDTO(
                id = friend.id,
                name = friend.email,
                status = friend.status
            )
        }
        return ResponseEntity.ok(friendDtos)
    }

    // 친구 요청 수락
    @PutMapping("/requests/{notificationId}/accept")
    fun acceptFriendRequest(@PathVariable notificationId: Long): ResponseEntity<Void> {
        friendService.acceptFriendRequest(notificationId)
        return ResponseEntity.ok().build()
    }

    // 친구 요청 거절 및 삭제
    @PutMapping("/requests/{notificationId}/reject")
    fun rejectFriendRequest(@PathVariable notificationId: Long): ResponseEntity<Void> {
        friendService.rejectFriendRequest(notificationId)
        return ResponseEntity.noContent().build()
    }

    // 특정 이메일로 친구 이메일 목록 조회
    @GetMapping("/emails")
    fun getFriendEmails(@AuthenticationPrincipal principal: Principal): ResponseEntity<List<String>> {
        val user = userService.findByEmail(principal.name)
        val friendEmails = friendService.getAcceptedFriendEmails(user)
        return ResponseEntity.ok(friendEmails)
    }

    // 이메일로 친구 검색
    @GetMapping("/friends/search")
    fun searchFriendByEmail(
        @RequestParam email: String
    ): ResponseEntity<List<User>> {
        return try {
            val friends = userService.searchUsersByEmailStartingWith(email)
            if (friends.isNotEmpty()) {
                ResponseEntity.ok(friends)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND).build()
            }
        } catch (e: Exception) {
            println("친구 검색 중 오류 발생: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }

    // 친구 요청 보내기
    @PostMapping("/friends/request")
    fun sendFriendRequest(
        @RequestParam friendEmail: String,
        @AuthenticationPrincipal principal: Principal
    ): ResponseEntity<Void> {
        return try {
            val fromEmail = principal.name
            notificationService.sendFriendNotification(friendEmail, fromEmail)
            ResponseEntity.ok().build()
        } catch (e: Exception) {
            println("친구 요청 전송 중 오류 발생: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()
        }
    }
}