package com.example.Community_4am_Kotlin.feature.user.dto

import com.example.Community_4am_Kotlin.domain.article.Article
import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class UserArticlesList(
    var id :Long?,
    var title :String,
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime?,
    var viewCount:Long
){
    constructor(article: Article) : this(
        id=article.id,
        title=article.title,
        createdAt=article.createdAt,
        viewCount=article.viewCount
    )
}
