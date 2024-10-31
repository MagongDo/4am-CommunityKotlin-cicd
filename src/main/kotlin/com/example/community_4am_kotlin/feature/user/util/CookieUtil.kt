package com.example.Community_4am_Kotlin.feature.user.util

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.util.SerializationUtils
import java.util.Base64

object CookieUtil {

    // 요청값(이름, 값, 만료 기간)을 바탕으로 쿠키 추가
    fun addCookie(response: HttpServletResponse, name: String, value: String, maxAge: Int) {
        val cookie = Cookie(name, value).apply {
            path = "/"
            this.maxAge = maxAge
        }
        response.addCookie(cookie)
    }

    // 쿠키의 이름을 입력받아 쿠키 삭제
    fun deleteCookie(request: HttpServletRequest, response: HttpServletResponse, name: String) {
        if (name.isNotEmpty()) {
            val cookie = Cookie(name, null).apply {
                path = "/"
                maxAge = 0
            }
            response.addCookie(cookie)
        }
    }

    // 객체를 직렬화해 쿠키의 값으로 변환
    fun serialize(obj: Any): String {
        return Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(obj))
    }

    // 쿠키를 역직렬화해 객체로 변환
    fun <T> deserialize(cookie: Cookie?, cls: Class<T>): T {
        val decodedBytes = Base64.getUrlDecoder().decode(cookie.value)
        return cls.cast(SerializationUtils.deserialize(decodedBytes))
    }
}