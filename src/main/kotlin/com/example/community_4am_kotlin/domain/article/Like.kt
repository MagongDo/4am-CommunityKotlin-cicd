package com.example.community_4am_kotlin.domain.article

import com.example.community_4am_kotlin.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "likes")
@EntityListeners(AuditingEntityListener::class)
data class Like (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var likeId: Long?=null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id", nullable = false)
    private var article: Article,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
    private var user: User,

    var likedStatus:Boolean,

    @CreatedDate
    @Column(name="created_at")
    private var createdAt: LocalDateTime?=null,
    @LastModifiedDate
    @Column(name="updated_at")
    private var updatedAt: LocalDateTime?=null

    ){
    fun changeLikedStatus(status: Boolean) {
        likedStatus = !status
    }
}