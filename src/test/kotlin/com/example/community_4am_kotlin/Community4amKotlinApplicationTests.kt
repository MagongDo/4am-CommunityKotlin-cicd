package com.example.community_4am_kotlin

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@ActiveProfiles("test")
@SpringBootTest
@TestPropertySource(locations = ["classpath:/application-test.yml"])

class Community4amKotlinApplicationTests {

    @Test
    fun contextLoads() {



    }



}
