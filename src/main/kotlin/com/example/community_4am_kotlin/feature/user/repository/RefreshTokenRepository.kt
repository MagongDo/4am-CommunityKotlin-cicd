package com.example.community_4am_kotlin.feature.user.repository

import com.example.community_4am_kotlin.domain.user.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByUserId(userId: Long?): Optional<RefreshToken>
    fun findByRefreshToken(refreshToken: String): Optional<RefreshToken>
}