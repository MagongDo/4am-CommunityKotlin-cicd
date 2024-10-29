package com.example.Community_4am_Kotlin.domain.notification

import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
data class CommentAlarm(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long,
    var articleId:Long,
    @Enumerated(EnumType.STRING)
    var alarmType: AlarmType,
    var userId:Long,
    var createdAt: LocalDateTime
)
