package com.example.community_4am_kotlin.config.oauth


import com.example.community_4am_kotlin.config.jwt.TokenProvider
import com.example.community_4am_kotlin.domain.user.RefreshToken
import com.example.community_4am_kotlin.feature.user.util.CookieUtil
import com.example.community_4am_kotlin.feature.user.repository.RefreshTokenRepository
import com.example.community_4am_kotlin.feature.user.service.UserService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import java.io.IOException
import java.time.Duration


@Component
class OAuth2SuccessHandler(
    private val tokenProvider: TokenProvider,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val authorizationRequestRepository: OAuth2AuthorizationRequestBasedOnCookieRepository,
    private val userService: UserService
) : SimpleUrlAuthenticationSuccessHandler() {

    companion object {
        const val REFRESH_TOKEN_COOKIE_NAME = "refresh_token"
        val REFRESH_TOKEN_DURATION: Duration = Duration.ofDays(14)
        val ACCESS_TOKEN_DURATION: Duration = Duration.ofDays(1)
        const val REDIRECT_PATH = "/"
    }

    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oauthToken = authentication as OAuth2AuthenticationToken
        val oAuth2User = oauthToken.principal
        log.info("OAuth2User 정보: {}"+ oAuth2User.attributes)

        val email = getEmailFromOAuth2User(oAuth2User) ?: throw IllegalArgumentException("이메일을 찾을 수 없습니다.")

        val user = userService.findByEmail(email)
        log.info("여기까지감2")

        val refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION)
        saveRefreshToken(user.id!!, refreshToken, user.email)
        addRefreshTokenToCookie(request, response, refreshToken)

        val accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION)
        val targetUrl = getTargetUrl(accessToken)

        clearAuthenticationAttributes(request, response)
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }

    private fun saveRefreshToken(userId: Long, newRefreshToken: String, email: String) {
        val refreshToken = refreshTokenRepository.findByUserId(userId)
            .map { it.update(newRefreshToken) }
            .orElseGet { RefreshToken(userId = userId, refreshToken = newRefreshToken, email = email) }

        refreshTokenRepository.save(refreshToken)
    }

    private fun addRefreshTokenToCookie(request: HttpServletRequest, response: HttpServletResponse, refreshToken: String) {
        val cookieMaxAge = REFRESH_TOKEN_DURATION.seconds.toInt()
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME)
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge)
    }

    private fun clearAuthenticationAttributes(request: HttpServletRequest, response: HttpServletResponse) {
        super.clearAuthenticationAttributes(request)
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response)
    }

    private fun getTargetUrl(token: String): String {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
            .queryParam("token", token)
            .build()
            .toUriString()
    }

    private fun getEmailFromOAuth2User(oAuth2User: OAuth2User): String? {
        return when {
            oAuth2User.attributes.containsKey("email") -> oAuth2User.attributes["email"] as String
            oAuth2User.attributes.containsKey("kakao_account") -> {
                val kakaoAccount = oAuth2User.attributes["kakao_account"] as Map<*, *>
                kakaoAccount["email"] as String
            }
            else -> null
        }
    }
}