package com.example.community_4am_kotlin.feature.videochat.dto

import com.example.community_4am_kotlin.domain.videochat.QVideoChatLog.videoChatLog
import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import com.example.community_4am_kotlin.domain.videochat.VideoChatReport
import java.time.LocalDateTime

data class VideoChatReportDTO (
    val reportId: Long = 0,

    var videoChatId: String? = null,
    var reporterId: String? = null,
    var reportedId: String? = null,
    var reportVideoChatCreatAt: LocalDateTime? = null,
    var reportVideoChatEndAt: LocalDateTime? = null,
    var reportDetails: String? = null,
){

    constructor(videoChatReport: VideoChatReport) : this(
        reportId = videoChatReport.reportId,
        videoChatId = videoChatReport.videoChatId,
        reporterId = videoChatReport.reporterId,
        reportedId = videoChatReport.reportedId,
        reportVideoChatCreatAt = videoChatReport.reportVideoChatCreatAt,
        reportVideoChatEndAt = videoChatReport.reportVideoChatEndAt,
        reportDetails = videoChatReport.reportDetails
    )

}