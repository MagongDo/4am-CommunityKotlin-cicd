package com.example.Community_4am_Kotlin.feature.article.dto

import com.example.Community_4am_Kotlin.domain.article.Article


// 클라이언트로 전달할 게시글 데이터를 담는 DTO
data class ArticleResponse(
    val title: String,
    val content: String
) {
    constructor(article: Article) : this(
        title = article.title,
        content = article.content
    )
}
