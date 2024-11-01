package com.example.community_4am_kotlin.feature.notification.controller

import com.example.community_4am_kotlin.domain.notification.Notification
import com.example.community_4am_kotlin.feature.notification.dto.CoustomAlarmDTO
import com.example.community_4am_kotlin.feature.notification.dto.NotificationDTO
import com.example.community_4am_kotlin.feature.notification.service.CoustomAlarmService
import com.example.community_4am_kotlin.feature.notification.service.NotificationService
import org.modelmapper.ModelMapper
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.*
import java.security.Principal

@Controller
@RequestMapping("/api/notifications")
class NotificationController (
    val notificationService: NotificationService,
    val coustomAlarmService: CoustomAlarmService,
    val modelMapper: ModelMapper
){

    fun getAuthor(principal: Principal):String{
        return principal.name
    }

    @GetMapping
    fun getAllUnreadNotifications(principal: Principal): ResponseEntity<List<NotificationDTO>> {
        val author:String = getAuthor(principal) // 사용자 식별 로직
        val notifications: MutableList<Notification> = notificationService.getUnreadNotifications(author)
        val dtos: MutableList<NotificationDTO> = notifications.map { modelMapper.map(it, NotificationDTO::class.java) }
            .toMutableList()
        return ResponseEntity.ok(dtos)
    }

    @GetMapping("/unread-count")
    fun getUnreadNotificationCount(principal: Principal): ResponseEntity<MutableMap<String,Long>> {
        var username:String=getAuthor(principal)
        var unreadCount:Long=notificationService.getUnreadNotificationsCount(username)
        var response:MutableMap<String,Long> = HashMap()
        response.put("unreadCount", unreadCount)
        return ResponseEntity.ok(response)
    }

    @PutMapping("/read/{id}")
    fun markAsRead(@PathVariable id:Long,principal: Principal): ResponseEntity<Void> {
        var author:String= getAuthor(principal)
        notificationService.markAsRead(id)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{id}")
    fun deleteNotification(@PathVariable id: Long, principal: Principal): ResponseEntity<Void> {
        val author = getAuthor(principal)
        notificationService.deleteNotification(id)
        return ResponseEntity.noContent().build()
    }


    @GetMapping("/custom")
    fun getCustomAlarms(principal: Principal): ResponseEntity<List<CoustomAlarmDTO>> {
        val author = principal.name
        val customAlarms = coustomAlarmService.getCustomAlarmsByUser(author)
        val dtos = customAlarms.map { modelMapper.map(it, CoustomAlarmDTO::class.java) }.toMutableList()
        return ResponseEntity.ok(dtos)
    }


    @PutMapping("/custom/{id}/status")
    fun updateCustomAlarmStatus(@PathVariable id: Long, @RequestBody status: Boolean, principal: Principal): ResponseEntity<Void> {
        val author = principal.name
        val updated = coustomAlarmService.updateCustomAlarmStatus(id, status, author)
        return if (updated) ResponseEntity.ok().build() else ResponseEntity.status(404).build()
    }


    @PutMapping("/custom/{id}")
    fun updateCustomAlarm(@PathVariable id: Long, @RequestBody coustomAlarmDTO: CoustomAlarmDTO): ResponseEntity<Void> {
        coustomAlarmService.updateCustomAlarm(id, coustomAlarmDTO)
        println("찍히나?: $coustomAlarmDTO")
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/custom/{id}")
    fun deleteCustomAlarm(@PathVariable id: Long): ResponseEntity<Void> {
        coustomAlarmService.deleteCustomAlarm(id)
        return ResponseEntity.noContent().build()
    }

}