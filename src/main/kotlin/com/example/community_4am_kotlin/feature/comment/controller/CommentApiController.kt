package com.example.community_4am_kotlin.feature.comment.controller

import com.example.community_4am_Kotlin.domain.article.Comment
import com.example.community_4am_kotlin.feature.comment.dto.AddCommentRequest
import com.example.community_4am_kotlin.feature.comment.dto.CommentResponse
import com.example.community_4am_kotlin.feature.comment.dto.UpdateCommentRequest
import com.example.community_4am_kotlin.feature.comment.service.CommentService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.io.IOException
import java.security.Principal

@RestController
@RequestMapping("/api/comment")
class CommentApiController(
    private val commentService: CommentService,
    private val notificationService: NotificationService
) {
    private val logger = LogManager.getLogger(LikeService::class.java)

    //게시글에 맞는 한개 댓글 생성
    @PostMapping("/{articleId}")
    fun addComment(@PathVariable("articleId") articleId:Long,
                   @RequestBody request: AddCommentRequest,
                   principal: Principal): ResponseEntity<Comment> {
        val savedComment=commentService.saveComment(request, articleId, principal.name)
        val userName=principal.name

        try{
            notificationService.sendCommentNotification(articleId, userName);
        }catch(e:IOException){
            logger.error("댓글 알림 전송 실패: ${e.message}", e)
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment)
    }

    //게시글에 달린 댓글 목록 조회 (시간순)
    @GetMapping(value =["/{articleId}"],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findComments(@PathVariable("articleId") articleId: Long): ResponseEntity<List<CommentResponse>> {
        val comments=commentService.getComments(articleId)
        return ResponseEntity.status(HttpStatus.OK).body(comments)
    }

    //게시글에 맞는 한개 댓글과 대댓글 조회
    @GetMapping(value = ["/{articleId}/{commentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findReComents(@PathVariable("articleId") articleId:Long,@PathVariable("commentId") commentId: Long): ResponseEntity<List<CommentResponse>> {
        val comments=commentService.getReComments(articleId, commentId)
        return ResponseEntity.status(HttpStatus.OK).body(comments)
    }

    //4. 댓글 수정
    @PutMapping(value = ["/{commentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun updateComment(@PathVariable("commentId") commentId: Long,
                      @RequestBody request: UpdateCommentRequest):ResponseEntity<UpdateCommentRequest> {
        val updatedComment=commentService.updateComment(commentId, request)
        return ResponseEntity.status(HttpStatus.OK).body(updatedComment)
    }

    //5. 댓글 삭제
    @DeleteMapping(value = ["/{commentId}"], produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteComment(@PathVariable("commentId") commentId: Long):ResponseEntity<Void> {
        commentService.deleteComment(commentId)
        return ResponseEntity.status(HttpStatus.OK).build()
    }


}