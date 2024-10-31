package com.example.community_4am_kotlin.feature.file.controller

import com.example.community_4am_Kotlin.domain.article.Article
import com.example.community_4am_Kotlin.domain.article.InsertedFile
import com.example.community_4am_kotlin.feature.article.service.ArticleService
import com.example.community_4am_kotlin.feature.file.service.FileUploadService
import com.example.community_4am_kotlin.feature.like.controller.LikeApiController
import org.apache.logging.log4j.LogManager
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@RestController
@RequestMapping("/api/upload")
class FileUploadApiController(
    private val articleService: ArticleService,
    private val fileUploadService: FileUploadService
) {

    private val logger = LogManager.getLogger(LikeApiController::class.java)

    //CKEditor에서 업로드된 파일을 받아, 해당 파일을 데이터베이스에 BLOB으로 저장한 후 파일 URL을 반환
    @PostMapping
    fun uploadFile(@RequestParam("upload") file: MultipartFile,@RequestParam articleId:Long):ResponseEntity<Map<String,Any>> {
        // articleId로 Article 객체 조회
        val article: Article =articleService.findById(articleId)
        // 파일을 업로드하고 InsertedFile 객체 생성 (파일을 BLOB으로 저장)
        val uploadedFiles: List<InsertedFile> = fileUploadService.uploadFiles(listOf(file), article)
        // 업로드된 파일의 URL 생성 및 반환 (uuidFileName 사용)
        val fileUrl="/api/upload/file?articleId=$articleId&uuidFileName="+ URLEncoder.encode(uploadedFiles.get(0).uuidFileName, StandardCharsets.UTF_8.toString());

        // CKEditor에서 요구하는 JSON 응답 형식으로 업로드된 파일의 URL 반환
        val response= mapOf(
            "uploaded" to true,
            "url" to fileUrl
        )

        return ResponseEntity.ok(response)
    }

//    @GetMapping("/file/{articleId}/{uuidFileName}")
    @GetMapping("/file")
    fun getFile(@RequestParam articleId:Long,@RequestParam uuidFileName:String):ResponseEntity<ByteArray> {
        return try{
            // articleId와 uuidFileName으로 InsertedFile 조회
            val insertedFile:InsertedFile=fileUploadService.getFileByArticleIdAndUuidFileName(articleId,uuidFileName)?:return ResponseEntity.notFound().build()
            // 파일 데이터
            val fileData:ByteArray=insertedFile.fileData
            // MIME 타입 설정
            val mediaType= insertedFile.fileType?.let { MediaType.parseMediaType(it) }
            // 파일명 인코딩
            val encodedFileName=URLEncoder.encode(insertedFile.originalFileName,StandardCharsets.UTF_8.toString())

            // 파일 데이터와 함께 응답 생성
            ResponseEntity.ok()
                .apply {
                    header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''$encodedFileName")
                    mediaType?.let { contentType(it) }
                }
                .body(fileData)

        }catch (e:IOException){
            logger.error("Error processing file download",e)
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null)
        }
    }
}