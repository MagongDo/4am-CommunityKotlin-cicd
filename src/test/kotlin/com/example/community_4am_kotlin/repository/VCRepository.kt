package com.example.community_4am_kotlin.repository

import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatLogRepository
import com.example.community_4am_kotlin.log
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.time.LocalDateTime


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VCRepository @Autowired constructor(
    private val videoChatLogRepository: VideoChatLogRepository
){

    @Test
    fun contextLoads() {

        val videoChatId :String = "testIdtheTh";
        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatId)
            .ifEmpty { throw IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatId") }
        log.info(videoChatLogs)

        val currentDateTime = LocalDateTime.now()

        videoChatLogs.forEach { logEntry ->
            logEntry.videoChatEndAt = currentDateTime
        }
        // 변경된 로그 저장
        videoChatLogRepository.saveAll(videoChatLogs)

        // 변경된 로그 출력
        println(videoChatLogs)

    }
}