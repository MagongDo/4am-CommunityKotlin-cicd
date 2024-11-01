package com.example.community_4am_kotlin.feature.article.controller

import com.example.community_4am_kotlin.feature.article.service.ArticleService
import com.example.community_4am_kotlin.feature.comment.service.CommentService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/mypage")
class MyPageViewController(
    private val articleService: ArticleService,
    private val userService:UserService,
    private val commentService: CommentService,
    private val likeService: LikeService
) {
    @GetMapping
    fun myPage(model: Model): String {
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        model.addAttribute("currentUserName", currentUserName)

        return "mypage/mypageMain"
    }

    @GetMapping("/articles")
    fun myPageArticles(model: Model): String {
        val currentuserName= SecurityContextHolder.getContext().authentication.name
        model.addAttribute("currentUserName", currentuserName)
        val userArticlesLists=articleService.getUserAllArticles(currentuserName)
        model.addAttribute("userArticlesLists", userArticlesLists)

        return "mypage/articles"
    }

    @GetMapping("/comments")
    fun myPageComments(model: Model): String {
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        model.addAttribute("currentUserName", currentUserName)
        val userCommentsLists=commentService.getUserAllComments(currentUserName)
        model.addAttribute("userCommentsLists", userCommentsLists)

        return "mypage/comments"
    }

    @GetMapping("/likes")
    fun myPageLikes(model: Model): String {
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        model.addAttribute("currentUserName", currentUserName)
        val userLikedArticlesLists=likeService.getUserAllArticlesAndLikes(currentUserName)
        model.addAttribute("userLikedArticlesLists", userLikedArticlesLists)

        return "mypage/likes"
    }

    @GetMapping("/chat-history")
    fun myPageChats(model: Model): String {
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        model.addAttribute("currentUserName", currentUserName)

        return "mypage/chats-history"
    }
}












