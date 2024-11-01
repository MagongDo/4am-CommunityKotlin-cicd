package com.example.community_4am_kotlin.feature.article.controller

import com.example.community_4am_kotlin.feature.article.dto.ArticleViewResponse
import com.example.community_4am_kotlin.feature.article.dto.PageRequestDTO
import com.example.community_4am_kotlin.feature.article.service.ArticleService
import com.example.community_4am_kotlin.feature.comment.dto.CommentPageRequestDTO
import com.example.community_4am_kotlin.feature.comment.service.CommentService
import com.example.community_4am_kotlin.feature.like.service.LikeService
import com.example.community_4am_kotlin.feature.user.service.UserService
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ArticleViewController(
    private val articleService: ArticleService,
    private val userService: UserService,
    private val commentService: CommentService,
    private val likeService: LikeService
) {
    // 게시글 목록을 페이지 네이션과 함께 가져오기
    @GetMapping("/articles")
    fun getArticles(@ModelAttribute pageRequestDTO: PageRequestDTO, model: Model):String{
        // 페이지 요청 정보에 맞는 게시글 리스트 가져오기 (페이지네이션 적용)
        val articleListPage=articleService.getList(pageRequestDTO)
        // 현재 페이지의 게시글 목록을 모델에 추가
        model.addAttribute("articles", articleListPage.content)
        // 페이지 네이션 관련 정보를 모델에 추가
        model.addAttribute("page",articleListPage)
        // articleList.html 템플릿으로 리턴 (게시글 목록 페이지)
        return "articleList"
    }

    // 특정 게시글을 가져와서 보여줌
    @GetMapping("/articles/{id}")
    fun getArticle(@PathVariable id:Long,@ModelAttribute commentPageRequestDTO: CommentPageRequestDTO,model: Model):String{
        // ID에 해당하는 게시글 찾기
        val article=articleService.findById(id)
        articleService.getIncreaseViewCount(id) //변경된 조회수를 저장

        val commentListPage=commentService.getComments(id,commentPageRequestDTO)

        val likeCount=likeService.getLikeCount(id)//좋아요
        val commentCount=commentService.getCommentCount(id)//조회수

        // 현재 사용자 정보 가져오기 (로그인한 사용자의 이름 또는 이메일)
        val currentUserName= SecurityContextHolder.getContext().authentication.name
        // 현재 사용자가 게시글의 작성자인지 확인
        val isArticleOwner=article.author==currentUserName

        val articleUser=userService.findByEmail(article.author)
        val currentUserImage=userService.findByEmail(currentUserName).getProfileImageAsBase64()

        model.addAttribute("article",article)
        model.addAttribute("profileImage",articleUser.getProfileImageAsBase64())
        model.addAttribute("isArticleOwner", isArticleOwner)
        model.addAttribute("currentUserName", currentUserName)
        model.addAttribute("currentUserImage", currentUserImage)
        model.addAttribute("comments", commentListPage.content)
        model.addAttribute("page", commentListPage)
        model.addAttribute("likeCount", likeCount)
        model.addAttribute("commentCount", commentCount)

        return "article"
    }

    // 새 게시글 작성 또는 수정 페이지로 이동
    @GetMapping("/new-article")
    fun newArticle(@RequestParam(required = false) id:Long, model: Model):String{
        if (id == null) {
            // ID가 없으면 빈 게시글 객체를 모델에 추가 (새 글 작성)
            model.addAttribute("article", ArticleViewResponse())
        } else {
            // ID가 있으면 해당 게시글 정보를 조회해서 모델에 추가 (게시글 수정)
            val article = articleService.findById(id)
            model.addAttribute("article", ArticleViewResponse(article))
        }
        // newArticle.html 템플릿으로 리턴 (새 글 작성/수정 페이지)
        return "newArticle"
    }
}