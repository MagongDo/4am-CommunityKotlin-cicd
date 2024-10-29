package com.example.Community_4am_Kotlin.feature.user.service


import com.example.Community_4am_Kotlin.domain.user.User
import com.example.Community_4am_Kotlin.feature.user.repository.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class FindUserDataService(
    private val userRepository: UserRepository
) {

    private val log: Logger = LoggerFactory.getLogger(FindUserDataService::class.java)

    // 닉네임으로 이메일 찾기
    fun findEmailByNickname(nickname: String): User {
        return userRepository.findByNickname(nickname)
            .orElseThrow { IllegalArgumentException("No user found with email: $nickname") }
    }

    // 이메일과 닉네임으로 비밀번호 찾기
    fun findPasswordByEmailAndNickname(email: String, nickname: String): User {
        val user = userRepository.findByEmailAndNickname(email, nickname)
        log.info("2 : ${user.toString()}")
        return user.orElse(null)
    }

    @Transactional
    fun updatePasswordByEmailAndNickname(email: String, nickname: String, password: String) {
        val user = userRepository.findByEmailAndNickname(email, nickname)
            .orElseThrow { UsernameNotFoundException("User not found") }

        val encoder = BCryptPasswordEncoder()
        user.updatePW(encoder.encode(password))
    }
}
