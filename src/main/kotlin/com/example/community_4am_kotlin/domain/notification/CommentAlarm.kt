package com.example.Community_4am_Kotlin.domain.notification

import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "comment_alarm")
@EntityListeners(AuditingEntityListener::class)
data class CommentAlarm(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id:Long?=null,
    var articleId:Long?=null,
    @Enumerated(EnumType.STRING)
    var alarmType: AlarmType?=null,
    var userId:Long?=null,
    var createdAt: LocalDateTime?=null
)
