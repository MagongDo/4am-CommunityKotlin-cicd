package com.example.community_4am_kotlin.feature.file.repository

import com.example.community_4am_kotlin.domain.article.InsertedFile
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository: JpaRepository<InsertedFile, Long> {
    fun findByArticleIdAndUuidFileName(articleId: Long, fileName: String) : InsertedFile?
}