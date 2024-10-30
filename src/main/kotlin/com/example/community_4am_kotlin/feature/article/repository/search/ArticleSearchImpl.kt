package com.example.community_4am_kotlin.feature.article.repository.search

import com.example.Community_4am_Kotlin.domain.article.Article
import com.example.Community_4am_Kotlin.domain.article.QArticle
import com.example.Community_4am_Kotlin.feature.article.dto.ArticleListViewResponse
import com.querydsl.core.types.Projections
import com.querydsl.jpa.JPQLQuery
import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport

class ArticleSearchImpl :QuerydslRepositorySupport(Article::class.java), ArticleSearch {
    override fun searchDTO(pageable: Pageable): Page<ArticleListViewResponse> {
        val article:QArticle= QArticle.article
        val query=from(article)

        query.where(article.id.gt(0L))

        querydsl?.applyPagination(pageable,query)


        val articleQuery: JPQLQuery<ArticleListViewResponse> = query.select(
            Projections.constructor(
                ArticleListViewResponse::class.java,
                article
            )
        )

        val articleListPage:MutableList<ArticleListViewResponse> = articleQuery.fetch()

        val count= query.fetchCount()

        return PageImpl(articleListPage,pageable,count)

    }
}