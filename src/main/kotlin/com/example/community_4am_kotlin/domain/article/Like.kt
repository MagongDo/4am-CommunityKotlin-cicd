package com.example.Community_4am_Kotlin.domain.article

import com.example.Community_4am_Kotlin.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "like")
@EntityListeners(AuditingEntityListener::class)
data class Like (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var likeid: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id", nullable = false)
    private var article: Article,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private var user: User,

    private var likedStatus:Boolean,

    @CreatedDate
    @Column(name="created_at")
    private var createdAt: LocalDateTime,
    @LastModifiedDate
    @Column(name="updated_at")
    private var updatedAt: LocalDateTime
    ){
    fun changeLikedStatus(status: Boolean) {
        likedStatus = !status
    }
}