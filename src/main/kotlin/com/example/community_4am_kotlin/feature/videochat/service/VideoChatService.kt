package com.example.community_4am_kotlin.feature.videochat.service

import com.example.community_4am_kotlin.domain.videochat.VideoChatReport
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatLogDTO
import com.example.community_4am_kotlin.feature.videochat.dto.VideoChatReportDTO
import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatLogRepository
import com.example.community_4am_kotlin.feature.videochat.repository.VideoChatReportRepository
import com.example.community_4am_kotlin.log
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class VideoChatService(
    private val videoChatLogRepository: VideoChatLogRepository,
    private val videoChatReportRepository: VideoChatReportRepository,
    private val userRepository : UserRepository
) {

    /**
     * 화상채팅 시작 로그를 저장하는 메서드
     *
     * @param videoChatLogDTO 화상채팅 로그 DTO
     * @return 저장된 화상채팅 로그 DTO
     */
    fun videoChatStartTimeLog(videoChatLogDTO: VideoChatLogDTO): VideoChatLogDTO {
        val videoChatLog = videoChatLogDTO.toEntity()
        videoChatLogRepository.save(videoChatLog)
        log.info("화상채팅 시작 로그 저장됨: $videoChatLog")
        return VideoChatLogDTO(videoChatLog)
    }

    /**
     * 화상채팅 종료 로그를 저장하는 메서드
     *
     * @param videoChatId 화상채팅 방 ID
     */
    fun videoChatEndTimeLog(videoChatId: String) {
//        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatId)
//            .ifEmpty { IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatId") }

        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatId)
            .ifEmpty { throw IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatId") }

        val currentDateTime = LocalDateTime.now()

        videoChatLogs.forEach { logEntry ->
            logEntry.videoChatEndAt = currentDateTime
        }
        // 변경된 로그 저장
        videoChatLogRepository.saveAll(videoChatLogs)

    }

    /**
     * 화상채팅 유저를 신고하는 메서드
     *
     * @param videoChatId 화상채팅 방 ID
     */
    fun videoChatReport(reporterUserEmail : String, videoChatReportDTO : VideoChatReportDTO) {

        val userId = userRepository.findByEmail(reporterUserEmail).get().id

        val videoChatLogs = videoChatLogRepository.findAllByVideoChatId(videoChatReportDTO.videoChatId)
            .ifEmpty { throw IllegalArgumentException("해당 ID의 로그를 찾을 수 없습니다: $videoChatReportDTO.videoChatId") }

        val videoChatLog = videoChatLogs.first()

        val currentDateTime = LocalDateTime.now()
        val videoChatReports: VideoChatReport = VideoChatReport(
            videoChatId = videoChatLog.videoChatId,
            reporterId = userId,
            reportedId = videoChatReportDTO.reportedId,
            reportType = videoChatReportDTO.reportType,
            reportDetails = videoChatReportDTO.reportDetails,
            reportTimestamp = currentDateTime
        )

        videoChatReportRepository.save(videoChatReports)

    }




































}