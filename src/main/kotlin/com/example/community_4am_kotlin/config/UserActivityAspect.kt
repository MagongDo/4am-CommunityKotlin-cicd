package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.feature.user.service.UserService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.security.Principal

@Aspect
@Component

class UserActivityAspect(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(UserActivityAspect::class.java)

    // 포인트컷 정의: 모든 컨트롤러의 모든 메서드
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *) || within(@org.springframework.stereotype.Controller *)")
    fun controllerMethods() {
        // 포인트컷 메서드 - 내용 없음
    }

    // 모든 컨트롤러 메서드 실행 전에 실행되는 어드바이스
    @Before("controllerMethods()")
    fun updateLastActiveTimeForUser() {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal

        when (principal) {
            is org.springframework.security.core.userdetails.UserDetails -> {
                val username = principal.username
                logger.info("Updating last active time for user: $username")
                userService.updateLastActiveTime(username)
            }
            is String -> {
                logger.info("Updating last active time for user: $principal")
                userService.updateLastActiveTime(principal)
            }
            else -> {
                logger.warn("Unable to update last active time: Unknown principal type")
            }
        }
    }
}