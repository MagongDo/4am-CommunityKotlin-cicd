package com.example.community_4am_kotlin.config

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync

@Configuration
@EnableAspectJAutoProxy
@EnableAsync
class ApplicationConfig {

}