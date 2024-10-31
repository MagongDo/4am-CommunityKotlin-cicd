package com.example.community_4am_kotlin.feature.file.service

import com.example.community_4am_Kotlin.domain.article.Article
import com.example.community_4am_Kotlin.domain.article.InsertedFile
import com.example.community_4am_kotlin.feature.file.repository.FileRepository
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.util.*

@Service
@Transactional
class FileUploadService(
    private val fileRepository: FileRepository,
    private val modelMapper: ModelMapper
) {
    fun uploadFiles(files: MutableList<MultipartFile>, article: Article): List<InsertedFile> {
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

    fun getFileByArticleIdAndUuidFileName(articleId:Long,uuidFileName:String):InsertedFile{
        return fileRepository.findByArticleIdAndUuidFileName(articleId,uuidFileName)
            ?:throw IllegalArgumentException("File not found")

    }
}


