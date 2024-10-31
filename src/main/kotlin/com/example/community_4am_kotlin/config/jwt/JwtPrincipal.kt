package com.example.community_4am_kotlin.config.jwt

import java.io.Serializable
import java.security.Principal

class JwtPrincipal(
    private val username: String // username을 생성자에서 설정할 수 있도록 함
) : Principal, Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun getName(): String {
        return username
    }

    override fun toString(): String {
        return "JwtPrincipal{username='$username'}"
    }
}