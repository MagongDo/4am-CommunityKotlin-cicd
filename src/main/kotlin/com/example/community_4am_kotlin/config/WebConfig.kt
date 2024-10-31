package com.example.community_4am_Kotlin.config

import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

class WebConfig : WebMvcConfigurer {
    override fun addCorsMappings(corsRegistry: CorsRegistry) {
        corsRegistry.apply {
            addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
        }

    }
}