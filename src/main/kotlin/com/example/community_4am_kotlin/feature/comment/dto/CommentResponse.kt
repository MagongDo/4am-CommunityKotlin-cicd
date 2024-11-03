package com.example.community_4am_kotlin.feature.comment.dto

import com.example.community_4am_kotlin.domain.article.Comment
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CommentResponse(
    val articleId: Long?,
    val commentAuthor: String?,
    val commentContent: String?,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    val commentCreatedAt: LocalDateTime?,
    val commentId: Long?,
    val parentCommentId: Long?,
    val commentIsHidden: Boolean?,
    val commentIsDeleted: Boolean?
) {
    constructor(comment: Comment) : this(
        articleId = comment.article.id!!,  // `id`가 null이 아닐 것으로 가정
        commentAuthor = comment.commentAuthor,
        commentContent = comment.commentContent,
        commentCreatedAt = comment.createdAt,
        commentIsHidden = comment.commentIsHidden,
        commentIsDeleted = comment.commentIsDeleted,
        commentId = comment.commentId,
        parentCommentId = comment.parentComment?.commentId
    )
}
