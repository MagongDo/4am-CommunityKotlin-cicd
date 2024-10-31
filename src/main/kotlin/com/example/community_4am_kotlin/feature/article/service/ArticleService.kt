package com.example.community_4am_kotlin.feature.article.service

import com.example.community_4am_Kotlin.domain.article.Article
import com.example.community_4am_Kotlin.feature.article.dto.*
import com.example.community_4am_Kotlin.feature.user.dto.UserArticlesList
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.comment.repository.CommentRepository
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import org.apache.coyote.http11.Constants.a
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
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
    private val likeService: LikeService,
    private val modelMapper: ModelMapper
){
    // 글 등록 메서드: 게시글을 저장하고 첨부 파일을 처리하여 파일과 게시글을 연결
    fun save(request: AddArticleRequest,userName:String, files:MutableList<MultipartFile>) : Article {
        val savedArticle=modelMapper.map(request,Article::class.java).apply { author=userName }
        articleRepository.save(savedArticle)
        files?.takeIf { it.isNotEmpty() }?.let {
            val insertedFiles = fileUploadService.uploadFiles(it, savedArticle)
            savedArticle.addFiles(insertedFiles)
        }
        return savedArticle
    }

    // 모든 게시글 조회
    fun getArticle():List<ArticleResponse>{
        val articles = articleRepository.findAll()
        return  articles.map { ArticleResponse(it) }
    }

    // 특정 ID로 게시글 조회
    fun findById(id:Long):Article{
        val article=articleRepository.findById(id).orElseThrow{IllegalArgumentException("article not found")}
        val likeCount=likeService.getLikeCount(id)

        article.changeLikeCount(likeCount)
        articleRepository.save(article)

        return article
    }

    // 게시글 삭제 메서드: 게시글을 작성한 사용자만 삭제 가능
    fun delete(id: Long){
        val article=articleRepository.findById(id).orElseThrow{IllegalArgumentException("not found: $id")}
        authorizeArticleAuthor(article)
        articleRepository.delete(article)
    }

    // 게시글 수정 메서드: 내용과 파일을 수정 가능
    fun update(id:Long,request: UpdateArticleRequest,files:MutableList<MultipartFile>):Article{
        val savedArticle=articleRepository.findById(id).orElseThrow{IllegalArgumentException("article not found")}
        authorizeArticleAuthor(savedArticle)
        savedArticle.update(request.title,request.content)

        files?.takeIf { it.isNotEmpty() }?.let {
            val insertedFiles = fileUploadService.uploadFiles(it, savedArticle)
            savedArticle.addFiles(insertedFiles)
        }

        return savedArticle
    }

    fun getIncreaseViewCount(id:Long):Article{
        val article=articleRepository.findById(id).orElseThrow{IllegalArgumentException("not found: $id ")}
        article.isIncrementViewCount()
        return articleRepository.save(article)
    }

    // 게시글 목록 조회 메서드: 페이지네이션을 적용하여 게시글 목록을 조회
    fun getList(pageRequestDTO: PageRequestDTO): Page<ArticleListViewResponse> {
        val sort= Sort.by("id").descending()
        val pageable: Pageable =pageRequestDTO.getPageable(sort)
        return articleRepository.searchDTO(pageable)
    }

    //사용자가 작성한 목록 조회
    fun getUserAllArticles(userName:String):List<UserArticlesList>{
        val articles=articleRepository.findUserArticles(userName)
        return articles.map { UserArticlesList(it.id,it.title,it.createdAt,it.viewCount) }
    }

    // 게시글의 작성자를 확인하여 권한 검증
    fun authorizeArticleAuthor(article: Article) {
        val userName:String= SecurityContextHolder.getContext().authentication.name
        if(article.author!=userName){
            throw IllegalArgumentException("not authorized")
        }
    }


}