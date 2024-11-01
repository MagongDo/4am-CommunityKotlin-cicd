package com.example.community_4am_kotlin.repository

import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatLogRepository
import com.example.community_4am_kotlin.log
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest


@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VCRepository @Autowired constructor(
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