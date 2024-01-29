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