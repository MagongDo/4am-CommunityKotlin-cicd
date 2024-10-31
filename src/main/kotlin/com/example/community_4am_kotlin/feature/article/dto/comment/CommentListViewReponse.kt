package com.example.community_4am_kotlin.feature.article.dto.comment

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
