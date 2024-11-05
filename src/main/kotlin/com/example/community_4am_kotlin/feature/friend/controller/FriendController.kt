package com.example.community_4am_kotlin.feature.friend.controller

import com.example.community_4am_kotlin.feature.friend.dto.FriendDTO
import com.example.community_4am_kotlin.feature.friend.service.FriendService
import com.example.community_4am_kotlin.feature.notification.service.NotificationService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
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
    fun getSortedFriendList(authentication: Authentication?): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        val user = userService.findByEmail(authentication.name)
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


    @GetMapping("/unread-count/friend")
    fun getUnreadFriendNotificationsCount(principal: Principal): ResponseEntity<Map<String, Long>> {
        val username = principal.name
        var unreadCount:Long=notificationService.getUnreadFriendNotificationsCount(username)
        var response:MutableMap<String,Long> = HashMap()
        response.put("unreadCount", unreadCount)
        return ResponseEntity.ok(response)
    }

    // 친구 요청 수락
    @PutMapping("/requests/{notificationId}/accept")
    fun acceptFriendRequest(
        @PathVariable notificationId: Long,
        authentication: Authentication?
    ): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        friendService.acceptFriendRequest(notificationId)
        return ResponseEntity.ok().build()
    }

    // 친구 요청 거절 및 삭제
    @PutMapping("/requests/{notificationId}/reject")
    fun rejectFriendRequest(
        @PathVariable notificationId: Long,
        authentication: Authentication?
    ): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        friendService.rejectFriendRequest(notificationId)
        return ResponseEntity.noContent().build()
    }

    // 특정 이메일로 친구 이메일 목록 조회
    @GetMapping("/emails")
    fun getFriendEmails(authentication: Authentication?): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        val user = userService.findByEmail(authentication.name)
        val friendEmails = friendService.getAcceptedFriendEmails(user)
        return ResponseEntity.ok(friendEmails)
    }

    // 이메일로 친구 검색
    @GetMapping("/search")
    fun searchFriendByEmail(
        @RequestParam email: String,
        principal: Principal
    ): ResponseEntity<Any> {
        return try {
            // 현재 로그인한 사용자의 정보
            val currentUser = userService.findByEmail(principal.name)

            // 검색 결과 가져오기 (이메일 접두사로 시작하는 사용자)
            val friends = userService.searchUsersByEmailStartingWith(email)

            // 현재 사용자의 친구 목록 가져오기
            val currentUserFriends = friendService.getFriendEmail(currentUser.id).map { it.friend.email }

            if (friends.isNotEmpty()) {
                // 본인과 친구 목록에 포함된 사용자를 제외하고 DTO로 변환하여 반환
                val friendDtos = friends.filter { user ->
                    user.email != principal.name && !currentUserFriends.contains(user.email)
                }.map { user ->
                    FriendDTO(
                        id = user.id,
                        name = user.email, // 이름이 없으면 이메일로 대체
                        status = user.status
                    )
                }

                ResponseEntity.ok(friendDtos)
            } else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(mapOf("message" to "친구를 찾을 수 없습니다."))
            }
        } catch (e: Exception) {
            println("친구 검색 중 오류 발생: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "서버 오류가 발생했습니다."))
        }
    }

    // 친구 요청 보내기
    @PostMapping("/request")
    fun sendFriendRequest(
        @RequestParam friendEmail: String,
        authentication: Authentication?
    ): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        return try {
            val fromEmail = authentication.name
            notificationService.sendFriendNotification(friendEmail, fromEmail)
            ResponseEntity.ok().body(mapOf("message" to "친구 요청이 전송되었습니다."))
        } catch (e: Exception) {
            println("친구 요청 전송 중 오류 발생: ${e.message}")
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "친구 요청 전송 중 오류가 발생했습니다."))
        }
    }

    @GetMapping("/requests")
    fun getFriendRequests(authentication: Authentication?): ResponseEntity<Any> {
        if (authentication == null || !authentication.isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(mapOf("message" to "로그인이 필요합니다."))
        }

        val user = userService.findByEmail(authentication.name)
        val friendRequests = friendService.getFriendRequests(user)
        return ResponseEntity.ok(friendRequests)
    }
}
