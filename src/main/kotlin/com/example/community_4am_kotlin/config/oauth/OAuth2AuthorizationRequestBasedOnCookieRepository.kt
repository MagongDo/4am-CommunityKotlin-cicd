package com.example.community_4am_kotlin.config.oauth

import com.example.Community_4am_Kotlin.feature.user.util.CookieUtil
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest
import org.springframework.web.util.WebUtils

// Spring Security의 OAuth2 인증 요청을 쿠키 기반으로 저장하고 불러오는 역할을 담당하는 커스텀 구현체
// 이 클래스는 AuthorizationRequestRepository<OAuth2AuthorizationRequest> 인터페이스를 구현하여,
// OAuth2 인증 요청 정보를 쿠키에 저장하고 이를 다시 불러오거나 삭제하는 기능을 제공합니다.
class OAuth2AuthorizationRequestBasedOnCookieRepository : AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    companion object {
        const val OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request" // 쿠키에 저장할 때 사용되는 쿠키의 이름
        private const val COOKIE_EXPIRE_SECONDS = 18000 // 쿠키의 만료 시간 정의 (18,000초 = 약 5시간)
    }

    // AuthorizationRequestRepository의 removeAuthorizationRequest 메서드를 구현한 것으로, 인증 요청을 제거할 때 호출됩니다.
    override fun removeAuthorizationRequest(request: HttpServletRequest, response: HttpServletResponse): OAuth2AuthorizationRequest? {
        return loadAuthorizationRequest(request) // 쿠키에서 인증 요청을 로드하여 반환
    }

    // 요청(Request) 객체로부터 쿠키를 읽어와 저장된 OAuth2 인증 요청(OAuth2AuthorizationRequest)을 로드하는 메서드
    override fun loadAuthorizationRequest(request: HttpServletRequest): OAuth2AuthorizationRequest? {
        val cookie: Cookie? = WebUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME) // 특정 이름의 쿠키를 요청에서 가져옴
        return CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest::class.java) // 쿠키에서 읽어온 값을 OAuth2AuthorizationRequest 객체로 역직렬화
    }

    // OAuth2 인증 요청을 쿠키에 저장하는 메서드
    override fun saveAuthorizationRequest(authorizationRequest: OAuth2AuthorizationRequest?, request: HttpServletRequest, response: HttpServletResponse) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response) // authorizationRequest가 null인 경우, 쿠키 삭제
            return
        }

        // 쿠키에 인증 요청을 직렬화하여 저장
        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS)
    }

    // 특정 요청(Request)에서 인증 요청 쿠키를 삭제하는 메서드
    fun removeAuthorizationRequestCookies(request: HttpServletRequest, response: HttpServletResponse) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
    }
}

// 전반적인 동작 요약:
// 저장 (saveAuthorizationRequest): OAuth2 인증 과정에서 OAuth2AuthorizationRequest 객체가 생성되면,
// 이 요청 정보를 직렬화하여 쿠키로 저장합니다.
// 불러오기 (loadAuthorizationRequest): OAuth2 인증 진행 중,
// 저장된 인증 요청이 필요한 경우 요청으로부터 쿠키를 읽어와 인증 요청 객체를 역직렬화하여 반환합니다.
// 삭제 (removeAuthorizationRequest 및 removeAuthorizationRequestCookies): OAuth2 인증 요청이 완료되거나 실패한 후,
// 해당 인증 요청 정보를 담고 있던 쿠키를 삭제합니다.