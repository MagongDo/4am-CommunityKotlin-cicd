package com.example.community_4am_kotlin.feature.user.controller


import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.user.dto.Id
import com.example.community_4am_kotlin.feature.user.dto.Pw
import com.example.community_4am_kotlin.feature.user.service.FindUserDataService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping

@Controller
class FindUserDataController(
    val userService: FindUserDataService
) {

    private val log: Logger = LoggerFactory.getLogger(FindUserDataService::class.java)

    @GetMapping("/find-username")
    fun findUserId(model: Model): String = "find-username"

    @GetMapping("/find-password")
    fun findUserPassword(model: Model): String = "find-password"

    @PostMapping("/find-email")
    fun findEmail(dto: Id, model:Model):String{
        val user = userService.findEmailByNickname(dto.nickname)

        val resultMessage = if (user.email != null) {
            "해당 닉네임으로 가입된 이메일은 ${user.email}입니다."
        } else {
            "해당 닉네임으로 가입된 이메일이 없습니다."
        }

        model.addAttribute("resultMessage", resultMessage)
        return "find-username"
    }

    @PostMapping("/find-password")
    fun findPassword(dto: Pw, model:Model):String{
        val user: User = userService.findPasswordByEmailAndNickname(dto.email,dto.nickname)
        userService.updatePasswordByEmailAndNickname(dto.email,dto.nickname,dto.password)

        val resultMessage = if (user.password != null) {
            userService.updatePasswordByEmailAndNickname(dto.email, dto.nickname, dto.password)
            "해당 이메일과 닉네임으로 새롭게 설정된 비밀번호는 ${dto.password}입니다."
        } else {
            "이메일 또는 닉네임이 올바르지 않습니다."
        }

        model.addAttribute("resultMessage", resultMessage)
        return "find-password"
    }
}