package com.example.community_4am_kotlin.feature.videochat.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping


@Controller
class VideoChatController {
    @GetMapping("/p2p-video-chat")
    fun videoChat(): String {
        return "p2p-video-chat"
    }

    @GetMapping("/random-video-chat")
    fun randomVideoChat(): String {
        return "random-video-chat"
    }

    @GetMapping("/newRandom-video-chat")
    fun newRandomVideoChat(): String {
        return "newRandom-video-chat"
    }

    @GetMapping("/chat")
    fun Chat(): String {
        return "chat"
    }
}