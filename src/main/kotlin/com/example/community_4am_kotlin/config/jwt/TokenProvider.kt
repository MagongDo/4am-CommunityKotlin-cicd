package com.example.community_4am_kotlin.config.jwt

import com.example.community_4am_kotlin.domain.user.User
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.Date

@Service
class TokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    // JWT 토큰 생성 메서드
    fun generateToken(user: User, expiredAt: Duration): String {
        val now = Date()
        return makeToken(Date(now.time + expiredAt.toMillis()), user)
    }

    // JWT 토큰 생성
    private fun makeToken(expiry: Date, user: User): String {
        val now = Date()

        return Jwts.builder()
            .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
            .setIssuer(jwtProperties.issuer)
            .setIssuedAt(now)
            .setExpiration(expiry)
            .setSubject(user.email)
            .claim("email", user.email)
            .claim("id", user.id)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact()
    }

    // JWT 토큰 유효성 검증 메서드
    fun validToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
            true
        } catch (e: Exception) {
            false
        }
    }

    // 토큰 기반으로 인증 정보를 가져오는 메서드
    fun getAuthentication(token: String?): Authentication {
        val claims = getClaims(token)
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
        val idClaim = claims["id"] as? Number
        return idClaim?.toLong()
    }

    // JWT 토큰에서 클레임 정보를 가져오는 메서드
    fun getClaims(token: String?): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
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
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(accessToken)
                .body
        } catch (e: ExpiredJwtException) {
            e.claims
        }
    }
}
