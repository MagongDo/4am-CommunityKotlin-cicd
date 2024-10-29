package com.example.Community_4am_Kotlin.feature.article.dto.comment

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

data class CommentListViewReponse(
    var commentId:Long,
    var commentAuthor:String,
    var commentContent:String,
    @JsonFormat(shape=JsonFormat.Shape.STRING,pattern="yyyy-MM-dd HH:mm:ss")
    var commentCreateAt: LocalDateTime,
    var articleId:Long,
    var parentCommentId:Long
)
{

}
