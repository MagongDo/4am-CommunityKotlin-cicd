package com.example.community_4am_kotlin.config.jwt

data class LoginRequest(
    val username: String,
    private val password: String
)

