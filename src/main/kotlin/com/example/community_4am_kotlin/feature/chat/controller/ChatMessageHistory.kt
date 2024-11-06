package com.example.community_4am_kotlin.feature.chat.controller


import com.example.community_4am_kotlin.feature.chat.service.ChatMessageService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import kotlin.math.log

@RestController
class ChatMessageHistory (private val chatMessageService: ChatMessageService) {

    @GetMapping("/chat/history/{roomId}")
    fun history(@PathVariable roomId: Long): List<Map<String, String>> {
        return chatMessageService.getMessage(roomId)
    }
}