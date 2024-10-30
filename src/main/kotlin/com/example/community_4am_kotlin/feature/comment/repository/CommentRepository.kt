package com.example.community_4am_kotlin.feature.comment.repository

import com.example.Community_4am_Kotlin.domain.article.Comment
import com.example.community_4am_kotlin.feature.comment.dto.CommentListViewResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface CommentRepository: JpaRepository<Comment, Long> {
    // 게시글에 속한 모든 댓글과 대댓글을 commentId 순으로 가져옴
    @Query("SELECT c FROM Comment c LEFT JOIN FETCH c.parentComment " +
                "WHERE c.article.id = :articleId ORDER BY c.commentId ASC")
    fun findByArticleIdOrderByCommentIdAsc(@Param("articleId") articleId:Long): List<Comment>

    @Query("SELECT c FROM Comment c WHERE c.article.id=:articleId")
    fun findByArticleId(@Param("articleId") articleId:Long): List<Comment>

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
}