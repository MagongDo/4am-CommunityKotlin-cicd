package com.example.community_4am_kotlin.feature.article.repository.search

import com.example.community_4am_kotlin.feature.article.dto.ArticleListViewResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ArticleSearch {
    fun searchDTO(pegeable: Pageable): Page<ArticleListViewResponse>
}