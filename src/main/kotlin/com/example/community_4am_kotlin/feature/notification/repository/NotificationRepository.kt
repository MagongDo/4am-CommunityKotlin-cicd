package com.example.community_4am_kotlin.feature.notification.repository


import com.example.community_4am_Kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.domain.notification.Notification
import com.example.community_4am_kotlin.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByRecipientAndIsReadFalseOrderByCreatedAtDesc(recipient: String): MutableList<Notification>
    @Query(
        """
    SELECT n FROM Notification n
    WHERE n.recipient = :recipient 
      AND n.makeId = :makeId 
      AND n.alarmType = :alarmType 
      AND n.isRead = false
    """
    )
    fun findByRecipientAndMakeIdAndAlarmTypeAndIsReadFalse(
        @Param("recipient") recipient: String,
        @Param("makeId") makeId: String,
        @Param("alarmType") alarmType: AlarmType
    ): Notification?
    fun countByRecipientAndIsReadFalse(recipient: String): Long
    fun findByUser(user: User): MutableList<Notification>
}