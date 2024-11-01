package com.example.community_4am_kotlin.feature.comment.dto

import com.example.community_4am_kotlin.domain.article.Comment

data class UpdateCommentRequest(
    val commentContent: String?,
    val commentIsHidden :Boolean?,
    val commentIsDeleted:Boolean?
) {
    constructor(comment: Comment):this(
        commentContent=comment.getCommentContents(),
        commentIsDeleted=comment.getCommentIsDeleteds(),
        commentIsHidden=comment.getCommentIsHiddens()
    )
}