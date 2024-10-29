package com.example.Community_4am_Kotlin.config.jwt

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties("jwt")
data class JwtProperties (
    private val issuer:String="",
    private val secret:String=""

)