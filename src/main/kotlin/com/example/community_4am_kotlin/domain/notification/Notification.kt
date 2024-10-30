package com.example.Community_4am_Kotlin.domain.notification

import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.notification.AlarmType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "notification")
@EntityListeners(AuditingEntityListener::class)
data class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long? = null,
    var alarmType: AlarmType,
    @CreatedDate
    var createdAt: LocalDateTime,
    var isRead: Boolean,
    var message:String,
    var recipient:String?=null,
    var targetId:Long,
    var makeId:String,
    @ManyToOne(fetch = FetchType.LAZY, cascade = [(CascadeType.MERGE)])
    @JoinColumn(name="user_id",nullable=false)
    var user: User?=null
)
{
    fun changeisRead(isRead: Boolean) {this.isRead=isRead}
}
