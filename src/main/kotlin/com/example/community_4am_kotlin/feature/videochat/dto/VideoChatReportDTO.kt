package com.example.community_4am_kotlin.feature.videochat.dto

import com.example.community_4am_kotlin.domain.videochat.VideoChatReport
import java.time.LocalDateTime

data class VideoChatReportDTO(
    val reportId: Long = 0,

    var videoChatId: String,
    var reporterId: Long?,
    var reportedId: Long?,
    var reportType: String?,
    var reportDetails: String?,
    var reportTimestamp: LocalDateTime?
){
    // 엔티티에서 DTO로 변환하는 생성자
    constructor(videoChatReport: VideoChatReport) : this(
        videoChatId = videoChatReport.videoChatId.toString(),
        reporterId = videoChatReport.reporterId,
        reportedId = videoChatReport.reportedId,
        reportType = videoChatReport.reportType,
        reportDetails = videoChatReport.reportDetails,
        reportTimestamp = videoChatReport.reportTimestamp
    )

    // DTO에서 엔티티로 변환하는 생성자
    fun toEntity(): VideoChatReport {
        return VideoChatReport(
            videoChatId = this.videoChatId,
            reporterId = this.reporterId,
            reportedId = this.reportedId,
            reportType = this.reportType,
            reportDetails = this.reportDetails,
            reportTimestamp = this.reportTimestamp
        )
    }

}