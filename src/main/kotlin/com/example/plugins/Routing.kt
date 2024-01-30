package com.example.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.minio.*
import io.minio.http.Method
import java.io.ByteArrayInputStream
import com.example.minioClient as currentMinioClient

const val bucketName = "stream-service"
var videoName: String = ""
fun Application.configureRouting() {
    // код для создания ведра, при его отсутствии
    val isBucketExist = currentMinioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())
    if (!isBucketExist) {
        currentMinioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build())
        println("Bucket created successfully.")
    }
    environment.monitor.subscribe(ApplicationStopping) {
        // Код для очистки при выходе из приложения
        deleteBucketWithObjects(bucketName)
    }
    routing {
        get("/") {
            call.respondText(main_form.trimIndent(), ContentType.Text.Html)
        }
        post("/upload") {
            val multipart = call.receiveMultipart()
            var uploadedFileName = ""
            multipart.forEachPart { part ->
                when(part) {
                    is PartData.FileItem -> {
                        videoName = part.originalFileName.orEmpty()
                        val fileBytes = part.streamProvider().readAllBytes()
                        if (videoName.isNotEmpty()) {
                            try {
                                currentMinioClient.putObject(
                                    PutObjectArgs.builder()
                                        .bucket(bucketName)
                                        .`object`(videoName)
                                        .stream(
                                           ByteArrayInputStream(fileBytes),
                                            fileBytes.size.toLong(),
                                            -1
                                        )
                                        .contentType(part.contentType?.toString()).build()
                                )

                                uploadedFileName = videoName
                            } catch (e: Exception) {
                                this@configureRouting.log.error("Error uploading file: ${e.message}")
                                throw e
                            }
                        }
                    }
                    else -> {
                        this@configureRouting.log.error("Received an unsupported part type: ${part::class}")
                }

            }

                part.dispose()
            }
            if (uploadedFileName.isNotEmpty()) {
                val url = currentMinioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .`object`(uploadedFileName)
                        .build()
                )
                call.respondRedirect(url)
            } else {
                call.respond(HttpStatusCode.BadRequest, "No file uploaded or filename is missing.")
            }
        }
        get("/view-video") {
            call.respondText(video_form.trimIndent(), ContentType.Text.Html)
        }
        get("/view-video/${videoName}") {
            if(videoName == "")
                return@get call.respond(HttpStatusCode.BadRequest, "Video name is not provided.")
            // Stream video directly.
            call.respondOutputStream(ContentType.Video.MP4) {
                currentMinioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(bucketName).
                        `object`(videoName).build())
                    .use { stream ->
                    stream.copyTo(this)
                }
            }
        }
    }
}
 fun deleteBucketWithObjects(bucketName: String) {
 // Сначала удалим все объекты в bucket
 try {
        val objectsList = currentMinioClient.listObjects(
        ListObjectsArgs.builder().bucket(bucketName).recursive(true).build()
 ).iterator()

 val removeObjectList = mutableListOf<String>()

     objectsList.forEach { item ->
         try {
             removeObjectList.add(item.get().objectName())
         }
         catch (e: Exception){
             e.printStackTrace()
         }
 }
     for(removeObject in removeObjectList) {
             currentMinioClient.removeObject(
                 RemoveObjectArgs
                     .builder()
                     .bucket(bucketName)
                     .`object`(removeObject)
                     .build()
             )
     }
 // Теперь удалим сам bucket
 currentMinioClient.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build())

 println("Bucket '$bucketName' и все его содержимое были успешно удалены.")
 } catch (e: Exception) {
 println("Возникла ошибка при удалении bucket: ${e.message}")
 }
 }