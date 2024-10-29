package com.example.Community_4am_Kotlin.feature.notification.repository

import com.example.Community_4am_Kotlin.domain.notification.CommentAlarm
import org.springframework.data.jpa.repository.JpaRepository

interface CommentAlarmRepository: JpaRepository<CommentAlarm, Long> {}