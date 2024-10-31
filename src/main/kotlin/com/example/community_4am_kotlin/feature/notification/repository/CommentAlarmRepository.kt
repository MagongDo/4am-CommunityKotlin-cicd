package com.example.community_4am_Kotlin.feature.notification.repository

import com.example.community_4am_Kotlin.domain.notification.CommentAlarm
import org.springframework.data.jpa.repository.JpaRepository

interface CommentAlarmRepository: JpaRepository<CommentAlarm, Long> {}