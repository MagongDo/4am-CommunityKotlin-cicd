package com.example.community_4am_kotlin.feature.article.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

@Schema(description = "게시글 목록 조회 시 페이징 정보 지정")
// 페이징을 위한 DTO
data class PageRequestDTO(
    @field:Schema(description = "페이지 번호 - 첫 번째 페이지는 1로 시작")
    var page: Int = 1,

    @field:Schema(description = "한 페이지에 표시할 게시물의 수")
    @field:Max(10)
    var size: Int = 10
) {
    // 페이지 번호, 페이지 게시물 수, 정렬 순서를 Pageable 객체로 반환
    fun getPageable(sort: Sort): Pageable {
        val pageNum = if (page < 1) 0 else page - 1 // 페이지는 0부터 시작하므로 -1
        val sizeNum = if (size > 10) 10 else size
        return PageRequest.of(pageNum, sizeNum, sort)
    }
}