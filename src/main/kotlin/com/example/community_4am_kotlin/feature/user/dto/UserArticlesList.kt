package com.example.community_4am_kotlin.feature.user.dto

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserArticlesList(
    var id :Long,
    var title :String,
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime,
    var viewCount:Long
)
