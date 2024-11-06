package com.example.community_4am_kotlin.feature.article.service

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.domain.article.InsertedFile
import com.example.community_4am_kotlin.feature.article.dto.*
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import com.example.community_4am_kotlin.feature.user.dto.UserArticlesList
import com.example.community_4am_kotlin.feature.user.service.UserService
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
    private val fileUploadService: FileUploadService,
    private val likeService: LikeService,
    private val userService: UserService
){

    // 글 등록 메서드: 게시글을 저장하고 첨부 파일을 처리하여 파일과 게시글을 연결
//    fun save(request: AddArticleRequest, userName: String, files: MutableList<MultipartFile>?): Article {
//        // 1. 우선 Article을 저장하여 ID 확보
//        var savedArticle = articleRepository.save(request.toEntity(userName))
//
//        // 2. 파일이 있을 경우 처리
//        files?.takeIf { it.isNotEmpty() }?.let {
//            val insertedFiles = fileUploadService.uploadFiles(it, savedArticle)
//            savedArticle.addFiles(insertedFiles)
//        }
//
//        // 3. 최종적으로 모든 변경사항 반영 및 저장
//        savedArticle = articleRepository.save(savedArticle)
//        return savedArticle
//    }
    //-----------------------
    //새 게시글 생성
    fun createArticle(request: AddArticleRequest, userName: String, files: List<MultipartFile>?): Article {
        val article = Article(
            title = request.title,
            content = request.content,
            author = userName,
            isTemporary = false
        )
        var savedArticle = articleRepository.save(article)

        // 파일 업로드 처리
        files?.takeIf { it.isNotEmpty() }?.let {
            val insertedFiles: List<InsertedFile> = fileUploadService.uploadFiles(it, savedArticle)
            savedArticle.addFiles(insertedFiles)
        }

        savedArticle = articleRepository.save(savedArticle)
        return savedArticle
    }

    //임시 게시글 저장
    @Transactional
    fun saveTemporaryArticle(request: AddArticleRequest, userName: String, files: List<MultipartFile>?): Article {
        val tempArticle = Article(
            title = request.title,
            content = request.content,
            author = userName,
            isTemporary = true
        )
        var savedArticle = articleRepository.save(tempArticle)

        // 파일 업로드 처리
        files?.takeIf { it.isNotEmpty() }?.let {
            val insertedFiles: List<InsertedFile> = fileUploadService.uploadFiles(it, savedArticle)
            savedArticle.addFiles(insertedFiles)
        }

        savedArticle = articleRepository.save(savedArticle)
        return savedArticle
    }

    //임시 게시글 목록 조회
    fun getTemporaryArticles(userName: String): List<Article> {
        return articleRepository.findByAuthorAndIsTemporary(userName, true)
    }

    //임시 게시글 삭제
    @Transactional
    fun deleteTemporaryArticle(articleId: Long, userName: String) {
        val article = articleRepository.findById(articleId).orElseThrow { IllegalArgumentException("Article not found") }
        if (article.author != userName || !article.isTemporary) {
            throw IllegalArgumentException("Not authorized to delete this temporary article")
        }
        // 관련 파일 삭제
        fileUploadService.deleteFiles(article.files)
        // 게시글 삭제
        articleRepository.delete(article)
    }

    //임시 게시글을 실제 게시글로 전환
    @Transactional
    fun finalizeTemporaryArticle(id: Long, request: AddArticleRequest, userName: String): Article {
        // 기존의 임시 글을 조회합니다.
        val article = articleRepository.findById(id).orElseThrow {
            throw IllegalArgumentException("Article not found")
        }

        // 작성자 확인
        if (article.author != userName) {
            throw IllegalAccessException("Unauthorized user")
        }

        // 제목과 내용을 요청 값으로 업데이트하고, isTemporary를 false로 설정
        article.title = request.title
        article.content = request.content
        article.isTemporary = false

        deleteTemporaryArticle(article.id!!, userName) //임시 목록에서 삭제
        // 기존 레코드를 수정하여 저장
        return articleRepository.save(article)
    }


//------------------------------

    // 모든 게시글 조회
    fun getArticle():List<ArticleResponse>{
        val articles = articleRepository.findAll()
        return  articles.map { ArticleResponse(it) }
    }

    // 특정 ID로 게시글 조회
    fun findById(id:Long): Article {
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
//---------------------------------------

    // 글 수정 시 임시로 파일 업로드
    @Transactional
    fun update(id: Long, request: UpdateArticleRequest, files: MutableList<MultipartFile>?): Article {
        val savedArticle = articleRepository.findById(id).orElseThrow { IllegalArgumentException("Article not found") }
        authorizeArticleAuthor(savedArticle)
        savedArticle.update(request.title, request.content)

        //----------- content에서 삭제 된 파일 db에서도 삭제하는 블록-------
        // 현재 content에서 사용 중인 파일명 추출
        val currentUuidFileNames = extractUuidFileNames(request.content)

        // 데이터베이스에 저장된 파일 중 content에 사용되지 않고 영구 저장된 파일만 삭제
        val filesToDelete = savedArticle.files.filter {
            it.uuidFileName !in currentUuidFileNames && !it.isTemporary
        }

        if (filesToDelete.isNotEmpty()) {
            // 필요 없는 파일 삭제
            fileUploadService.deleteFiles(filesToDelete)
            savedArticle.files.removeAll(filesToDelete)
        }
        //------------------------------------------------------

        // 파일이 존재하면 임시로 업로드
        files?.takeIf { it.isNotEmpty() }?.let {
            fileUploadService.uploadFilesTemporarily(it, savedArticle)
        }
        return articleRepository.save(savedArticle)
    }

    // 글 수정 완료 시 임시 파일 반영
    @Transactional
    fun finalizeFilesEdit(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow { IllegalArgumentException("Article not found") }
        fileUploadService.confirmTemporaryFiles(article) // 임시 파일을 확정
    }

    // 글 수정 취소 시 임시 파일 삭제
    @Transactional
    fun cancelFilesEdit(articleId: Long) {
        val article = articleRepository.findById(articleId).orElseThrow { IllegalArgumentException("Article not found") }
        fileUploadService.deleteTemporaryFiles(article) // 임시 파일 삭제
    }
//    ----------------------------

    fun getIncreaseViewCount(id:Long): Article {
        val article=articleRepository.findById(id).orElseThrow{IllegalArgumentException("not found: $id ")}
        article.isIncrementViewCount()
        return articleRepository.save(article)
    }

    // 게시글 목록 조회 메서드: 페이지네이션을 적용하여 게시글 목록을 조회
//    fun getList(pageRequestDTO: PageRequestDTO): Page<ArticleListViewResponse> {
//        val sort= Sort.by("id").descending()
//        val pageable: Pageable =pageRequestDTO.getPageable(sort)
//        return articleRepository.searchDTO(pageable)
//    }
// 닉네임을 가져와서 조회---------------------
    fun getList(pageRequestDTO: PageRequestDTO): Page<ArticleListViewResponse> {
        val sort = Sort.by("id").descending()
        val pageable: Pageable = pageRequestDTO.getPageable(sort)

        // 모든 Article 엔티티를 가져온 후, 닉네임을 포함한 DTO로 변환하여 반환
        val articles = articleRepository.findAll(pageable)

        return articles.map { article ->
            val nickname = userService.findByUsername(article.author).nickname?:"" // 닉네임 조회
            ArticleListViewResponse(article, nickname) // 닉네임 포함한 DTO 생성
        }
    }
//-----------------------
    //사용자가 작성한 목록 조회
    fun getUserAllArticles(userName:String):List<UserArticlesList>{
        val articles=articleRepository.findUserArticles(userName)
        return articles.map { it.id?.let { it1 -> it.createdAt?.let { it2 ->
            UserArticlesList(it1,it.title,
                it2,it.viewCount)
        } }!! }
    }

    //content에서 사용 중인 파일명 추출
    fun extractUuidFileNames(content: String): List<String> {
        val regex = Regex("uuidFileName=([\\w-]+)")  // uuidFileName에 해당하는 값을 추출하는 정규식
        return regex.findAll(content)
            .map { it.groupValues[1] } // uuidFileName만 추출
            .toList()
    }


    // 게시글의 작성자를 확인하여 권한 검증
    fun authorizeArticleAuthor(article: Article) {
        val userName:String= SecurityContextHolder.getContext().authentication.name
        if(article.author!=userName){
            throw IllegalArgumentException("not authorized")
        }
    }


}