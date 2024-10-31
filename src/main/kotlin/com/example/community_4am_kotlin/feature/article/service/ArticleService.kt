package com.example.community_4am_kotlin.feature.article.service

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.feature.article.dto.AddArticleRequest
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.comment.repository.CommentRepository
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import org.apache.coyote.http11.Constants.a
import org.modelmapper.ModelMapper
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
@Transactional
class ArticleService (
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val fileUploadService: FileUploadService,
    private val modelMapper: ModelMapper
){
    fun save(request: AddArticleRequest,userName:String, files:MutableList<MultipartFile>) : Article {
        val savedArticle=modelMapper.map(request,Article::class.java).apply { author=userName }

        articleRepository.save(savedArticle)
        files?.takeIf { it.isNotEmpty() }?.let {
            val insertedFiles = fileUploadService.uploadFiles(it, savedArticle)
            savedArticle.addFiles(insertedFiles)
        }

        return savedArticle
    }




    // 게시글의 작성자를 확인하여 권한 검증
    fun authorizeArticleAuthor(article: Article) {
        val userName:String= SecurityContextHolder.getContext().authentication.name
        if(article.author!=userName){
            throw IllegalArgumentException("not authorized")
        }
    }


}