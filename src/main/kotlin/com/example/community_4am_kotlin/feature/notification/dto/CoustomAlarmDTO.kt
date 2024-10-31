package com.example.community_4am_kotlin.feature.notification.dto


import com.example.community_4am_kotlin.domain.notification.CoustomAlarm

data class CoustomAlarmDTO(
    var id: Long? = null,
    var message: String,
    var notificationDays: MutableSet<String>,
    var reserveAt: String? = null, // "HH:mm" 형식
    var status: Boolean? = null,
    var isRead: Boolean,
    var dataType: String = "CoustomAlarm"
) {
    fun toDTO(alarm: CoustomAlarm): CoustomAlarmDTO {
        return CoustomAlarmDTO(
            id = alarm.id,
            message = alarm.message,
            notificationDays = alarm.notificationDays,
            reserveAt = alarm.reserveAt.toString(), // "HH:mm" 형식으로 변환
            status = alarm.status,
            dataType = "CoustomAlarm",
            isRead = alarm.isRead
        )
    }


}
