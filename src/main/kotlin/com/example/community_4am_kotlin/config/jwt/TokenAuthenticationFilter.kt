package com.example.community_4am_kotlin.config.jwt

import com.example.community_4am_kotlin.feature.user.service.UserDetailService
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class TokenAuthenticationFilter(
    private val tokenProvider: TokenProvider, // JWT 토큰을 생성하고 검증하는 역할을 담당하는 클래스
    private val userDetailService: UserDetailService
) : OncePerRequestFilter() {

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization" // Authorization 헤더의 키 값
        private const val TOKEN_PREFIX = "Bearer " // JWT 토큰의 접두사
    }

    // 필터링 로직을 구현한 메인 메서드로, 요청을 가로채어 JWT를 처리한 후, 남은 필터 체인을 계속 실행하도록 한다.
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorizationHeader = request.getHeader(HEADER_AUTHORIZATION) // 요청 헤더에서 Authorization 헤더의 값을 가져옴
        val token = getAccessToken(authorizationHeader) // 헤더에서 Bearer 접두사를 제거한 실제 JWT 토큰을 추출

        if (tokenProvider.validToken(token)) { // 토큰의 유효성을 검사
            val auth: Authentication = tokenProvider.getAuthentication(token) // 유효한 토큰인 경우 인증 정보를 가져옴
            SecurityContextHolder.getContext().authentication = auth // 인증 정보를 SecurityContext에 설정
        }

        filterChain.doFilter(request, response) // 필터 체인의 다음 필터로 요청을 전달함
    }

    private fun getAccessToken(authorizationHeader: String?): String? {
        return if (!authorizationHeader.isNullOrEmpty() && authorizationHeader.startsWith(TOKEN_PREFIX)) {
            authorizationHeader.substring(TOKEN_PREFIX.length) // Bearer 부분 제거 후 순수 토큰 반환
        } else null
    }
}