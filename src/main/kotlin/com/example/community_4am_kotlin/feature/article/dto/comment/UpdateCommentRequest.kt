package com.example.Community_4am_Kotlin.feature.article.dto.comment

data class UpdateCommentRequest(
    var commentContent:String,
    var commentIsHidden:Boolean,
    var commentIsDeleted:Boolean
)
