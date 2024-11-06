package com.example.community_4am_kotlin.feature.videochat.controller

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.feature.article.dto.AddArticleRequest
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatReportDTO
import com.example.community_4am_kotlin.feature.videochat.service.VideoChatService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.security.Principal


@RestController
@RequestMapping("/api/video-chat-reports")
@Validated
class VideoChatAPIController (
    private val videoChatService : VideoChatService
) {

    @PostMapping("/report")
    fun reportUser(
        @Valid @RequestBody videoChatReportDTO : VideoChatReportDTO,
        authentication: Authentication
    ): ResponseEntity<Map<String, String>> {
        val reporterUserEmail = authentication.name // 현재 인증된 사용자의 ID 가져오기

        videoChatService.videoChatReport(reporterUserEmail, videoChatReportDTO)
//        println(reporterUserEmail)

//        VideoChatReportDTO(reportId=0, videoChatId=70cd61dd-8b68-4a6a-b1f6-b0d0dcddee0e, reporterId=null, reportedId=1,
//            reportType=spam, reportDetails=ㅎ, reportTimestamp=null, reportVideoChatCreatAt=null, reportVideoChatEndAt=null)
        val responseBody = mapOf("message" to "신고가 성공적으로 접수되었습니다.")
        return ResponseEntity.status(HttpStatus.CREATED).body(responseBody)
    }

}

