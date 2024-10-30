package com.example.community_4am_kotlin.feature.notification.repository

import com.example.Community_4am_Kotlin.domain.notification.CommentAlarm
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface CommentAlarmRepository: JpaRepository<CommentAlarm, Long> {}