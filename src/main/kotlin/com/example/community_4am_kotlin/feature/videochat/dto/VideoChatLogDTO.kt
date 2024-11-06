package com.example.community_4am_kotlin.feature.videochat.dto


import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import java.time.LocalDateTime

data class VideoChatLogDTO(
    var id: Long? = null,
    var videoChatId: String = "", // 화상채팅 방 id
    var userId: Long?, // 본인 고유번호
    var otherUserId: Long?, // 상대 고유번호
    var videoChatCreateAt: LocalDateTime?, // 화상채팅 시작 날짜 및 시각
    var videoChatEndAt: LocalDateTime? // 화상채팅 종료 날짜 및 시각
) {
    constructor(videoChatLog: VideoChatLog) : this(
        id = videoChatLog.id,
        videoChatId = videoChatLog.videoChatId,
        userId = videoChatLog.userId,
        otherUserId = videoChatLog.otherUserId,
        videoChatCreateAt = videoChatLog.videoChatCreateAt,
        videoChatEndAt = videoChatLog.videoChatEndAt
    )

    fun toEntity(): VideoChatLog {
        return VideoChatLog(
            id = id,
            videoChatId = videoChatId,
            userId = userId,
            otherUserId = otherUserId,
            videoChatCreateAt = videoChatCreateAt,
            videoChatEndAt = videoChatEndAt
        )
    }
}
