package com.example.community_4am_kotlin.feature.user.dto

data class UserDTO(
    val id: Long,
    val email: String,
    val status: String, // 상태 (예: "ONLINE", "OFFLINE")
    val isFriend: Boolean // 친구 여부
)