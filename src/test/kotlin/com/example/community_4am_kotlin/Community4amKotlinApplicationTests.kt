package com.example.community_4am_kotlin

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource


@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = ["classpath:/application-dev.yml"])
class Community4amKotlinApplicationTests {
    @Test
    fun contextLoads() {
        // 테스트 코드
    }
}