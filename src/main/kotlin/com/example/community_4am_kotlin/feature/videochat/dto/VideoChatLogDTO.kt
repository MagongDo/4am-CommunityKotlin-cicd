package com.example.community_4am_Kotlin.feature.videochat.dto


import com.example.community_4am_Kotlin.domain.videochat.VideoChatLog
import java.time.LocalDateTime

data class VideoChatLogDTO(
    var video_chat_id: String = "", // 화상채팅 방 id
    var user_id: Long, // 본인 고유번호
    var other_user_id: Long, // 상대 고유번호
    var video_chat_create_at: LocalDateTime, // 화상채팅 시작 날짜 및 시각
    var video_chat_end_at: LocalDateTime // 화상채팅 종료 날짜 및 시각
) {
    constructor(videoChatLog: VideoChatLog) : this(
        video_chat_id = videoChatLog.video_chat_id,
        user_id = videoChatLog.user_id,
        other_user_id = videoChatLog.other_user_id,
        video_chat_create_at = videoChatLog.video_chat_created_at,
        video_chat_end_at = videoChatLog.video_chat_modified_at
    )

    fun toEntity(): VideoChatLog {
        return VideoChatLog(
            video_chat_id = video_chat_id,
            user_id = user_id,
            other_user_id = other_user_id,
            video_chat_created_at = video_chat_create_at,
            video_chat_modified_at = video_chat_end_at
        )
    }
}
