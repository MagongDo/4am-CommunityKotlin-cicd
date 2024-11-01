package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.domain.user.enums.UserStatus
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class UserStatusScheduler(
    private val userRepository: UserRepository
) {

    @Scheduled(fixedRate = 60000) // 1분마다 실행
    fun updateUserStatuses() {
        val inactiveThreshold = LocalDateTime.now().minusMinutes(5) // 5분 동안 활동이 없는 사용자 기준

        // 5분 동안 활동이 없는 사용자들을 OFFLINE으로 업데이트
        val inactiveUsers = userRepository.findInactiveUsersSince(inactiveThreshold)
        inactiveUsers.forEach { user ->
            user.status = UserStatus.OFFLINE
            userRepository.save(user)
        }

        println("사용자 온라인/오프라인 상태가 업데이트되었습니다.")
    }
}