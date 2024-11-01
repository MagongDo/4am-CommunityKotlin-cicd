package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.feature.user.service.UserService
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component
import java.security.Principal

@Aspect
@Component
class UserActivityAspect(
    private val userService: UserService
) {

    @Before("within(@org.springframework.web.bind.annotation.RestController *) && args(principal, ..)")
    fun updateLastActiveTimeForUser(principal: Principal) {
        val userId = userService.findUserIdByEmail(principal.name)
        userService.updateLastActiveTime(userId)
    }
}