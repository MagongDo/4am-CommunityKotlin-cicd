package com.example.community_4am_kotlin.feature.file.repository

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.domain.article.InsertedFile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface FileRepository: JpaRepository<InsertedFile, Long> {
    fun findByArticleIdAndUuidFileName(articleId: Long, fileName: String) : InsertedFile?


    // isTemporary가 true인 파일 목록 조회
    fun findByArticleAndIsTemporary(article: Article, isTemporary: Boolean): List<InsertedFile>

    // isTemporary가 true인 파일 삭제
  //  fun deleteByArticleAndIsTemporary(article: Article, isTemporary: Boolean)

    @Modifying
    @Query("DELETE FROM InsertedFile f WHERE f.article = :article AND f.isTemporary = true")
    fun deleteByArticleAndIsTemporary(article: Article)

    fun findByArticleId(articleId: Long): List<InsertedFile>

}