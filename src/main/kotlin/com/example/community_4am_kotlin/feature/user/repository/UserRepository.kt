package com.example.community_4am_kotlin.feature.user.repository

import com.example.community_4am_Kotlin.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): Optional<User> // email로 사용자 정보를 가져옴

    fun findByNickname(nickname: String): Optional<User>
    fun findByEmailAndNickname(email: String, nickname: String): Optional<User>

    @Modifying
    @Query("UPDATE User u SET u.profileImage = :profileImage WHERE u.email = :email")
    fun updateProfileImage(@Param("email") email: String, @Param("profileImage") profileImage: ByteArray)
}
