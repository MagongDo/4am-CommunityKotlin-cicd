package com.example.community_4am_kotlin.feature.notification.event

import com.example.community_4am_kotlin.domain.notification.CoustomAlarm
import org.springframework.context.ApplicationEvent

class CustomAlarmReceivedEvent(source: Any, val coustomAlarm: CoustomAlarm) : ApplicationEvent(source){

}
