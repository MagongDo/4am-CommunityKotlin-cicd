package com.example.community_4am_kotlin.feature.article.dto

import com.example.community_4am_kotlin.domain.article.Article


// 클라이언트로 전달할 게시글 데이터를 담는 DTO
//data class ArticleResponse(
//    val title: String,
//    val content: String
//) {
//    constructor(article: Article) : this(
//        title = article.title,
//        content = article.content
//    )
//}

data class ArticleResponse(
    val id: Long?,
    val title: String,
    val content: String,
    val author: String,
    val isTemporary: Boolean
) {
    constructor(article: Article) : this(
        id = article.id,
        title = article.title,
        content = article.content,
        author = article.author,
        isTemporary = article.isTemporary
    )
}