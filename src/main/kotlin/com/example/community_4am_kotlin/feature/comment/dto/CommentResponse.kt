package com.example.community_4am_kotlin.feature.comment.dto

import com.example.community_4am_kotlin.domain.article.Comment
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CommentResponse (
    val articleId: Long?,
    val commentAuthor: String?,
    val commentContent: String?,
    @field:JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd HH:mm:ss")
    val commentCreatedAt: LocalDateTime?,
    val commentId: Long?,
    val parentCommentId: Long?,
    val commentIsHidden: Boolean?,
    val commentIsDeleted: Boolean?
){
    constructor(comment: Comment): this(
        articleId=comment.getArticles().id!!,
        commentAuthor=comment.getCommentAuthors(),
        commentContent=comment.getCommentContents(),
        commentCreatedAt=comment.getCreatedDates(),
        commentIsHidden=comment.getCommentIsHiddens(),
        commentIsDeleted=comment.getCommentIsDeleteds(),
        commentId=comment.commentId,
        parentCommentId=comment.getParentComments()?.commentId
    )
}