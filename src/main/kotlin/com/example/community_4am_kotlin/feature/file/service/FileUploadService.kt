package com.example.community_4am_kotlin.feature.file.service

import com.example.community_4am_kotlin.domain.article.Article
import com.example.community_4am_kotlin.domain.article.InsertedFile
import com.example.community_4am_kotlin.feature.file.repository.FileRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
class FileUploadService(
    private val fileRepository: FileRepository,
) {

    @Transactional
    fun uploadFiles(files: List<MultipartFile>, article: Article): List<InsertedFile> {
        val uploadedFiles: MutableList<InsertedFile> = mutableListOf<InsertedFile>()

        for (file in files) {
            try {
                val uuidFileName: String = UUID.randomUUID().toString() + "_" + file.originalFilename
                val fileData = file.bytes

                // `originalFilename`과 `contentType`에 null-safe 처리 추가
                val insertedFile = InsertedFile(
                    uuidFileName = uuidFileName,
                    originalFileName = file.originalFilename ?: "unknown_filename", // null 처리
                    fileType = file.contentType ?: "unknown/type", // null 처리
                    fileData = fileData,
                    article = article
                )
                uploadedFiles.add(fileRepository.save(insertedFile))
            } catch (e: IOException) {
                throw RuntimeException("File upload failed: ${file.originalFilename}", e)
            }
        }
        return uploadedFiles
    }

    fun getFileByArticleIdAndUuidFileName(articleId:Long,uuidFileName:String): InsertedFile {
        return fileRepository.findByArticleIdAndUuidFileName(articleId,uuidFileName)
            ?:throw IllegalArgumentException("File not found")

    }

    //삽입된 이미지 파일 삭제
    @Transactional
    fun deleteFiles(files: List<InsertedFile>){
        files.forEach { fileRepository.delete(it) }
    }

    //----------------------------
    //글 등록전 임시파일 생성
    // 파일 임시 업로드
    @Transactional
    fun uploadFilesTemporarily(files: List<MultipartFile>, article: Article) {
        files.forEach { file ->
            val insertedFile = InsertedFile(
                uuidFileName = UUID.randomUUID().toString() + "_" + file.originalFilename,
                originalFileName = file.originalFilename ?: "unknown_filename",
                fileType = file.contentType ?: "unknown/type",
                fileData = file.bytes,
                article = article,
                isTemporary = true  // 임시 플래그 설정
            )
            fileRepository.save(insertedFile)
        }
    }

    // 임시 파일 확정
    @Transactional
    fun confirmTemporaryFiles(article: Article) {
        val temporaryFiles = fileRepository.findByArticleAndIsTemporary(article, true)
        temporaryFiles.forEach { it.isTemporary = false }  // isTemporary 값을 false로 설정
        fileRepository.saveAll(temporaryFiles)  // 변경 사항 저장
    }

    // 임시 파일 삭제
    @Transactional
    fun deleteTemporaryFiles(article: Article) {
        fileRepository.deleteByArticleAndIsTemporary(article)
    }
//--------------------------------

}
