package com.example.community_4am_kotlin.feature.user.service

import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(private val userRepository: UserRepository) {

    // 이메일로 사용자 조회
    fun findByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException("No user found with email: $email") }
    }
}