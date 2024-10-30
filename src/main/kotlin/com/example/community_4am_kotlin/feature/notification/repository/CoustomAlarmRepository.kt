package com.example.community_4am_kotlin.feature.notification.repository


import com.example.Community_4am_Kotlin.domain.notification.CoustomAlarm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

interface CoustomAlarmRepository : JpaRepository<CoustomAlarm, Long> {
    fun findByUserId(userId: String): List<CoustomAlarm>
    fun findByIdAndUserId(id: Long, userId: String): Optional<CoustomAlarm>
}