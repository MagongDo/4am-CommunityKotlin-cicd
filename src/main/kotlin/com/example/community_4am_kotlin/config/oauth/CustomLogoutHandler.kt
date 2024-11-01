package com.example.community_4am_kotlin.config.oauth

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.stereotype.Component

@Component
class CustomLogoutHandler(
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository // 생성자 주입
) : LogoutHandler {

    override fun logout(request: HttpServletRequest, response: HttpServletResponse, authentication: Authentication?) {
        val session: HttpSession? = request.getSession(false) // 세션을 가져옴
        session?.invalidate()?.also {
            println("Session invalidated.") // 세션 무효화
        }

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
        println("Authorization request cookies removed.") // 쿠키 삭제

        authentication?.let {
            SecurityContextHolder.clearContext() // 인증 정보가 있으면 SecurityContext를 지운다.
            println("Security context cleared.")
        }
    }
}