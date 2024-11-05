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
//    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
//    fun addArticle(@RequestPart("request")request: AddArticleRequest,
//                   principal: Principal): ResponseEntity<Article> {
//        val savedArticle=articleService.save(request,principal.name,null)
//        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle)
//    }

    //새 글 등록 API (POST)
    @PostMapping(consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun createArticle(
        @RequestPart("request") request: AddArticleRequest,
        @RequestPart("files", required = false) files: List<MultipartFile>?,
        principal: Principal
    ): ResponseEntity<ArticleResponse> {
        return try {
            val userName = principal.name
            val createdArticle: Article = articleService.createArticle(request, userName, files)
            ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse(createdArticle))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    //임시 게시글 저장 API (POST)
    @PostMapping("/temporary", consumes = [org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE])
    fun saveTemporaryArticle(
        @RequestPart("request") request: AddArticleRequest,
        @RequestPart("files", required = false) files: List<MultipartFile>?,
        principal: Principal
    ): ResponseEntity<ArticleResponse> {
        return try {
            val userName = principal.name
            val savedTempArticle: Article = articleService.saveTemporaryArticle(request, userName, files)
            ResponseEntity.status(HttpStatus.CREATED).body(ArticleResponse(savedTempArticle))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
    }

    //임시 게시글 목록 조회 API (GET)
    @GetMapping("/temporary")
    fun getTemporaryArticles(principal: Principal): ResponseEntity<List<ArticleResponse>> {
        return try {
            val userName = principal.name
            val tempArticles: List<Article> = articleService.getTemporaryArticles(userName)
            val response = tempArticles.map { ArticleResponse(it) }
            ResponseEntity.ok(response)
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(emptyList())
        }
    }

    /**
     * 임시 게시글 삭제 API (DELETE)
     */
    @DeleteMapping("/temporary/{id}")
    fun deleteTemporaryArticle(
        @PathVariable("id") id: Long,
        principal: Principal
    ): ResponseEntity<Map<String, String>> {
        return try {
            val userName = principal.name
            articleService.deleteTemporaryArticle(id, userName)
            ResponseEntity.ok(mapOf("message" to "Temporary article deleted successfully"))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(mapOf("message" to  "Error deleting temporary article"))
        }
    }

    //임시 게시글을 실제 게시글로 전환 API (PUT)
    @PutMapping("/finalize-edit/{id}")
    fun finalizeTemporaryArticle(
        @PathVariable("id") id: Long,
        @RequestBody request: AddArticleRequest,
        principal: Principal
    ): ResponseEntity<ArticleResponse> {
        return try {
            val userName = principal.name
            val article = articleService.finalizeTemporaryArticle(id, request)
            if (article.author != userName) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null)
            }
            ResponseEntity.ok(ArticleResponse(article))
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null)
        }
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
//---------------
    // 글 수정 완료 시 임시 파일 반영 API
    @PutMapping("/{id}/finalize-edit")
    fun finalizeFilesEdit(@PathVariable("id") id: Long): ResponseEntity<Void> {
        articleService.finalizeFilesEdit(id)
        return ResponseEntity.ok().build()
    }

    // 글 수정 취소 시 임시 파일 삭제 API
    @PutMapping("/{id}/cancel-edit")
    fun cancelFilesEdit(@PathVariable("id") id: Long): ResponseEntity<Void> {
        articleService.cancelFilesEdit(id)
        return ResponseEntity.ok().build()
    }
//----------------


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