package com.example.community_4am_kotlin.config

import org.modelmapper.ModelMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class AppConfig {
    @Bean
    fun modelMapper(): ModelMapper {
        return ModelMapper()
    }
}