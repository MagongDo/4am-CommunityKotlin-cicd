package com.example.community_4am_Kotlin.config.jwt

import com.example.community_4am_Kotlin.config.jwt.JwtProperties
import com.example.community_4am_Kotlin.domain.user.User
import org.springframework.stereotype.Service

@Service
class TokenProvider(
    val jwtProperties: JwtProperties
) {
    fun generateToken(user: User) : String? {
        return null
    }
}