package com.example.community_4am_kotlin.feature.comment.dto

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.domain.article.Comment

data class AddCommentRequest(
    val commentContent: String,
    val parentCommentId: Long? = null  // 오타 수정
) {
    fun toEntity(commentAuthor: String, article: Article, parentComment: Comment? = null): Comment {
        return Comment(
            commentAuthor = commentAuthor,
            commentContent = commentContent,
            article = article,
            parentComment = parentComment  // parentComment가 null일 수 있도록 처리
        )
    }
}