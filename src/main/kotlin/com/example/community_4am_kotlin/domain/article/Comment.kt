package com.example.community_4am_kotlin.domain.article

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.AbstractPersistable_.id
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
@Entity
@Table(name = "comment")
@EntityListeners(AuditingEntityListener::class)
data class Comment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var commentId: Long? = null,

    var commentAuthor: String? = null,
    var commentContent: String? = null,

    @CreatedDate
    var createdAt: LocalDateTime? = null,
    @LastModifiedDate
    var updatedAt: LocalDateTime? = null,

    @JsonProperty("commentIsHidden")
    var commentIsHidden: Boolean? = false,
    @JsonProperty("commentIsDeleted")
    var commentIsDeleted: Boolean? = false,

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    var article: Article,

    @ManyToOne
    @JoinColumn(name = "parent_comment_id", nullable = true)  // 최상위 댓글을 위해 nullable=true로 설정
    @JsonIgnore
    var parentComment: Comment? = null,  // null 허용

    @OneToMany(mappedBy = "parentComment", cascade = [(CascadeType.MERGE)], orphanRemoval = true)
    var childComments: MutableList<Comment> = mutableListOf()
) {
    fun addChildComment(childComment: Comment) {
        this.childComments.add(childComment)
        childComment.changeParentComment(this)
    }

    // setter
    fun changeParentComment(parentComment: Comment) {
        this.parentComment = parentComment
    }

    fun changeCommentContent(commentContent: String?) {
        this.commentContent = commentContent
    }

    fun changeCommentIsHidden(commentIsHidden: Boolean) {
        this.commentIsHidden = commentIsHidden
    }

    fun changeCommentIsDeleted(commentIsDeleted: Boolean) {
        this.commentIsDeleted = commentIsDeleted
    }

    fun update(commentContent: String) {
        this.commentContent = commentContent
    }

}
