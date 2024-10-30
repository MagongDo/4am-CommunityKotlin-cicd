package com.example.community_4am_kotlin.feature.article.repository

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.community_4am_kotlin.feature.article.repository.search.ArticleSearch
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface ArticleRepository: JpaRepository<Article, Long>,ArticleSearch {
    @Query("SELECT a FROM Article a WHERE a.author = :email ORDER BY a.id DESC ")
    fun findUserArticles(@Param("email")email:String): List<Article>

    @Modifying
    @Transactional
    @Query("UPDATE Article a SET a.author = '탈퇴한 사용자입니다.' WHERE a.author = :email")
    fun updateAuthorToDeleted(@Param("email")email:String)


}