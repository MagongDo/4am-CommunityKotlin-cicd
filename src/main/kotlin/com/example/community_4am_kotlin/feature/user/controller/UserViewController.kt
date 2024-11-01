package com.example.community_4am_kotlin.feature.user.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class UserViewController {

    // 로그인 페이지로 이동하는 메서드
    @GetMapping("/login")  // "/login" 경로로 GET 요청을 처리
    fun login(): String {
        // oauthLogin.html 템플릿으로 이동 (로그인 페이지)
        return "oauthLogin"
    }

    // 회원가입 페이지로 이동하는 메서드
    @GetMapping("/signup")  // "/signup" 경로로 GET 요청을 처리
    fun signup(): String {
        // signup.html 템플릿으로 이동 (회원가입 페이지)
        return "signup"
    }
}