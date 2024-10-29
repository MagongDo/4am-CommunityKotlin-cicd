package com.example.Community_4am_Kotlin.feature.notification.repository


import com.example.Community_4am_Kotlin.domain.notification.CoustomAlarm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface CoustomAlarmRepository : JpaRepository<CoustomAlarm, Long> {
    fun findByUserId(userId: String): List<CoustomAlarm>
    fun findByIdAndUserId(id: Long, userId: String): Optional<CoustomAlarm>
}