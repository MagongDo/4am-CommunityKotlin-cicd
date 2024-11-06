package com.example.community_4am_kotlin.repository

import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import com.example.community_4am_kotlin.domain.videochat.VideoChatReport
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatLogDTO
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatReportDTO
import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatLogRepository
import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatReportRepository
import com.example.community_4am_kotlin.log
import com.querydsl.core.types.dsl.Wildcard.count
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VCRepository @Autowired constructor(
    private val videoChatLogRepository: VideoChatLogRepository,
    private val videoChatReportRepository: VideoChatReportRepository
){

    @Test
    fun videoChatEndTimeLogTest() {

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

    @Test
    fun videoChatReportTest() {

        val videoChatId :String = "00c994e8-bcfc-41e2-aebe-c160135e6b5a";
        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatId)
            .ifEmpty { throw IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatId") }

        val videoChatLog = videoChatLogs.first()

        val currentDateTime = LocalDateTime.now()
        val videoChatReports: VideoChatReport = VideoChatReport(
//            videoChatId = videoChatLog.videoChatId,
//            reporterId = "a",
//            reportedId = "b",
//            reportDetails = "나쁜사람",
//            reportTimestamp = currentDateTime,
//            reportVideoChatCreatAt = videoChatLog.videoChatCreateAt,
//            reportVideoChatEndAt = videoChatLog.videoChatEndAt
        )

        println("save " + videoChatReportRepository.save(videoChatReports))

    }

























}