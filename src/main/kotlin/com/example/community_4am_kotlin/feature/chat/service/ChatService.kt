package com.example.community_4am_kotlin.feature.chat.service

import org.springframework.web.socket.WebSocketSession
import java.util.concurrent.ConcurrentHashMap

interface ChatService {
    fun handleUserConnection(session: WebSocketSession, roomId : String, accountId : String, roomSessions : ConcurrentHashMap<String,MutableMap<String,WebSocketSession>>)
    fun handleUserDisconnection(session: WebSocketSession,roomId : String, email : String)
    fun memberListUpdated(roomId: String,roomSessions : ConcurrentHashMap<String,MutableMap<String,WebSocketSession>>)
    fun getUserCount(roomId: String): Int
}