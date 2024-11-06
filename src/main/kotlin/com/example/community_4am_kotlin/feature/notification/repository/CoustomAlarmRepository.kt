package com.example.community_4am_kotlin.feature.notification.repository


import com.example.community_4am_kotlin.domain.notification.CoustomAlarm
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface CoustomAlarmRepository : JpaRepository<CoustomAlarm, Long> {
    fun findByUserId(userId: String): List<CoustomAlarm>
    fun findByIdAndUserId(id: Long, userId: String): Optional<CoustomAlarm>
}