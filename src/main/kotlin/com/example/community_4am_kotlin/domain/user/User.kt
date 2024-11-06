package com.example.community_4am_kotlin.domain.user

import com.example.community_4am_kotlin.domain.friend.Friend
import com.example.community_4am_kotlin.domain.user.enums.UserStatus
import com.example.community_4am_kotlin.domain.article.Like
import jakarta.persistence.*
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.Base64

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener::class)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    val id: Long?=null,

    @Column(name = "email", nullable = false, unique = true)
    var email: String,

    @Column(name = "password", length = 255)
    private var password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: Role,

    @Column(name = "nickname", unique = true)
    var nickname: String? = null,

    @Lob
    @Column(name = "profile_image", columnDefinition = "LONGBLOB")
    var profileImage: ByteArray? = null,

    @Column(name = "profile_url")
    var profileUrl: String? = null,

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    var likes: MutableList<Like> = mutableListOf(),

    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val friends: MutableList<Friend> = mutableListOf(),

    var lastActiveTime: LocalDateTime,

    @Enumerated(EnumType.STRING)
    var status: UserStatus = UserStatus.OFFLINE




) : UserDetails {



   /*     class Builder {
            private var id: Long? = null
            private var email: String? = null
            private var password: String? = null
            private var role: Role? = null
            private var nickname: String? = null
            private var profileImage: ByteArray? = null
            private var profileUrl: String? = null
            private var likes: MutableList<Like> = mutableListOf()

            fun id(id: Long) = apply { this.id = id }
            fun email(email: String) = apply { this.email = email }
            fun password(password: String) = apply { this.password = password }
            fun role(role: Role) = apply { this.role = role }
            fun nickname(nickname: String?) = apply { this.nickname = nickname }
            fun profileImage(profileImage: ByteArray?) = apply { this.profileImage = profileImage }
            fun profileUrl(profileUrl: String?) = apply { this.profileUrl = profileUrl }
            fun likes(likes: MutableList<Like>) = apply { this.likes = likes }

            fun build() = User(
                id ?: throw IllegalArgumentException("Id must be provided"),
                email ?: throw IllegalArgumentException("Email must be provided"),
                password ?: throw IllegalArgumentException("Password must be provided"),
                role ?: throw IllegalArgumentException("Role must be provided"),
                nickname,
                profileImage,
                profileUrl,
                likes
            )
        }*/



        fun getProfileImageAsBase64(): String? {
        return profileImage?.let {
            "data:image/png;base64," + Base64.getEncoder().encodeToString(it)
        }
    }

    fun setProfileImage(profileImage: ByteArray?, profileUrl: String?) {
        this.profileImage = profileImage
        this.profileUrl = profileUrl
    }

    fun update(nickname: String?): User {
        this.nickname = nickname
        return this
    }

    @Transactional
    fun updatePW(password: String): User {
        require(password.isNotEmpty()) { "Password cannot be null or empty" }
        println("$password 4. 제발 되라")
        this.password = password
        return this
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("user"))
    }

    override fun getUsername(): String = email

    override fun getPassword(): String = password

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true


}