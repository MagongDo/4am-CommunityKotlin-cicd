package com.example.community_4am_kotlin.feature.like.controller

import com.example.community_4am_kotlin.feature.like.dto.LikeRequest
import com.example.community_4am_kotlin.feature.like.service.LikeService
import org.apache.logging.log4j.LogManager
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.security.Principal

@RestController
@RequestMapping("/api/like")
class LikeApiController(
    private val likeService: LikeService,
    private val notificationService: NotificationService
) {

    private val logger = LogManager.getLogger(LikeApiController::class.java)

    /**
     * 좋아요 상태를 토글하고, 현재 좋아요 상태와 좋아요 수를 반환합니다.
     *
     * @param request 좋아요 요청 정보
     * @param principal 현재 인증된 사용자 정보
     * @return 현재 좋아요 상태와 좋아요 수를 담은 응답
     */
    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun toggleLike(
        @RequestBody request: LikeRequest,
        principal: Principal
    ): ResponseEntity<Map<String, Any>> {
        // 좋아요 상태를 토글하고 현재 상태를 반환
        val likedStatus = likeService.addLike(request.articleId, principal.name)
        // 현재 게시물의 좋아요 수를 조회
        val likeCount = likeService.getLikeCount(request.articleId)

        logger.info("ArticleId: {}", request.articleId)

        // 응답에 포함할 데이터 생성
        val response: MutableMap<String, Any> = mutableMapOf()
        response["likedStatus"] = likedStatus
        response["likeCount"] = likeCount

        return ResponseEntity.ok(response)
    }

    /**
     * 현재 사용자의 특정 게시물에 대한 좋아요 상태를 조회합니다.
     *
     * @param articleId 좋아요 상태를 조회할 게시물의 ID
     * @param principal 현재 인증된 사용자 정보
     * @return 현재 좋아요 상태를 담은 응답
     */
    @GetMapping("/status")
    fun getLikeStatus(
        @RequestParam articleId: Long,
        principal: Principal
    ): ResponseEntity<Map<String, Boolean>> {
        // 현재 사용자의 좋아요 상태를 확인
        val likedStatus = likeService.checkLikeStatus(articleId, principal.name)

        // 응답에 포함할 데이터 생성
        val response: MutableMap<String, Boolean> = mutableMapOf()
        response["likedStatus"] = likedStatus

        return ResponseEntity.ok(response)
    }
}