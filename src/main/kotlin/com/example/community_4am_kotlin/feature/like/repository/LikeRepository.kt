package com.example.community_4am_kotlin.feature.like.repository

import com.example.community_4am_Kotlin.domain.article.Article
import com.example.community_4am_Kotlin.domain.article.Like
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.*

interface LikeRepository: JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.article.id = :articleId AND l.user.id = :userId")
    fun findByArticleAndUser(@Param("articleId") articleId: Long,@Param("userId") userId: String): Optional<Like>

    @Query("SELECT COUNT(l) FROM Like l WHERE l.article.id = :articleId AND l.likedStatus=true")
    fun countLikesByArticleId(@Param("articleId") articleId: Long): Long

    @Query("SELECT DISTINCT a FROM Like l JOIN l.article a JOIN l.user u WHERE l.likedStatus = true AND u.email = :email ORDER BY a.createdAt DESC")
    fun findUserLikedArticles(@Param("email") email: String): List<Article>
}