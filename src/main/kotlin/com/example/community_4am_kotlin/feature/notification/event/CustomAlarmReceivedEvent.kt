package com.example.community_4am_kotlin.feature.notification.event

import com.example.Community_4am_Kotlin.domain.notification.CoustomAlarm
import org.springframework.context.ApplicationEvent

class CustomAlarmReceivedEvent(source: Any, val coustomAlarm: CoustomAlarm) : ApplicationEvent(source){

}
