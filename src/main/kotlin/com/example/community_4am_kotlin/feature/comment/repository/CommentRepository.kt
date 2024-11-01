package com.example.community_4am_kotlin.feature.comment.repository

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.domain.article.Comment
import com.example.community_4am_kotlin.feature.comment.dto.CommentListViewResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface CommentRepository: JpaRepository<Comment, Long> {
    // 게시글에 속한 모든 댓글과 대댓글을 commentId 순으로 가져옴
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.parentComment " +
                "WHERE c.article.id = :articleId ORDER BY c.commentId ASC")
    fun findByArticleIdOrderByCommentIdAsc(@Param("articleId") articleId:Long): List<Comment>

//    @Query("SELECT c FROM Comment c WHERE c.article.id=:articleId")
//    fun findByArticleId(@Param("articleId") articleId:Long): List<Comment>

    @Query("SELECT c FROM Comment c WHERE c.article.id=:articleId")
    fun list(@Param("articleId") articleId:Long,pageable: Pageable): Page<CommentListViewResponse>

    //특정 게시글의 특정 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.article.id = :articleId AND c.commentId = :commentId")
    fun findByCommentId(@Param("articleId") articleId:Long,@Param("commentId") commentId:Long): List<Comment>

    // 특정 부모 댓글과 그 자식 댓글을 모두 조회, commentId 순으로 정렬
    @Query("SELECT c FROM Comment c " +
                "WHERE c.article.id = :articleId " +
                "AND (c.commentId = :commentId OR c.parentComment.commentId = :commentId) " +
                "ORDER BY c.commentId ASC")
    fun finsParentAndChildCommnetsByArticleId(@Param("articleId") articleId:Long,@Param("commentId") commentId:Long): List<Comment>

     @Query("SELECT COUNT(c) FROM Comment c WHERE c.article.id = :articleId AND c.commentIsHidden = false AND c.commentIsDeleted = false")
     fun countCommentsByArticleId(@Param("articleId") articleId:Long):Long

    //사용자가 작성한 댓글과 댓글의 게시물 조회
    @Query("SELECT c,a FROM Comment c JOIN c.article a WHERE c.commentAuthor = :email AND a.id=c.article.id ORDER BY c.commentId DESC")
     fun findUserComments(@Param("email") email:String): List<Comment>

    //사용자가 작성한 댓글의 게시물 조회
    @Query("SELECT DISTINCT a FROM Comment c JOIN c.article a WHERE c.commentAuthor = :email ORDER BY a.createdAt DESC")
     fun findUserArticlesAndComments(@Param("email") email:String): List<Article>

     //탈퇴한 사용자 표시
     @Modifying
     @Transactional
     @Query("UPDATE Comment c SET c.commentAuthor = '탈퇴한 사용자입니다.' WHERE c.commentAuthor = :email")
     fun updateCommentAuthorToDeleted(@Param("email") email:String)
}