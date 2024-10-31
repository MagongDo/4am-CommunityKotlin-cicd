package com.example.community_4am_kotlin.domain.friend

import com.example.community_4am_kotlin.domain.user.User
import jakarta.persistence.*

@Entity
@Table(name = "friends")
data class Friend(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    var friend: User,

    @Enumerated(EnumType.STRING)
    var status: FriendStatus = FriendStatus.PENDING // 친구 상태 (예: 요청, 수락, 거절 등)

    // 다른 속성 추가 가능, 예: 요청 날짜 등
)

