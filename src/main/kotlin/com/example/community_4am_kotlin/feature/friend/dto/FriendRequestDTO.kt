package com.example.community_4am_kotlin.feature.friend.dto
data class FriendRequestDTO(
    val id: Long?,
    val fromUserEmail: String?,
    val message: String
    // 필요한 경우 추가 필드
)