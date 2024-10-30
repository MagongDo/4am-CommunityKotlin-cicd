package com.example.community_4am_kotlin.feature.comment.dto

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.domain.article.Comment
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CommentResponse (
    val articleId: Long?,
    val commentAuthor: String,
    val commentContent: String,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    val commentCreatedAt: LocalDateTime?,
    val commentId: Long?,
    val parentCommentId: Long?,
    val commentIsHidden: Boolean?,
    val commentIsDeleted: Boolean?
){
    constructor(comment: Comment): this(
        articleId=comment.getArticle().id!!,
        commentAuthor=comment.getCommentAuthor(),
        commentContent=comment.getCommentContent(),
        commentCreatedAt=comment.getCreatedDate(),
        commentIsHidden=comment.getCommentIsHidden(),
        commentIsDeleted=comment.getCommentIsDeleted(),
        commentId=comment.commentId,
        parentCommentId=comment.getParentComment()?.commentId
    )
}