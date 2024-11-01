package com.example.community_4am_kotlin.feature.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserCommentsList(
    var commentContent:String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    var commentCreatedAt: LocalDateTime,
    var articleTitle:String,
    var articleId:Long
)
