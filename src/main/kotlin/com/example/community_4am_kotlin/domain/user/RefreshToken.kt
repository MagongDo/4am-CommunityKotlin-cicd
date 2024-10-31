package com.example.community_4am_kotlin.domain.user

import jakarta.persistence.*

@Entity
data class RefreshToken(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "refresh_token", nullable = false, length = 1000)
    var refreshToken: String,

    @Column(name = "email", length = 255)
    var email: String
) {
    // 업데이트 메서드가 문자열 타입의 새 리프레시 토큰을 받도록 수정
    fun update(newRefreshToken: String): RefreshToken {
        this.refreshToken = newRefreshToken
        return this
    }
}