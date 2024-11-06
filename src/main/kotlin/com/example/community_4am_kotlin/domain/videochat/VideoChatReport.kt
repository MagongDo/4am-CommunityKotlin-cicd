package com.example.community_4am_kotlin.domain.videochat

import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@EntityListeners(AuditingEntityListener::class)
@Table(name="video_chat_report")
data class VideoChatReport (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    val reportId: Long = 0,

    var videoChatId: String? = null,
    var reporterId: Long? = null,
    var reportedId: Long? = null,
    var reportType: String? = null,
    var reportDetails: String? = null,
    var reportTimestamp: LocalDateTime? = null

    ){

}