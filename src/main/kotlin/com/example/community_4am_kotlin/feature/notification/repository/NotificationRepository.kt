package com.example.Community_4am_Kotlin.feature.notification.repository


import com.example.Community_4am_Kotlin.domain.notification.Notification
import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient: String): List<Notification>
    fun findByRecipientAndMakeIdAndAlarmTypeIsLikeAndIsReadFalse(
        recipient: String,
        makeId: String,
        alarmType: AlarmType
    ): Notification?
    fun countByRecipientAndIsReadFalse(recipient: String): Long
    fun findByUser(user: User): List<Notification>
}