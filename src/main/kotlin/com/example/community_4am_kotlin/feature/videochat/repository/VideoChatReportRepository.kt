package com.example.community_4am_kotlin.feature.videochat.repository

import com.example.community_4am_kotlin.domain.videochat.VideoChatLog
import com.example.community_4am_kotlin.domain.videochat.VideoChatReport
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface  VideoChatReportRepository: JpaRepository<VideoChatReport, Long > {



}