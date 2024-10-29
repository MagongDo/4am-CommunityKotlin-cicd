package com.example.Community_4am_Kotlin.domain.user

import jakarta.persistence.*

@Entity
data class RefreshToken (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @Column(name="user_id", nullable = false)
    private var userId: Long,
    @Column(name="refresh_token", nullable = false,length=1000)
    private var refreshToken: String,
    @Column(name="email", length=255)
    var email: String,
){
    fun RefreshToken(userId: Long, refreshToken: String, email: String){
        this.userId = userId
        this.refreshToken = refreshToken
        this.email = email
    }
    fun update(newRefreshToken: RefreshToken): RefreshToken{
        this.refreshToken = newRefreshToken.refreshToken
        return this
    }
}