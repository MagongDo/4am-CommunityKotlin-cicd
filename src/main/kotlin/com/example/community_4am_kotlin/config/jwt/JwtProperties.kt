package com.example.Community_4am_Kotlin.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties(
    var issuer: String = "ajufresh@gmail.com", // issuer 기본값 설정
    var secret: String = "c3R1ZHktc3ByaW5nYm9vdC1zZWN1cmUtc2VjcmV0LWtleS0zMmNoYXJz" // secret 기본값 설정
)