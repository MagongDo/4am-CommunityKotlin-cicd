package com.example.community_4am_kotlin.repository

import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatLogRepository
import com.example.community_4am_kotlin.log
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class VCRepository (
    private val videoChatLogRepository: VideoChatLogRepository
){

    @Test
    fun contextLoads() {

        val videoChatId :String = "testIdtheTh";
        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatId)
            .ifEmpty { IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatId") }

        log.info(videoChatLogs)
    }
}