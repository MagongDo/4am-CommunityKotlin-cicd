package com.example.community_4am_kotlin.config.jwt

import com.example.Community_4am_Kotlin.config.jwt.JwtProperties
import com.example.Community_4am_Kotlin.domain.user.User
import io.jsonwebtoken.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Collections
import java.util.Date

@Service
class TokenProvider(
    private val jwtProperties: JwtProperties
) {
    // 사용자의 정보를 기반으로 JWT를 생성하는 메서드
    // 입력받은 User 객체의 이메일과 ID를 사용하여 토큰을 만들고, 만료 기간(expirationAt)을 지정
    fun generateToken(user: User, expiredAt: Duration): String {
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    // JWT 토큰 생성 메서드 : 리프레쉬 및 엑세스 토큰을 한꺼번에, 따로따로가 아닌
    // 토큰에는 헤더, 클레임, 서명 등이 포함
    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE) // 헤더 type: JWT
            .setIssuer(jwtProperties.issuer) // 발행자(iss)를 설정
            .setIssuedAt(now) // 내용 iat :현재 시간
            .setExpiration(expiry) // 내용 exp: expiry 멤버 변수값
            .setSubject(user.email) // 토큰의 주체(sub)를 설정 내용 sub : 유저의 이메일
            .claim("email", user.email) // 클래임에 이메일을 포함
            .claim("id", user.id) // 클래임 id : 유저 ID
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret) // 비밀키 설정
            .compact() // 토큰을 생성하여 문자열로 반환
    }

    // JWT 토큰 유효성 검증 메서드
    fun validToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(jwtProperties.secret) // 비밀키 설정
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false // 복호화 과정에서 에러가 나면 유효하지 않은 토큰
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    fun getAuthentication(token: String?): Authentication {
        val claims = getClaims(token)
        // 기본적으로 "ROLE_USER" 권한을 부여
        val authorities = setOf(SimpleGrantedAuthority("ROLE_USER"))

        return UsernamePasswordAuthenticationToken(
            org.springframework.security.core.userdetails.User(
                claims.subject, "", authorities
            ), token, authorities
        )
    }

    // 토큰 기반으로 유저 ID를 가져오는 메서드
    fun getUserId(token: String): Long? {
        val claims = getClaims(token)
        return claims["id", Long::class.java]
    }

    // JWT 토큰에서 클레임 정보를 가져오는 메서드
    fun getClaims(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(jwtProperties.secret) // 비밀키 설정
            .build()
            .parseClaimsJws(token)
            .body
    }

    // 토큰에서 이메일을 가져오는 메서드
    fun getEmail(token: String): String? {
        return parseClaims(token)["email", String::class.java]
    }

    // 토큰에서 클레임 정보를 가져오는 메서드
    private fun parseClaims(accessToken: String): Claims {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(jwtProperties.secret) // 비밀키 설정
                .build()
                .parseClaimsJws(accessToken)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims // 토큰이 만료된 경우 클레임 반환
        }
    }
}