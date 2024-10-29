package com.example.Community_4am_Kotlin.config.jwt

import com.example.Community_4am_Kotlin.domain.user.User
import org.springframework.stereotype.Service

@Service
class TokenProvider(
    val jwtProperties: JwtProperties
) {
    fun generateToken(user: User) : String? {
        return null
    }
}