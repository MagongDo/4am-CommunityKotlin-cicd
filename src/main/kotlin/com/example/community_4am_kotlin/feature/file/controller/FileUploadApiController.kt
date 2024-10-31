package com.example.community_4am_kotlin.feature.file.controller

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.community_4am_kotlin.feature.article.service.ArticleService
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/upload")
class FileUploadApiController(
    private val articleService: ArticleService,
    private val fileUploadService: FileUploadService
) {
    @PostMapping
    fun uploadFile(@RequestParam("upload") file: MultipartFile,@RequestParam articleId:Long)=run {
//        val article: Article =articleService.
//
//
//        ResponseEntity.ok(response)
    }
}