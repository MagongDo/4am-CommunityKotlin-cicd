package com.example.community_4am_kotlin.feature.like.service

import com.example.community_4am_Kotlin.domain.article.Article
import com.example.community_4am_Kotlin.domain.article.Like
import com.example.community_4am_Kotlin.domain.user.User
import com.example.community_4am_Kotlin.feature.notification.AlarmType
import com.example.community_4am_Kotlin.feature.notification.repository.NotificationRepository
import com.example.community_4am_Kotlin.feature.user.dto.UserLikedArticlesList
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.like.repository.LikeRepository
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class LikeService(
    private val likeRepository: LikeRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository
) {

    private val logger = LogManager.getLogger(LikeService::class.java)

    //게시글에 맞는 좋아요 생성
    fun addLike(articleId: Long, userName: String): Boolean {
        val article: Article = articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Invalid article ID") }

        val user: User = userRepository.findByEmail(userName)
            .orElseThrow { IllegalArgumentException("Invalid user ID") }

       // logger.info("ArticleId: {}", articleId)

        val existingLike: Like? = likeRepository.findByArticleAndUser(articleId, user.id.toString()).orElse(null)

        val isRead: Boolean = notificationService.likeIsRead(article.author, userName, AlarmType.LIKE)

//        if (user.email == userName) {
//            logger.info("email: {} already exists", user.email)
//            logger.info("userName: {} already exists", userName)
//        } else {
//            logger.info("email: {}", user.email)
//            logger.info("userName: {}", userName)
//        }

        return existingLike?.let { updateLike ->
            updateLike.changeLikedStatus(!updateLike.likedStatus)

            if (updateLike.likedStatus && !isRead) {
                notificationService.sendLikeNotification(articleId, userName)
            }
            likeRepository.save(updateLike)
            updateLike.likedStatus
        } ?: run {
            val newLike = Like(
                article = article,
                user = user,
                likedStatus = true
            )
            likeRepository.save(newLike)
            notificationService.sendLikeNotification(articleId, userName)
            newLike.likedStatus
        }
    }

    //좋아요뷰단에서 취소하면 delete 되는게 아니라 likedStatus가 0(false)으로 됨
    //한 게시글에 대한 좋아요 누른 사용자 목록 조회
    fun checkLikeStatus(articleId: Long, userName: String): Boolean {
        val user: User = userRepository.findByEmail(userName)
            .orElseThrow { IllegalArgumentException("Invalid user ID") }

        val like: Like? = likeRepository.findByArticleAndUser(articleId, user.id.toString()).orElse(null)

        // 좋아요가 존재하고 likedStatus가 true이면 true 반환
        return like?.likedStatus ?: false
    }

    fun getUserAllArticlesAndLikes(userName: String): List<UserLikedArticlesList> {
        val articles: List<Article> = likeRepository.findUserLikedArticles(userName)

        // Article 엔티티에서 필요한 데이터를 가공하여 DTO로 변환
        return articles.map { article ->
            UserLikedArticlesList(
                id = article.id,
                title = article.title,
                createdAt = article.createdAt,
                viewCount = article.viewCount
            )
        }
    }


    fun getLikeCount(articleId: Long): Long {
        return likeRepository.countLikesByArticleId(articleId)
    }

//    // 사용자 조회 메서드 예제
//    private fun findUserByUsername(username: String): User {
//        return userRepository.findByEmail(username)
//            .orElseThrow { IllegalArgumentException("User not found") }
//    }
//
//    // 게시물 조회 메서드 예제
//    private fun findArticleById(articleId: Long): Article {
//        return articleRepository.findById(articleId)
//            .orElseThrow { IllegalArgumentException("Article not found") }
//    }
}