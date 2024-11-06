
package com.example.community_4am_kotlin.feature.notification.dto

import com.example.community_4am_kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.domain.notification.Notification
import java.time.LocalDateTime

data class NotificationDTO(
    val id: Long? = null,
    val alarmType: String = "",
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val isRead: Boolean = false,
    val message: String = "",
    val recipient: String = "",
    val targetId: Long = 0,
    val userId: Long? = null, // User의 ID만 포함
    val makeId: String = "",
    val userEmail: String? = null, // 필요 시 User의 이메일 포함
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
