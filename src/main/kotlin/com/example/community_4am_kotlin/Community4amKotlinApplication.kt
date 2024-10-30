package com.example.community_4am_kotlin

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Community4amKotlinApplication

inline val <reified T> T.log: Logger
    get() = LogManager.getLogger()

fun main(args: Array<String>) {
    runApplication<Community4amKotlinApplication>(*args)
}
