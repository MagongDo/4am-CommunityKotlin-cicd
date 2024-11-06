package com.example.community_4am_kotlin.feature.comment.dto

import com.example.community_4am_kotlin.domain.article.Comment

data class CommentWithProfile(
    val comment: Comment,
    val profileImage: String?

)