package com.example.community_4am_Kotlin.feature.user.controller

import com.example.community_4am_Kotlin.domain.user.User
import com.example.community_4am_Kotlin.feature.user.dto.Id
import com.example.community_4am_Kotlin.feature.user.dto.Pw
import com.example.community_4am_Kotlin.feature.user.service.FindUserDataService
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
    fun findUserId(model: Model): String {return "find-username"}

    @GetMapping("/find-password")
    fun findUserPassword(model: Model): String{return "find-password"}

    @PostMapping("/find-email")
    fun findEmail(dto: Id,model:Model):String{
        var user: User=userService.findEmailByNickname(dto.nickname)
        if(user.email!=null){
            model.addAttribute("resultMessage","해당 닉네임으로 가임된 이메일은"+user.email+"입니다.")
        }
        else{
            model.addAttribute("resultMessage","해당 닉네임으로 가입된 이메일이 없습니다.")
        }
        return "find-username"
    }

    @PostMapping("/find-password")
    fun findPassword(dto: Pw,model:Model):String{
        var user: User= userService.findPasswordByEmailAndNickname(dto.email,dto.nickname)
        userService.updatePasswordByEmailAndNickname(dto.email,dto.nickname,dto.password)
        if(user.password!=null){
            model.addAttribute("resultMessage","해당 이메일과 닉네임으로 새롭게 가입된 비밀번호는:"+dto.password+"입니다.")
        }
        else{
            model.addAttribute("resultMessage","이메일 또는 닉네임이 올바르지 않습니다.")
        }
        return "find-password"
    }
}