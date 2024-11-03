package com.example.community_4am_kotlin.feature.article.controller

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.feature.article.dto.AddArticleRequest
import com.example.community_4am_kotlin.feature.article.dto.ArticleResponse
import com.example.community_4am_kotlin.feature.article.dto.UpdateArticleRequest
import com.example.community_4am_kotlin.feature.article.service.ArticleService
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.security.Principal

@CrossOrigin(origins = ["http://localhost:8080"])
@RestController
@RequestMapping("/api/article")
class ArticleApiController(
    private val articleService: ArticleService,
    private val fileUploadService: FileUploadService,
) {
    private val logger = LogManager.getLogger(LikeService::class.java)

    // 게시글 등록 API (POST)
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun addArticle(@RequestPart("request")request: AddArticleRequest,
                   principal: Principal): ResponseEntity<Article> {
        val savedArticle=articleService.save(request,principal.name,null)
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle)
    }

    // 특정 게시글 조회 API (GET)
    @GetMapping(value = ["/{id}"],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun findArticleById(@PathVariable("id") id: Long): ResponseEntity<ArticleResponse> {
        val article=articleService.findById(id)
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        return ResponseEntity.ok().body(ArticleResponse(article))
    }

    // 게시글 수정 API (PUT)
    @PutMapping(value = ["/{id}"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun updateArticle(
        @PathVariable("id") id: Long,
        @RequestPart("request") request: UpdateArticleRequest,
        @RequestPart(value = "files", required = false) files: MutableList<MultipartFile>?): ResponseEntity<Article> {
        val updatedArticle = articleService.update(id, request, files) // `files`가 null이더라도 `update` 호출
        return ResponseEntity.ok().body(updatedArticle)
    }



    // 게시글 삭제 API (DELETE)
    @DeleteMapping(value = ["/{id}"],produces = [MediaType.APPLICATION_JSON_VALUE])
    fun deleteArticle(@PathVariable("id") id: Long): ResponseEntity<Map<String,String>> {
        return try{
            articleService.delete(id)
            ResponseEntity.ok().body(mapOf("message" to "Article deleted successfully"))
        }catch (e: Exception){
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(mapOf("message" to "Error deleting article"))
        }
    }



}