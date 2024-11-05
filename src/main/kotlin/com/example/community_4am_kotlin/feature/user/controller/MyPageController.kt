package com.example.community_4am_kotlin.feature.user.controller

import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.user.service.MyPageService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.multipart.MultipartFile

@Controller
class MyPageController(
    private val myPageService: MyPageService, // MyPageService 주입
    private val userService: UserService
) {

    @GetMapping("/mypage/edit")
    fun getUserProfile(model: Model): String {
        val email = SecurityContextHolder.getContext().authentication.name
        val user: User = myPageService.getUserByEmail(email)
        val currentUserNickName=userService.findByUsername(email).nickname

        model.addAttribute("user", user)
        model.addAttribute("currentUserNickName", currentUserNickName)
        model.addAttribute("profileImage", user.getProfileImageAsBase64())

        return "mypage/mypageedit"
    }

    @PostMapping("/mypage/edit/updateProfileImage")
    fun updateProfileImage(@RequestParam("profileImage") profileImage: MultipartFile): String {
        val email = SecurityContextHolder.getContext().authentication.name
        myPageService.updateProfileImage(email, profileImage)

        return "redirect:/mypage/edit" // 이미지 변경 후 마이페이지로 리다이렉트
    }
}