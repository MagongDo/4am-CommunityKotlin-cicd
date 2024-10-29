package com.example.Community_4am_Kotlin.feature.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserCommentedArticlesList(
    var id :Long,
    var title: String,
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime,
    var viewCount:Long
)
