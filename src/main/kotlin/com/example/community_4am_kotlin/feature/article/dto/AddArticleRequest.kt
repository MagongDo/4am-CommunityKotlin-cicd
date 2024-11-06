package com.example.community_4am_kotlin.feature.article.dto

import com.example.community_4am_kotlin.domain.article.Article

data class AddArticleRequest(
    val title: String ,
    val content: String
) {
    fun toEntity(author: String): Article {
        return Article(
            title = title,
            content = content,
            author = author,
            viewCount = 0L,
            likeCount = 0L
        )
    }
}