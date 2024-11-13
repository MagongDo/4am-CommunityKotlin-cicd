package com.example.community_4am_kotlin

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:/application-dev.properties"])
class Community4amKotlinApplicationTests {
    @MockBean
    private lateinit var redisTemplate: RedisTemplate<Any, Any>

    @MockBean
    private lateinit var redisConnectionFactory: RedisConnectionFactory
    @Test
    fun contextLoads() {
        // 테스트 코드
    }
}