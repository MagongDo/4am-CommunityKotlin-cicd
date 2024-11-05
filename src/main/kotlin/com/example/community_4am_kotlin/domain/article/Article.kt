package com.example.community_4am_kotlin.domain.article


import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import org.hibernate.annotations.BatchSize
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "article")
@EntityListeners(AuditingEntityListener::class)
data class Article(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "article_id", nullable = false)
    var id: Long? = null,

    @Column(name = "title")
    var title: String,
    @Column(columnDefinition = "LONGTEXT")
    var content: String,

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
//    @JsonIgnore
    var files: MutableList<InsertedFile> = mutableListOf(),

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], orphanRemoval = true)
    var likes: MutableList<Like> = mutableListOf(),

    @OneToMany(mappedBy = "article", cascade = [CascadeType.ALL], orphanRemoval = true)
    var comments: MutableList<Comment> = mutableListOf(),

    @Column(name = "author", nullable = false)
    var author: String,

    @CreatedDate
    @Column(name = "created_at")
    var createdAt: LocalDateTime ?= null,

    @LastModifiedDate
    @Column(name = "update_at")
    var updatedAt: LocalDateTime ?=null,

    @Column(name = "view_count")
    var viewCount: Long = 0L,

    @Column(name = "like_count")
    var likeCount: Long = 0L,

    @Column(name = "is_temporary", nullable = false)
    var isTemporary: Boolean = false // 임시 게시글 플래그

) {

    fun addFiles(files: List<InsertedFile>?) {
        files?.forEach { file ->
            file.changeArticle(this)
            this.files.add(file)
        }
    }

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    fun changeAuthor(author: String) { this.author = author }

    fun changeTitle(title: String) { this.title = title }

    fun changeContent(content: String) { this.content = content }

    fun isIncrementViewCount() { this.viewCount++ }

    fun changeLikeCount(likeCount: Long) { this.likeCount=likeCount }

    override fun toString(): String {
        return "Article(id=$id, title='$title')"
    }
}