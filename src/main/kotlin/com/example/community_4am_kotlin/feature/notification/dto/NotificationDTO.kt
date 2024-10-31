package com.example.community_4am_Kotlin.feature.notification.dto

import com.example.community_4am_Kotlin.domain.notification.Notification
import com.example.community_4am_Kotlin.feature.notification.AlarmType
import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long? = null,
    val alarmType: String,
    val createdAt: LocalDateTime,
    val isRead: Boolean,
    val message: String,
    val recipient: String,
    val targetId: Long,
    val userId: Long, // User의 ID만 포함
    val makeId: String,
    val userEmail: String, // 필요 시 User의 이메일 포함
    val dataType: String = "Notification"
) {

    fun toEntity(): Notification {
        return Notification(
            id = this.id,
            alarmType = AlarmType.valueOf(this.alarmType),
            createdAt = this.createdAt,
            isRead = this.isRead,
            message = this.message,
            recipient = this.recipient,
            targetId = this.targetId,
            makeId = this.makeId
            // User 설정은 서비스 레이어에서 처리해야 함
        )
    }
}