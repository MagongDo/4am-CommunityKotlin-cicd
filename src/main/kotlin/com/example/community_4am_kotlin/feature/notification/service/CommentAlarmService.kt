package com.example.community_4am_kotlin.feature.notification.service


import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.domain.notification.CommentAlarm
import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.feature.notification.repository.CommentAlarmRepository
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import com.example.community_4am_kotlin.feature.article.ArticleRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class CommentAlarmService(
    private val commentAlarmRepository: CommentAlarmRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository
) {

    // 댓글 작성
    fun addComment(userId: Long, articleId: Long) {
        val user: User = userRepository.findById(userId)
            .orElseThrow { RuntimeException("사용자 없음") }
        val article: Article = articleRepository.findById(articleId)
            .orElseThrow { RuntimeException("게시물 없음") }

        val commentAlarm = CommentAlarm().apply {
            this.userId = user.id
            this.articleId = article.id
            this.alarmType = AlarmType.COMMENT
        }

        commentAlarmRepository.save(commentAlarm)
    }
}