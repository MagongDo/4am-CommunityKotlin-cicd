package com.example.community_4am_Kotlin.config

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.stereotype.Component

@Component
class MultipartJackson2HttpMessageConverter(
    objectMapper: ObjectMapper
) : MappingJackson2HttpMessageConverter(objectMapper) {

    init {
        supportedMediaTypes = listOf(
            MediaType.APPLICATION_OCTET_STREAM,
            MediaType.MULTIPART_FORM_DATA
        )
    }
}