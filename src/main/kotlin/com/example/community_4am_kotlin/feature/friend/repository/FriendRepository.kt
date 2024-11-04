package com.example.community_4am_kotlin.feature.friend.repository

import com.example.community_4am_kotlin.domain.friend.Friend
import com.example.community_4am_kotlin.domain.friend.FriendStatus
import com.example.community_4am_kotlin.domain.user.User
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface FriendRepository : JpaRepository<Friend, Long> {
  @Query("SELECT f.friend.email FROM Friend f WHERE f.user = :user AND f.status = 'ACCEPTED'")
  fun findAcceptedFriendEmailsByUser(user: User): List<String>

  // 친구 요청 수락 시 상태를 ACCEPTED로 업데이트
  @Modifying
  @Transactional
  @Query("UPDATE Friend f SET f.status = :status WHERE f.user.id = :userId AND f.friend.id = :friendId")
  fun updateFriendStatus(
    @Param("userId") userId: Long?,
    @Param("friendId") friendId: Long?,
    @Param("status") status: FriendStatus
  )

  // 친구 요청 거절 시 친구 관계 삭제
  @Modifying
  @Transactional
  @Query("DELETE FROM Friend f WHERE f.user.id = :userId AND f.friend.id = :friendId")
  fun deleteFriend(
    @Param("userId") userId: Long?,
    @Param("friendId") friendId: Long?
  )

  // 친구 목록 조회 (수락된 친구만, 온라인 우선 정렬)
  @Query("SELECT f.friend FROM Friend f WHERE f.user.id = :userId AND f.status = 'ACCEPTED' ORDER BY f.friend.status DESC")
  fun findFriendsByUserId(@Param("userId") userId: Long?): List<User>

  fun findByUserId(@Param("userId") userId: Long?): List<Friend>


}