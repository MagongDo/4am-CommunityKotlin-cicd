package com.example.community_4am_kotlin.domain.article

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "comment")
@EntityListeners(AuditingEntityListener::class)
data class Comment (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private var commentId: Long?=null,
    private var commentAuthor: String,
    private var commentContent: String,
    @CreatedDate
    private var createdDate: LocalDateTime?=null,
    @LastModifiedDate
    private var modifiedDate: LocalDateTime?=null,
    @JsonProperty("commentIsHidden")
    private var commentIsHidden: Boolean?=null,
    @JsonProperty("commentIsDeleted")
    private var commentIsDeleted: Boolean?=null,

    @ManyToOne
    @JoinColumn(name="article_id",nullable=false)
    private var article: Article,
    @ManyToOne
    @JoinColumn(name="parent_comment_id",nullable=false)
    @JsonIgnore
    private var parentComment: Comment,
    @OneToMany(mappedBy="parentComment", cascade = [(CascadeType.MERGE)], orphanRemoval = true)
    private var childComments: MutableList<Comment> = mutableListOf(),
){
    fun addChildComment(childComment: Comment){
        this.childComments.add(childComment)
        childComment.changeParentComment(this)
    }

    fun changeParentComment(parentComment: Comment){this.parentComment = parentComment}
    fun changeCommentContents(commentContent: String){this.commentContent = commentContent}
    fun changeCommentIsHidden(commentIsHidden: Boolean){this.commentIsHidden = commentIsHidden}
    fun changeCommentIsDeleted(commentIsDeleted: Boolean){this.commentIsDeleted = commentIsDeleted}
    fun update(commentContent: String){this.commentContent = commentContent}

}