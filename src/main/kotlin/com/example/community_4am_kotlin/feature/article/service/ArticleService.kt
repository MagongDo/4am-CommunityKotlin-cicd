package com.example.community_4am_kotlin.feature.article.service

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.feature.article.dto.AddArticleRequest
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.comment.repository.CommentRepository
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile


@Service
@Transactional
class ArticleService (
    private val articleRepository: ArticleRepository,
    private val commentRepository: CommentRepository,
    private val fileUpoadService: FileUploadService,
    private val modelMapper: ModelMapper
){
    fun save(request: AddArticleRequest,userName:String, files:List<MultipartFile>) : Article {
        val article=modelMapper.map(request,Article::class.java)
        if(files!=null && !files.isEmpty()) {
            val insertedFiles:MutableList<Article> = mutableListOf()
        }
    }


}