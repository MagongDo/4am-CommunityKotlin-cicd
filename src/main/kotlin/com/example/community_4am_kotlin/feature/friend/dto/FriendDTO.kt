package com.example.community_4am_kotlin.feature.friend.dto

import com.example.community_4am_kotlin.domain.friend.FriendStatus
import com.example.community_4am_kotlin.domain.user.enums.UserStatus

data class FriendDTO(
    val id: Long?=null,
    val name: String?=null,
    val status: UserStatus?=null
)
