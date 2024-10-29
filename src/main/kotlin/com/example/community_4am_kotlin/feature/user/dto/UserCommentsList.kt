package com.example.Community_4am_Kotlin.feature.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserCommentsList(
    var commentContent:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var commentCreatedAt: LocalDateTime,
    var articleTitle:String,
    var articleId:Long
)
