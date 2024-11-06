package com.example.community_4am_kotlin.feature.comment.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

data class CommentPageRequestDTO (
    @field:Min(1)
    val page:Int=1,

    @field:Min(5)
    @field:Max(100)
    val size:Int=5,
    val id:Long
){
    fun getPageable(sort: Sort): Pageable {
        return PageRequest.of(page-1,size,sort)
    }
}