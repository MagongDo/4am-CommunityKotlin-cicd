package com.example.community_4am_kotlin.feature.notification.event


import com.example.community_4am_Kotlin.feature.notification.AlarmType
import org.springframework.context.ApplicationEvent

class NotificationEvent(
    source: Any,
    val recipient: String?=null,
    val message: String,
    val alarmType: AlarmType
) : ApplicationEvent(source)