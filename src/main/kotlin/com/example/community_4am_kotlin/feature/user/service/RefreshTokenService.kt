package com.example.community_4am_kotlin.feature.user.service

import com.example.community_4am_kotlin.domain.user.RefreshToken
import com.example.community_4am_kotlin.feature.user.repository.RefreshTokenRepository
import org.springframework.stereotype.Service

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository // RefreshTokenRepository를 주입
) {

    // 주어진 refreshToken을 이용해 RefreshToken 엔티티를 조회하는 메서드
    fun findByRefreshToken(refreshToken: String): RefreshToken {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
            .orElseThrow { IllegalArgumentException("Unexpected token") } // 토큰이 없으면 예외 발생
    }
}