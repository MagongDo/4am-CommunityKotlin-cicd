package com.example.community_4am_kotlin.feature.user.controller

import com.example.Community_4am_Kotlin.feature.user.dto.Token.CreateAccessTokenRequest
import com.example.Community_4am_Kotlin.feature.user.dto.Token.CreateAccessTokenResponse
import com.example.community_4am_kotlin.feature.user.service.TokenService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenApiController(
    private val tokenService: TokenService // 토큰 관련 비즈니스 로직을 처리하는 TokenService
) {

    // POST 요청을 처리하여 새로운 Access Token을 발급함
    @PostMapping("/api/token")  // /api/token 경로로 POST 요청이 들어오면 처리
    fun createNewAccessToken(@RequestBody request: CreateAccessTokenRequest): ResponseEntity<CreateAccessTokenResponse> {
        // 클라이언트로부터 받은 Refresh Token으로 새로운 Access Token을 생성
        val newAccessToken = tokenService.createNewAccessToken(request.refreshToken)

        // 새로운 Access Token을 담아 응답으로 보냄 (HTTP 상태 코드는 201 Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(CreateAccessTokenResponse(newAccessToken))
    }
}