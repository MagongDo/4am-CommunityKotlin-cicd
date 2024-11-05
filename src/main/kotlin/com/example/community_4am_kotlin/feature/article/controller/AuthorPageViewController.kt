//package com.example.community_4am_kotlin.feature.article.controller
//
//import com.example.community_4am_kotlin.feature.article.service.ArticleService
//import com.example.community_4am_kotlin.feature.comment.service.CommentService
//import com.example.community_4am_kotlin.feature.like.service.LikeService
//import com.example.community_4am_kotlin.feature.user.service.UserService
//import org.springframework.stereotype.Controller
//import org.springframework.web.bind.annotation.GetMapping
//import org.springframework.web.bind.annotation.PathVariable
//import org.springframework.web.bind.annotation.RequestMapping
//
//
//@Controller
//@RequestMapping("/authorpage")
//class AuthorPageViewController(
//    private val articleService: ArticleService,
//    private val userService: UserService,
//    private val commentService: CommentService,
//    private val likeService: LikeService
//) {
//    // 작성자가 작성한 게시글 조회
//    @GetMapping("/articles/{author}")
//    fun getAuthorArticles(@PathVariable author: String, model: Model): String {
//        val articles = articleService.getArticlesByAuthor(author)
//        //userService.findByEmail
//        model.addAttribute("author", author)
//        model.addAttribute("articles", articles)
//        return "authorpage/authorArticles" // 작성자의 게시글 목록 페이지로 이동
//    }
//
//    // 작성자가 작성한 댓글 조회
//    @GetMapping("/comments/{author}")
//    fun getAuthorComments(@PathVariable author: String, model: Model): String {
//        val comments = commentService.getCommentsByAuthor(author)
//        model.addAttribute("author", author)
//        model.addAttribute("comments", comments)
//        return "authorpage/authorComments" // 작성자의 댓글 목록 페이지로 이동
//    }
//}