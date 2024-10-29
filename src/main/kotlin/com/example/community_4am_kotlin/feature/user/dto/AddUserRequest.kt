package com.example.Community_4am_Kotlin.feature.user.dto

import org.springframework.web.multipart.MultipartFile

data class AddUserRequest(
    var email: String,
    var password: String,
    var nickname: String,
    var profileImage: MultipartFile
)
