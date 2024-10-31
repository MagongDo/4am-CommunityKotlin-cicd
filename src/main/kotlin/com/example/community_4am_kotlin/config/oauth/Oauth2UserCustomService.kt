package com.example.community_4am_kotlin.config.oauth

import com.example.Community_4am_Kotlin.domain.user.Role
import com.example.Community_4am_Kotlin.domain.user.User
import com.example.community_4am_kotlin.feature.user.repository.UserRepository
import org.hibernate.query.sqm.tree.SqmNode.log
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.file.Files

@Service
class Oauth2UserCustomService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val user = super.loadUser(userRequest)

        println("OAuth2User attributes: ${user.attributes}")

        saveOrUpdate(user)
        return user
    }

    // 유저가 있으면 업데이트, 없으면 유저 생성
    private fun saveOrUpdate(oAuth2User: OAuth2User): User {
        val encoder = BCryptPasswordEncoder()
        val profileImageBytes: ByteArray
        val profileUrl: String

        try {
            val defaultImage = File("src/main/resources/static/img/default.jpeg")
            profileImageBytes = Files.readAllBytes(defaultImage.toPath())
            profileUrl = "default.jpeg"
        } catch (e: IOException) {
            e.printStackTrace()
            throw RuntimeException("Failed to load the default profile image", e)
        }

        val attributes = oAuth2User.attributes
        val email: String
        val name: String

        if (attributes.containsKey("kakao_account")) {
            email = (attributes["kakao_account"] as Map<String, Any>)["email"] as String
            println("Email from Kakao: $email")
            name = (attributes["properties"] as Map<String, Any>)["nickname"] as String
        } else {
            email = attributes["email"] as String
            name = attributes["name"] as String
        }

        val user = userRepository.findByEmail(email)
            .map { entity ->
                println("User found, updating: $entity")
                entity.update(name)
            }
            .orElseGet {
                println("User not found, creating new user with email: $email")
                User(
                    email = email,
                    password = encoder.encode("123456"),
                    nickname = name,
                    profileImage = profileImageBytes,
                    profileUrl = profileUrl,
                    role = Role.ROLE_USER
                )
            }

        log.info("여기까지감3")
        return userRepository.save(user)
    }
}