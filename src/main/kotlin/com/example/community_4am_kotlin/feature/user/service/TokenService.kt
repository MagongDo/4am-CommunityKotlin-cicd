package com.example.community_4am_kotlin.feature.user.service

import com.example.Community_4am_Kotlin.domain.user.User
import com.example.community_4am_kotlin.config.jwt.TokenProvider
import org.springframework.stereotype.Service
import java.time.Duration

@Service

class TokenService(
    private val tokenProvider: TokenProvider, // JWT 토큰 생성과 검증을 담당하는 클래스
    private val refreshTokenService: RefreshTokenService, // Refresh Token을 관리하는 서비스
    private val userService: UserService // 사용자 정보를 관리하는 서비스
) {

    // 주어진 Refresh Token을 사용해 새로운 Access Token을 생성하는 역할
    fun createNewAccessToken(refreshToken: String): String {
        // 토큰 유효성 검사에 실패하면 예외 발생
        if (!tokenProvider.validToken(refreshToken)) { // TokenProvider를 사용해 Refresh Token이 유효한지 검증
            throw IllegalArgumentException("Unexpected token") // Refresh Token이 유효하지 않으면 IllegalArgumentException 예외를 던짐
        }

        val userId = refreshTokenService.findByRefreshToken(refreshToken).userId // RefreshTokenService에서 전달받은 Refresh Token으로부터 사용자 ID를 찾음
        val user: User = userService.findById(userId) // UserService를 사용해 유저 ID로부터 실제 사용자 정보를 조회

        return tokenProvider.generateToken(user, Duration.ofHours(2)) // 조회된 사용자 정보를 바탕으로 새로운 Access Token을 생성
    }
}