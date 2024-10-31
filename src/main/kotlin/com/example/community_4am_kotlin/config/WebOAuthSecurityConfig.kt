package com.example.community_4am_kotlin.config

import com.example.Community_4am_Kotlin.config.jwt.TokenProvider
import com.example.community_4am_kotlin.config.jwt.TokenAuthenticationFilter
import com.example.community_4am_kotlin.config.oauth.CustomLogoutHandler
import com.example.community_4am_kotlin.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository
import com.example.community_4am_kotlin.config.oauth.OAuth2SuccessHandler
import com.example.community_4am_kotlin.config.oauth.Oauth2UserCustomService
import com.example.community_4am_kotlin.feature.user.repository.RefreshTokenRepository
import com.example.community_4am_kotlin.feature.user.service.UserDetailService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
class WebOAuthSecurityConfig(
    private val oauth2UserCustomService: Oauth2UserCustomService,
    private val tokenProvider: TokenProvider,
    private val userDetailService: UserDetailService,
    private val userService: UserService,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    // 스프링 시큐리티의 기본 보안 설정을 비활성화하는 부분
    @Bean
    fun configure(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring().requestMatchers("/img/**", "/css/**", "/js/**")
        }
    }

    // 메인 보안 설정 메서드
    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf { it.disable() }
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .logout { it.disable() }

        // 세션을 사용하지 않는 방식으로 설정 (무상태)
        http.sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.ALWAYS) }

        // JWT 토큰을 처리하는 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)

        // API 토큰 발급 경로와 파일 업로드 경로는 인증 없이 접근 가능
        http.authorizeHttpRequests {
            it.requestMatchers("/api/token", "/api/upload/**", "/api/login", "/ws/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
        }

        // OAuth2 로그인 설정
        http.oauth2Login {
            it.loginPage("/login")
                .authorizationEndpoint { endpoint ->
                    endpoint.authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                }
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint { userInfo ->
                    userInfo.userService(oauth2UserCustomService)
                }
        }

        // 로그아웃 설정
        http.logout {
            it.logoutRequestMatcher(AntPathRequestMatcher("/logout", "GET"))
                .addLogoutHandler(CustomLogoutHandler(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                .logoutSuccessUrl("/login")
        }

        // 예외 처리
        http.exceptionHandling {
            it.defaultAuthenticationEntryPointFor(
                HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                AntPathRequestMatcher("/api/**")
            )
        }

        return http.build()
    }

    // OAuth2 인증 성공 후 토큰을 발급하는 커스텀 핸들러
    @Bean
    fun oAuth2SuccessHandler(): OAuth2SuccessHandler {
        return OAuth2SuccessHandler(
            tokenProvider,
            refreshTokenRepository,
            oAuth2AuthorizationRequestBasedOnCookieRepository(),
            userService
        )
    }

    // JWT 토큰을 확인하고 인증하는 커스텀 필터
    @Bean
    fun tokenAuthenticationFilter(): TokenAuthenticationFilter {
        return TokenAuthenticationFilter(tokenProvider, userDetailService)
    }

    // OAuth2 인증 요청을 쿠키에 기반하여 저장하고 관리하는 리포지토리
    @Bean
    fun oAuth2AuthorizationRequestBasedOnCookieRepository(): OAuth2AuthorizationRequestBasedOnCookieRepository {
        return OAuth2AuthorizationRequestBasedOnCookieRepository()
    }

    // 사용자 비밀번호를 암호화하기 위해 BCryptPasswordEncoder를 사용
    @Bean
    fun bCryptPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }
}