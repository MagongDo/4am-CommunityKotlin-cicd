package com.example.Community_4am_Kotlin.feature.article.dto

import com.example.community_4am_kotlin.domain.article.Article
import java.time.LocalDateTime

// 게시글을 조회할 때 클라이언트에게 반환할 데이터를 담기 위한 DTO
data class ArticleViewResponse(
    val id: Long?,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: LocalDateTime,
    val isOwner: Boolean = false
) {
    constructor(article: Article) : this(
        id = article.id,
        title = article.title,
        content = article.content,
        author = article.author,
        createdAt = article.createdDate
    )

    constructor(article: Article, currentUserName: String) : this(
        id = article.id,
        title = article.title,
        content = article.content,
        author = article.author,
        createdAt = article.createdDate,
        isOwner = currentUserName == article.author
    )
}