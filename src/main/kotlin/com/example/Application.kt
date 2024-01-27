package com.example

import com.example.plugins.*
import io.ktor.utils.io.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.minio.BucketExistsArgs
import io.minio.MakeBucketArgs
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.GetObjectArgs
import io.minio.StatObjectArgs
import io.minio.*
import io.minio.GetPresignedObjectUrlArgs
import io.minio.errors.MinioException
import io.minio.http.Method
import java.io.InputStream
import java.io.File
//import kotlinx.html.*
//import io.ktor.application.*
//import io.ktor.http.*
//import io.ktor.response.*
//import io.ktor.routing.*
//import io.ktor.server.engine.*
//import io.ktor.server.netty.*
//import java.io.File


fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            post("/upload") {
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    // Обрабатываем только файловые части
                    if (part is PartData.FileItem) {
                        val fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        // Тут мы сохраняем файл локально, но его можно сразу отправить в MinIO
                        val file = File("uploads/$fileName")
                        file.writeBytes(fileBytes)

                        // Настроить MinIO клиента
                        val minioClient = MinioClient.builder()
                            .endpoint("http://localhost:9000")
                            .credentials("senton", "89314951rrsad/")
                            .build()

                        // Загружаем файл в MinIO
                        minioClient.putObject(
                            PutObjectArgs.builder()
                                .bucket("stream-service")
                                .`object`(fileName)
                                .stream(file.inputStream(), file.length(), -1) // -1 for stream size to let minio sdk compute it.
                                .contentType(part.contentType?.toString())
                                .build()
                        )
                        file.delete()
                    }

                    part.dispose()
                }
                call.respondText("Файл загружен в MinIO")
            }
            get("/upload"){

            }
            get("/") {
                call.respondText(main_form.trimIndent(), ContentType.Text.Html)
                val bucket = call.parameters["stream-service"]
                val fileKey = call.parameters["Q2xpbWJIaWdoVHV0b3JpYWxWaWRlby5tcDQ="]
                // Настройка клиента MinIO
                val minioClient = MinioClient.builder()
                    .endpoint("http://localhost:9000")
                    .credentials("senton", "89314951rrsad/")
                    .build()
                try {
                    // Проверяем наличие bucket
                    val isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket("stream-service").build())
                    when (isExist) {
                        true -> println("Bucket already exists.")
                        false -> {
                            minioClient.makeBucket(MakeBucketArgs.builder()
                                .bucket("stream-service").build());
                            println("Bucket created successfully.")
                        }
                    }
                } catch (e: MinioException) {
                    println("Error occurred: ${e.message}")
                }
                // Получаем URL к файлу для загрузки
                val presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucket)
                        .`object`(fileKey)
                        .build()
                )

                // Перенаправление клиента на полученный presigned URL
                call.respondRedirect(presignedUrl)

                val videoFile = File(presignedUrl)

                // Проверяем, существует ли файл
                if (!videoFile.exists()) {
                    call.respond(HttpStatusCode.NotFound)
                    return@get
                }

                // Устанавливаем необходимые заголовки для потоковой передачи
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, videoFile.name).toString()
                )
                call.response.header(HttpHeaders.ContentType, ContentType.Video.MP4.toString())

                // Отправляем файл
                call.respondFile(videoFile)
            }

            // Если вы хотите встроить видеоплеер на странице
            get("/view-video") {
                // Генерируем HTML с видеоплеером
                call.respondText(video_form.trimIndent(), ContentType.Text.Html)
            }
        }
    }.start(wait = true)
}

/*
fun main() {

}
fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureTemplating()
    configureRouting()
}

*/
/*
fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/video/{bucket}/{fileKey}") {
                val bucket = call.parameters["bucket"]!!
                val fileKey = call.parameters["fileKey"]!!

                val minioClient = MinioClient.builder()
                    .endpoint("your-minio-endpoint")
                    .credentials("your-access-key", "your-secret-key")
                    .build()

                val stream = minioClient.getObject(
                    GetObjectArgs.builder()
                        .bucket(bucket)
                        .`object`(fileKey)
                        .build()
                )

                val contentLength = minioClient.statObject(
                    StatObjectArgs.builder()
                        .bucket(bucket)
                        .`object`(fileKey)
                        .build()
                ).size()

                call.response.header(
                    io.ktor.http.HttpHeaders.ContentLength, contentLength.toString()
                )

                call.respondWrite(ContentType.Video.MP4) {
                    stream.use { inputStream ->
                        val buffer = ByteArray(4 * 1024)
                        var bytesRead: Int
                        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                            channel.writeFully(buffer, 0, bytesRead)
                            channel.flush()
                        }
                    }
                }
            }
        }
    }.start(wait = true)
}
*/