package com.example.Community_4am_Kotlin.feature.article.dto.comment

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class CommentPageRequestDTO(
    @Min(1)
    var page: Int = 1, // 페이지 번호 - 첫 번째 페이지는 1부터 시작

    @Min(5)
    @Max(100)
    var size: Int = 5, // 한 페이지 리뷰 수

    var id: Long? = null // 게시물 아이디
) {
    // 페이지 번호, 페이지 게시물 수, 정렬 순서를 Pageable 객체로 반환
    fun getPageable(sort: Sort): Pageable {
        return PageRequest.of(page - 1, size, sort)
    }
}