package com.example.community_4am_kotlin.domain.article

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "inserted_file")
data class InsertedFile(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var uuidFileName: String, // UUID 파일 이름

    @Column(nullable = false)
    var originalFileName: String, // 원래 파일 이름

    var fileType: String? = null, // 파일 타입 (MIME 타입)을 저장하는 필드

    @Lob
    @Column(name = "file_data", nullable = false, columnDefinition = "LONGBLOB")
    var fileData: ByteArray, // 파일의 실제 데이터를 바이트 배열로 저장

    @ManyToOne
    @JoinColumn(name = "article_id", nullable = false)
    @JsonIgnore
    var article: Article
) {

    fun changeArticle(article: Article) {
        this.article = article
    }

    fun changeFileName(originalFileName: String) {
        this.originalFileName = originalFileName
    }

    fun changeFileType(fileType: String) {
        this.fileType = fileType
    }

    fun changeFileData(fileData: ByteArray) {
        this.fileData = fileData
    }
}