package com.example.community_4am_kotlin.config

import com.example.community_4am_kotlin.domain.article.Comment
import com.example.community_4am_kotlin.feature.comment.dto.CommentResponse
import org.modelmapper.ModelMapper
import org.modelmapper.PropertyMap
import org.modelmapper.convention.MatchingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RootConfig {
    @Bean
    fun modelMapper() : ModelMapper {
        return ModelMapper().apply {
            with(configuration) {
                isFieldMatchingEnabled = true
                fieldAccessLevel = org.modelmapper.config.Configuration.AccessLevel.PRIVATE
                matchingStrategy = MatchingStrategies.LOOSE

                // BooleanConverter 오류를 피하기 위해 특정 필드 무시 설정 추가
//                addMappings(object : PropertyMap<Comment,CommentResponse>() {
//                    override fun configure() {
//                        skip(destination.commentIsHidden)
//                        skip(destination.commentIsDeleted)
//                    }
//                })
            }
        }
    }
}