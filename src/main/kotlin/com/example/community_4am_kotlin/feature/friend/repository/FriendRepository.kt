package com.example.community_4am_kotlin.feature.friend.repository

import com.example.community_4am_kotlin.domain.friend.Friend
import org.springframework.data.jpa.repository.JpaRepository

interface FriendRepository : JpaRepository<Friend, Long> {}