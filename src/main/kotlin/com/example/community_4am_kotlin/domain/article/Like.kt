package com.example.community_4am_kotlin.domain.article

import com.example.community_4am_kotlin.domain.user.User
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "user_like")
@EntityListeners(AuditingEntityListener::class)
data class Like (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var likeid: Long?=null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="article_id", nullable = false)
     var article: Article,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id", nullable = false)
     var user: User,

    var likedStatus:Boolean,

    @CreatedDate
    @Column(name="created_at")
     var createdAt: LocalDateTime,
    @LastModifiedDate
    @Column(name="updated_at")
     var updatedAt: LocalDateTime
    ){
    fun changeLikedStatus(status: Boolean) {
        likedStatus = !status
    }
}