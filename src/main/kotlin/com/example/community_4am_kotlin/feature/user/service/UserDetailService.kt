package com.example.community_4am_kotlin.feature.user.service


import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service

// 스프링 시큐리티에서 사용자 인증 시 사용자를 이메일을 통해 조회하여, 인증 처리에 필요한 사용자 정보를 제공
class UserDetailService(
    private val userRepository: UserRepository
) : UserDetailsService { // 스프링 시큐리티에서 사용자의 정보를 가져오는 UserDetailsService 인터페이스 구현

    override fun loadUserByUsername(email: String): UserDetails {
        // email을 기준으로 사용자 정보를 조회하여 반환
        return userRepository.findByEmail(email)
            .orElseThrow { IllegalArgumentException(email) } // 메서드나 생성자가 예상하지 못한 인자를 받았을 때 발생하는 예외
    }
}