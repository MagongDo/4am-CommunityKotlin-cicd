package com.example.community_4am_kotlin.feature.notification.repository


import com.example.community_4am_kotlin.feature.notification.AlarmType
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
    @Query("""
    SELECT COUNT(n) FROM Notification n 
    WHERE n.recipient = :recipient 
      AND n.isRead = false 
      AND n.alarmType NOT IN (:excludedTypes)
""")
    fun countByRecipientAndIsReadFalseAndAlarmTypeNotIn(
        @Param("recipient") recipient: String,
        @Param("excludedTypes") excludedTypes: List<AlarmType>
    ): Long
    fun findByUser(user: User): MutableList<Notification>

    fun findByUserAndAlarmTypeAndIsReadFalse(targetUser: User, type: AlarmType): List<Notification>

    fun countByRecipientAndAlarmTypeAndIsReadFalse(
        recipient: String,
        alarmType: AlarmType = AlarmType.FRIEND
    ): Long
}