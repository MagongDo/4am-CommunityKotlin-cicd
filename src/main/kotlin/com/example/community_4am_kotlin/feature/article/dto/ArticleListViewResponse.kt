package com.example.community_4am_kotlin.feature.article.dto

import com.example.community_4am_kotlin.domain.article.Article

import java.time.LocalDateTime

// 게시글 목록 데이터를 담고 있는 DTO
data class ArticleListViewResponse(
    val id: Long?,
    val title: String,
    val content: String,
    val author: String,
    val createdAt: LocalDateTime?,
    val viewCount: Long,
    val likeCount: Long
) {
    constructor(article: Article) : this(
        id = article.id,
        title = article.title,
        content = article.content,
        author = article.author,
        createdAt = article.createdDate,
        viewCount = article.viewCount,
        likeCount = article.likeCount
    )
}