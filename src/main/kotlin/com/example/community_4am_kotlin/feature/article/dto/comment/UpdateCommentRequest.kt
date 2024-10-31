package com.example.community_4am_kotlin.feature.article.dto.comment

data class UpdateCommentRequest(
    var commentContent:String,
    var commentIsHidden:Boolean,
    var commentIsDeleted:Boolean
)
