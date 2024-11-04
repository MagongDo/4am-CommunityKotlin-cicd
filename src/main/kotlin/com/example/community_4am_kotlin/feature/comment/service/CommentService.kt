package com.example.community_4am_kotlin.feature.comment.service

import com.example.community_4am_kotlin.domain.article.Comment
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.comment.dto.*
import com.example.community_4am_kotlin.feature.comment.repository.CommentRepository
import com.example.community_4am_kotlin.feature.like.service.LikeService
import com.example.community_4am_kotlin.feature.user.dto.UserCommentsList
import org.apache.logging.log4j.LogManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val articleRepository: ArticleRepository,
) {

    private val logger = LogManager.getLogger(LikeService::class.java)

    //게시글에 맞는 한개 댓글 생성
    fun saveComment(request:AddCommentRequest,articleId:Long,userName:String): Comment {
        val article=articleRepository.findById(articleId).orElseThrow { IllegalArgumentException("Article not found") }
        val parentComment=request.parentCommentId?.let{
            commentRepository.findById(it).orElseThrow { IllegalArgumentException("Parent comment not found") }
        }

        val savedComment = commentRepository.save(request.toEntity(userName, article, parentComment));
        return commentRepository.save(savedComment)
    }

    //게시글에 달린 댓글 목록 조회(페이징)
    fun getComments(articleId:Long,commentPageRequestDTO: CommentPageRequestDTO): Page<CommentListViewResponse>{
        return try{
            val sort= Sort.by("commentId").descending()
            val pageable: Pageable =commentPageRequestDTO.getPageable(sort)
            return commentRepository.list(commentPageRequestDTO.id,pageable)

        }catch (e:Exception){
            logger.error("---"+e.message)
            throw RuntimeException("Review NOT Fetched",e)
        }
    }

    //게시글에 달린 댓글 목록 조회
    fun getComments(articleId:Long): List<CommentResponse>{
        val comments=commentRepository.findByArticleIdOrderByCommentIdAsc(articleId)
        return comments.map {CommentResponse(it)}
    }

    //게시글에 맞는 한개 댓글과 대댓글 조회
    fun getReComments(articleId:Long,commentId:Long): List<CommentResponse>{
        val comments=commentRepository.findParentAndChildCommentsByArticleId(articleId,commentId)
        return comments.map {CommentResponse(it)}
    }

    //댓글 수정
    fun updateComment(commentId:Long,request: UpdateCommentRequest):UpdateCommentRequest{
        val updateComment=commentRepository.findById(commentId).orElseThrow { IllegalArgumentException("Comment not found") }

        authorizeCommentAuthor(updateComment)
        //댓글 블라인드 처리 업데이트
        updateComment.changeCommentContent(request.commentContent)
        request.commentIsDeleted?.let { updateComment.changeCommentIsDeleted(it) }
        request.commentIsHidden?.let { updateComment.changeCommentIsHidden(it) }

        return UpdateCommentRequest(updateComment)
    }

    //댓글 삭제
    fun deleteComment(commentId:Long){
        val comment=commentRepository.findById(commentId).orElseThrow { IllegalArgumentException("Comment not found") }
        authorizeCommentAuthor(comment)
        commentRepository.delete(comment)
    }

    //댓글 개수 세기
    fun getCommentCount(articleId:Long):Long=commentRepository.countCommentsByArticleId(articleId)

    //사용자가 작성한 댓글과 해당 게시물 목록 조회
    fun getUserAllComments(userName:String):List<UserCommentsList>{
        val comments=commentRepository.findUserComments(userName)

        return comments.mapNotNull { comment ->
            comment.createdAt?.let { createdAt ->
                comment.article.id?.let { id ->
                    UserCommentsList(
                        comment.commentContent,
                        createdAt,
                        comment.article.title,
                        id
                    )
                }
            }
        }
    }


    // 게시글의 작성자를 확인하여 권한 검증
    fun authorizeCommentAuthor(comment: Comment) {
        val userName:String= SecurityContextHolder.getContext().authentication.name
        if(comment.commentAuthor!=userName){
            throw IllegalArgumentException("not authorized")
        }
    }

}