package com.example.community_4am_kotlin.feature.user.controller


import com.example.Community_4am_Kotlin.feature.user.dto.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.hibernate.query.sqm.tree.SqmNode.log


import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.security.Principal

@Controller
class UserApiController(
    private val userService: UserService,  // 사용자 서비스 클래스 (회원가입, 로그아웃 처리)
    private val articleService: ArticleService,
    private val commentService: CommentService,
    private val likeService: LikeService
) {

    // 회원가입 요청 처리 메서드
    @PostMapping("/user") // HTTP POST 요청으로 회원가입 처리
    fun signup(request: AddUserRequest): String {
        userService.save(request) // UserService를 통해 회원가입 로직 수행
        return "redirect:/login" // 회원가입이 완료되면 로그인 페이지로 리다이렉트
    }

    // 로그아웃 요청 처리 메서드
    @GetMapping("/logout") // HTTP GET 요청으로 로그아웃 처리
    fun logout(request: HttpServletRequest, response: HttpServletResponse): String {
        log.info("로그아웃 요청 수신")  // 로그아웃 요청이 들어왔음을 로깅

        // SecurityContextHolder를 통해 현재 로그인한 사용자의 이메일을 가져옴
        val email = SecurityContextHolder.getContext().authentication.name
        log.info("로그아웃 이메일: $email")

        // UserService를 통해 해당 사용자의 닉네임을 null로 설정
        userService.setNicknameNullByEmail(email)
        log.info("닉네임 null로 설정 완료")

        // Spring Security의 로그아웃 처리 메서드 호출 (현재 사용자의 세션 및 인증 정보 삭제)
        SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().authentication)

        // 로그아웃 완료 후 로그인 페이지로 리다이렉트
        return "redirect:/login"
    }

    // 사용자 탈퇴
    @DeleteMapping("/api/user")
    fun deleteUser(): ResponseEntity<Void> {
        val currentUserName = SecurityContextHolder.getContext().authentication.name
        userService.deleteUserByUsername(currentUserName)
        return ResponseEntity.ok().build()
    }

    // 사용자가 작성한 게시글 목록 조회
    @GetMapping(value = ["/api/user/articles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findUserAllArticles(principal: Principal): ResponseEntity<List<UserArticlesList>> {
        val articles = articleService.getUserAllArticles(principal.name)
        return ResponseEntity.ok(articles) // 조회된 게시글 리스트를 반환
    }

    // 사용자가 작성한 댓글과 해당 게시물 목록 조회
    @GetMapping(value = ["/api/user/comments"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findUserAllComments(principal: Principal): ResponseEntity<List<UserCommentsList>> {
        val comments = commentService.getUserAllComments(principal.name)
        return ResponseEntity.ok(comments) // 조회된 댓글 리스트를 반환
    }

    // 사용자가 댓글 작성한 게시물 조회
    @GetMapping(value = ["/api/user/commentedArticles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findUserAllCommentedArticles(principal: Principal): ResponseEntity<List<UserCommentedArticlesList>> {
        val commentedArticles = commentService.getUserAllArticlesAndComments(principal.name)
        return ResponseEntity.ok(commentedArticles) // 조회된 댓글이 작성된 게시물 리스트를 반환
    }

    @GetMapping(value = ["/api/user/likedArticles"], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseBody
    fun findUserAllLikedArticles(principal: Principal): ResponseEntity<List<UserLikedArticlesList>> {
        val likes = likeService.getUserAllArticlesAndLikes(principal.name)
        return ResponseEntity.ok(likes) // 조회된 좋아요한 게시글 리스트를 반환
    }
}