package com.example.community_4am_kotlin.feature.notification.service


import com.example.community_4am_kotlin.domain.friend.Friend
import com.example.community_4am_kotlin.domain.friend.FriendStatus
import com.example.community_4am_kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.domain.notification.Notification
import com.example.community_4am_kotlin.feature.friend.repository.FriendRepository
import com.example.community_4am_kotlin.feature.article.repository.ArticleRepository
import com.example.community_4am_kotlin.feature.notification.event.NotificationEvent
import com.example.community_4am_kotlin.feature.notification.repository.NotificationRepository
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.Principal
import java.time.LocalDateTime

@Service
@Transactional
class NotificationService(
    private val notificationRepository: NotificationRepository,
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val commentAlarmService: CommentAlarmService,
    private val friendRepository: FriendRepository
) {

    fun likeIsRead(recipient: String, makeId: String, alarmType: AlarmType): Boolean {
        val notification = notificationRepository.findByRecipientAndMakeIdAndAlarmTypeAndIsReadFalse(
            recipient, makeId, alarmType
        )
        return notification != null
    }
    fun getUnreadFriendNotificationsCount(recipient: String): Long {
        return notificationRepository.countByRecipientAndAlarmTypeAndIsReadFalse(recipient, AlarmType.FRIEND)
    }
    // 친구 신청 알람 생성 및 전송
    fun sendFriendNotification(friendEmail: String, fromEmail: String) {
        try {
            val recipientUser = userRepository.findByEmail(friendEmail)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
            val senderUser = userRepository.findByEmail(fromEmail)
                .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
            val friend = Friend(
                user = senderUser,
                friend = recipientUser,
                status = FriendStatus.PENDING
            )
            friendRepository.save(friend)

            val message = "$fromEmail 님이 회원님에게 친구 요청을 보냈습니다."
            val notification = Notification(
                alarmType = AlarmType.FRIEND,
                message = message,
                recipient = friendEmail,
                isRead = false,
                makeId = fromEmail,
                targetId = senderUser.id,
                user = recipientUser, // 여기서 받는 사람을 설정합니다.
                createdAt = LocalDateTime.now()
            )
            notificationRepository.save(notification)
            eventPublisher.publishEvent(NotificationEvent(this, notification.recipient, message, notification.alarmType))
        } catch (e: Exception) {
            println("알림 전송 중 오류 발생: ${e.message}")
        }

    }

    // 게시글에 좋아요를 누른 경우 알림 생성 및 전송
    fun sendLikeNotification(articleId: Long, fromAuthor: String) {
        try {
            val article = articleRepository.findById(articleId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

            val toAuthor = article.author
            val recipientUser = userRepository.findByEmail(toAuthor)
                .orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

            if (toAuthor != fromAuthor) {
                val message = "$fromAuthor 님이 회원님의 ${article.title} 게시물을 좋아합니다."

                val notification = Notification(
                    alarmType = AlarmType.LIKE,
                    message = message,
                    recipient = toAuthor,
                    isRead = false,
                    makeId = fromAuthor,
                    targetId = articleId,
                    user = recipientUser,
                    createdAt = LocalDateTime.now()
                )

                notificationRepository.save(notification)
                commentAlarmService.addComment(recipientUser.id, articleId)
                eventPublisher.publishEvent(NotificationEvent(this, toAuthor, message, notification.alarmType))
            }

        } catch (e: Exception) {
            println("알림 전송 중 오류 발생: ${e.message}")
        }
    }

    // 게시글에 댓글을 남긴 경우 알림 생성 및 전송
    fun sendCommentNotification(articleId: Long, fromAuthor: String) {
        try {
            val article = articleRepository.findById(articleId)
                .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }

            val toAuthor = article.author
            val recipientUser = userRepository.findByEmail(toAuthor)
                .orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

            if (toAuthor != fromAuthor) {
                val message = "$fromAuthor 님이 회원님의 ${article.title} 게시물에 댓글을 남겼습니다."

                val notification = Notification(
                    alarmType = AlarmType.COMMENT,
                    message = message,
                    recipient = toAuthor,
                    isRead = false,
                    makeId = fromAuthor,
                    targetId = articleId,
                    user = recipientUser,
                    createdAt = LocalDateTime.now()
                )

                notificationRepository.save(notification)
                eventPublisher.publishEvent(NotificationEvent(this, toAuthor, message, notification.alarmType))
            }

        } catch (e: Exception) {
            println("알림 전송 중 오류 발생: ${e.message}")
        }
    }

    fun sendReCommentNotification(articleId: Long, fromAuthor: String) {
        val article = articleRepository.findById(articleId)
            .orElseThrow { IllegalArgumentException("게시글을 찾을 수 없습니다.") }
        userRepository.findByEmail(fromAuthor)
            .orElseThrow { IllegalArgumentException("사용자를 찾을 수 없습니다.") }
    }

    // 사용자별 읽지 않은 알림 목록 조회
    fun getUnreadNotifications(recipient: String): MutableList<Notification> {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient)
    }

    // 사용자별 읽지 않은 알림 수 카운트
    fun getUnreadNotificationsCount(recipient: String): Long {
        val excludedTypes = listOf(AlarmType.FRIEND)
        return notificationRepository.countByRecipientAndIsReadFalseAndAlarmTypeNotIn(recipient, excludedTypes)
    }

    // 알림 생성
    @Transactional
    fun createNotification(notification: Notification, principal: Principal): Notification {
        val author = principal.name
        val user = userRepository.findByEmail(author)
            .orElseThrow { RuntimeException("User not found") }
        notification.user = user
        return notificationRepository.save(notification)
    }

    // 사용자 ID로 알림 조회
    fun getNotificationsByUserId(userId: Long): List<Notification> {
        val user = userRepository.findById(userId)
            .orElseThrow { RuntimeException("User not found") }
        return notificationRepository.findByUser(user)
    }

    // 특정 알림을 읽음으로 처리
    fun markAsRead(notificationId: Long) {
        val notification = notificationRepository.findById(notificationId)
            .orElseThrow { IllegalArgumentException("알림을 찾을 수 없습니다.") }
        notification.changeisRead(true)
        notificationRepository.save(notification)
    }

    // 특정 알림 삭제
    fun deleteNotification(notificationId: Long) {
        notificationRepository.deleteById(notificationId)
    }
}