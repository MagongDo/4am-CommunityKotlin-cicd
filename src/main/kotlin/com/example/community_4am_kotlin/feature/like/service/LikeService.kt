package com.example.community_4am_kotlin.feature.like.service

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.domain.article.Like
import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import com.example.Community_4am_Kotlin.feature.notification.repository.NotificationRepository
import com.example.Community_4am_Kotlin.feature.user.dto.UserLikedArticlesList
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.like.repository.LikeRepository
import org.apache.logging.log4j.LogManager
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class LikeService(
    private val likeRepository: LikeRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val notificationService: NotificationService,
    private val notificationRepository: NotificationRepository
) {

    private val logger = LogManager.getLogger(LikeService::class.java)

    /**
     * 게시글에 대한 좋아요를 추가하거나 상태를 토글합니다.
     *
     * @param articleId 좋아요를 추가할 게시글의 ID
     * @param userName 좋아요를 추가하는 사용자의 이메일
     * @return 현재 좋아요 상태(true/false)
     */
    @Transactional
    fun addLike(articleId: Long, userName: String): Boolean {
        val article: Article = articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Invalid article ID") }

        val user: User = userRepository.findByEmail(userName)
            .orElseThrow { IllegalArgumentException("Invalid user ID") }

        logger.info("ArticleId: {}", articleId)

        val existingLike: Like? = likeRepository.findByArticleAndUser(articleId, user.id.toString()).orElse(null)

        val isRead: Boolean = notificationService.likeIsRead(article.author, userName, AlarmType.LIKE)

        if (user.email == userName) {
            logger.info("email: {} already exists", user.email)
            logger.info("userName: {} already exists", userName)
        } else {
            logger.info("email: {}", user.email)
            logger.info("userName: {}", userName)
        }

        return if (existingLike != null) {
            // 현재 좋아요 상태를 반대로 변경
            existingLike.changeLikedStatus(!existingLike.likedStatus)

            // 좋아요 상태가 true로 변경되고 알림이 읽히지 않은 경우 알림 전송
            if (existingLike.likedStatus && !isRead) {
                notificationService.sendLikeNotification(articleId, userName)
            }

            likeRepository.save(existingLike) // 변경 사항 저장
            existingLike.likedStatus
        } else {
            // 새로운 좋아요 생성
//            val newLike: Like = Like.builder()
//                .article(article)
//                .user(user)
//                .likedStatus(true)
//                .build()
            val newLike: Like = Like(
                article = article,
                user = user,
                likedStatus = true,
            )


            likeRepository.save(newLike) // 좋아요 추가

            notificationService.sendLikeNotification(articleId, userName) // 알림 전송
            newLike.likedStatus
        }
    }

    /**
     * 특정 사용자의 특정 게시글에 대한 좋아요 상태를 확인합니다.
     *
     * @param articleId 좋아요 상태를 확인할 게시글의 ID
     * @param userName 좋아요 상태를 확인할 사용자의 이메일
     * @return 좋아요 상태(true/false)
     */
    @Transactional(readOnly = true)
    fun checkLikeStatus(articleId: Long, userName: String): Boolean {
        val user: User = userRepository.findByEmail(userName)
            .orElseThrow { IllegalArgumentException("Invalid user ID") }

        val like: Like? = likeRepository.findByArticleAndUser(articleId, user.id.toString()).orElse(null)

        // 좋아요가 존재하고 likedStatus가 true이면 true 반환
        return like?.likedStatus ?: false
    }

    /**
     * 사용자가 좋아요를 누른 모든 게시물을 조회합니다.
     *
     * @param userName 사용자의 이메일
     * @return 사용자가 좋아요를 누른 게시물 목록
     */
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

    /**
     * 특정 게시글에 대한 좋아요 수를 조회합니다.
     *
     * @param articleId 좋아요 수를 조회할 게시글의 ID
     * @return 좋아요 수
     */
    @Transactional
    fun getLikeCount(articleId: Long): Long {
        return likeRepository.countLikesByArticleId(articleId)
    }

    // 사용자 조회 메서드 예제
    private fun findUserByUsername(username: String): User {
        return userRepository.findByEmail(username)
            .orElseThrow { IllegalArgumentException("User not found") }
    }

    // 게시물 조회 메서드 예제
    private fun findArticleById(articleId: Long): Article {
        return articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("Article not found") }
    }
}