package com.example.community_4am_kotlin.feature.user.service

import com.example.Community_4am_Kotlin.feature.user.repository.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
@RequiredArgsConstructor
class MyPageService(
    private val userRepository: UserRepository // UserRepository 주입
) {

    fun getUserByEmail(email: String): User {
        return userRepository.findByEmail(email)
            .orElseThrow { RuntimeException("User not found") }
    }

    @Transactional
    fun updateProfileImage(email: String, profileImage: MultipartFile?) {
        // Optional로 반환된 객체를 처리
        val user = userRepository.findByEmail(email)
            .orElseThrow { UsernameNotFoundException("User not found") }

        var profileUrl: String? = null
        var profileImageBytes: ByteArray? = null

        if (profileImage != null) {
            try {
                profileImageBytes = profileImage.bytes
                val fileName = "${UUID.randomUUID()}_${profileImage.originalFilename}"

                profileUrl = fileName
            } catch (e: IOException) {
                e.printStackTrace()
                throw RuntimeException("Failed to process the profile image", e)
            }
        }
        user.setProfileImage(profileImageBytes, profileUrl)
        userRepository.save(user)
    }
}