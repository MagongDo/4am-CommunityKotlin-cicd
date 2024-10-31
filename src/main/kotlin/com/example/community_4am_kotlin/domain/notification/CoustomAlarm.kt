package com.example.community_4am_kotlin.domain.notification

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.Date

@Entity
@EntityListeners(AuditingEntityListener::class)
data class CoustomAlarm(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var userId: String?=null, // 사용자 ID

    var message: String,

    @Column(nullable = false)
    var isRead: Boolean = false, // 기본값 false로 초기화

    var status: Boolean? = null,

    var reserveAt: LocalTime,

    @ElementCollection
    @CollectionTable(name = "notification_days", joinColumns = [JoinColumn(name = "notification_id")])
    @Column(name = "day")
    var notificationDays: MutableSet<String>, // Set<String>으로 설정

    @CreatedDate
    val createdAt: LocalDateTime
)

