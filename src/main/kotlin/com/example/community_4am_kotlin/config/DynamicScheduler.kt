package com.example.community_4am_kotlin.config

import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledFuture

@Component
class DynamicScheduler (
    val taskScheduler: TaskScheduler
) {
    val tasks: ConcurrentHashMap<Long, ScheduledFuture<*>> = ConcurrentHashMap()
    fun scheduleTask(taskId: Long, task: Runnable, startTime: Date) {

        val future = taskScheduler.schedule({
            try {
                task.run()
                tasks.remove(taskId)
            } catch (e: Exception) {
                println("Error executing scheduled task with ID: $taskId: ${e.message}")
            }
        }, startTime)
        tasks[taskId] = future
    }

    fun cancelTask(taskId: Long) {
        val future = tasks.remove(taskId)
        if (future != null) {
            future.cancel(false)
            println("Cancelled task with ID: $taskId")
        }
    }
}