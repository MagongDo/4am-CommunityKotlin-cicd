package com.example.community_4am_kotlin.feature.user.dto

import org.springframework.web.multipart.MultipartFile

data class AddUserRequest(
    var email: String,
    var password: String,
    var nickname: String,
    var profileImage: MultipartFile
)
