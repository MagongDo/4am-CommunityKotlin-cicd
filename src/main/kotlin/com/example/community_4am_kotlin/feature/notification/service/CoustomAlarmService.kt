package com.example.community_4am_kotlin.feature.notification.service


import com.example.community_4am_kotlin.domain.notification.CoustomAlarm
import com.example.community_4am_kotlin.domain.notification.Notification
import com.example.community_4am_kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.notification.AlarmType
import com.example.community_4am_kotlin.feature.notification.dto.CoustomAlarmDTO
import com.example.community_4am_kotlin.config.DynamicScheduler
import com.example.community_4am_kotlin.feature.notification.event.CustomAlarmReceivedEvent
import com.example.community_4am_kotlin.feature.notification.event.NotificationEvent
import com.example.community_4am_kotlin.feature.notification.repository.CoustomAlarmRepository
import com.example.community_4am_kotlin.feature.notification.repository.NotificationRepository
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*

@Service
@Transactional
class CoustomAlarmService(
    private val dynamicScheduler: DynamicScheduler,
    private val customAlarmRepository: CoustomAlarmRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository
) {

    // 알림 생성 시 스케줄링
    fun createOrUpdateNotification(notification: CoustomAlarm) {
        try {
            val savedNotification = customAlarmRepository.save(notification)
            scheduleNotification(savedNotification)
        } catch (e: Exception) {
            println("Error saving notification: ${e.message}")
        }
    }

    // 알림을 스케줄링하는 메소드
    fun scheduleNotification(notification: CoustomAlarm) {
        val notificationTime = notification.reserveAt
        val notificationDays = parseDays(notification.notificationDays)

        val notificationTask = Runnable {
            val today = LocalDate.now().dayOfWeek
            val now = LocalTime.now()
            if (notificationDays.contains(today) &&
                now.until(notificationTime, ChronoUnit.MINUTES) == 0L &&
                notification.status == true
            ) {
                try {
                    sendNotification(notification)
                } catch (e: Exception) {
                    throw RuntimeException(e)
                }
            }
        }

        val nextExecutionTime = getNextExecutionTime(notificationTime, notificationDays)
        dynamicScheduler.scheduleTask(notification.id!!, notificationTask, nextExecutionTime)
    }

    // 알림을 전송하는 메소드
    private fun sendNotification(notification: CoustomAlarm) {
        val recipientUser: User = userRepository.findByEmail(notification.userId)
            .orElseThrow { IllegalArgumentException("수신자를 찾을 수 없습니다.") }

        val notifications = Notification(
            alarmType = AlarmType.COUSTOM,
            message = notification.message,
            recipient = notification.userId,
            isRead = false,
            makeId = recipientUser.email,
            targetId = recipientUser.id,
            createdAt = notification.createdAt,
            user = recipientUser
        )

        // 알림 저장
        notificationRepository.save(notifications)
        // 이벤트 발행을 통해 알림 전송
        eventPublisher.publishEvent(
            NotificationEvent(this, notification.userId, notification.message, notifications.alarmType)
        )
    }

    // 다음 실행 시간을 계산하는 메소드
    private fun getNextExecutionTime(notificationTime: LocalTime, notificationDays: MutableSet<DayOfWeek>): Date {
        val today = LocalDate.now()
        val now = LocalTime.now()

        for (i in 0..6) {
            val day = today.plusDays(i.toLong()).dayOfWeek
            if (notificationDays.contains(day)) {
                val targetDate = today.plusDays(i.toLong())
                val targetTime = notificationTime

                if (i == 0 && targetTime.isBefore(now)) continue

                return Date.from(targetTime.atDate(targetDate)
                    .atZone(ZoneId.systemDefault())
                    .toInstant())
            }
        }

        val nextDate = today.plusDays(1)
        return Date.from(notificationTime.atDate(nextDate)
            .atZone(ZoneId.systemDefault())
            .toInstant())
    }

    // 요일 문자열을 Set<DayOfWeek>로 변환하는 메소드
    private fun parseDays(days: MutableSet<String>): MutableSet<DayOfWeek> {
        return days.map { DayOfWeek.valueOf(it.uppercase()) }.toSet() as MutableSet<DayOfWeek>
    }

    fun updateCustomAlarm(id: Long, coustomAlarmDTO: CoustomAlarmDTO) {
        var existingAlarm = customAlarmRepository.findById(id)
            .orElseThrow { RuntimeException("Custom Alarm not found") }
        existingAlarm.message = coustomAlarmDTO.message
        existingAlarm.notificationDays = coustomAlarmDTO.notificationDays
        existingAlarm.reserveAt = LocalTime.parse(coustomAlarmDTO.reserveAt)
        existingAlarm.status = coustomAlarmDTO.status
        customAlarmRepository.save(existingAlarm)
        scheduleNotification(existingAlarm)
    }

    fun deleteCustomAlarm(id: Long) {
        customAlarmRepository.deleteById(id)
    }

    fun getCustomAlarmsByUser(userId: String): List<CoustomAlarm> {
        return customAlarmRepository.findByUserId(userId)
    }

    fun updateCustomAlarmStatus(id: Long, status: Boolean, userId: String): Boolean {
        return customAlarmRepository.findByIdAndUserId(id, userId)
            .map {
                it.status = status
                customAlarmRepository.save(it)
                true
            }
            .orElse(false)
    }

    @EventListener
    fun handleCustomAlarmReceived(event: CustomAlarmReceivedEvent) {
        try {
            val notification = event.coustomAlarm
            createOrUpdateNotification(notification)
        } catch (e: Exception) {
            println("Error handling CustomAlarmReceivedEvent: ${e.message}")
        }
    }
}
