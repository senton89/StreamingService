package com.example.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.minio.MinioClient
import io.minio.PutObjectArgs
import io.minio.GetObjectArgs
import kotlinx.html.*
import java.io.InputStream

fun Application.configureRouting() {
    val minioClient = MinioClient.builder()
        .endpoint("http://127.0.0.1:9000")
        .credentials("senton", "89314951rrsad/")
        .build()
    routing {
        get("/"){
            call.respondHtml(HttpStatusCode.OK, HTML::createForm)
        }
        post("/upload") {
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val name = part.originalFileName as String
                    val size = part.streamProvider().available().toLong()
                    val inputStream = part.streamProvider() as InputStream
                    minioClient.putObject(
                        PutObjectArgs.builder().bucket("stream-service").`object`(name).stream(
                            inputStream, size, -1
                        )
                            .contentType("video/mp4")
                            .build()
                    )
                    //call.respondFile("minio://stream-service/ClimbHighTutorialVideo.mp4", fileName = name, configure = )
                    inputStream.close()
                }
                part.dispose()
            }
            call.respondText("Файл загружен")
        }
    }
}
fun HTML.createForm() {
    head {
        title("Test Form")
    }
    body {
        form(
            action = "/submit-form",
            method = FormMethod.post,
            encType = FormEncType.multipartFormData
        ) {
            textInput(name = "username") {
                placeholder = "Username"
            }
            passwordInput(name = "password") {
                placeholder = "Password"
            }
            fileInput(name = "fileToUpload") {}
            submitInput { value = "Submit Form" }
        }
    }
}
fun HTML.createPlayer() {
    head {
        title("Player")
    }
    body {
        form(
            action = "/submit-form",
            method = FormMethod.post,
            encType = FormEncType.multipartFormData
        ) {
            textInput(name = "username") {
                placeholder = "Username"
            }
            passwordInput(name = "password") {
                placeholder = "Password"
            }
            fileInput(name = "fileToUpload") {}
            submitInput { value = "Submit Form" }
        }
    }
}
/*fun Route.videoStreamingController() {

    get("/video-stream") {
        val videoFile = "minio://stream-service/ClimbHighTutorialVideo.mp4"
        call.respondFile(File(videoFile))
    }
}*/
fun Route.videoPlayerPageController() {
    get("/watch") {
        call.respondHtml {
            head {
                title("Video Stream")
                script(src = "https://vjs.zencdn.net/7.17.0/video.js") {}
                link(rel = "stylesheet", href = "https://vjs.zencdn.net/7.17.0/video-js.css") {}
            }
            body {
                video(classes = "video-js vjs-default-skin" ) {
                    controls = true
                    id = "video_player"
                    src = "/video-stream"
//                    source(src = "/video-stream", type = "video/mp4")
                }
                script {
                    +"videojs(document.getElementById('video_player'));"
                }
            }
        }
    }
}
//install(MultiPartFeature) { // настройка мультипарт обработки
//        // ...
//    }
//        get("file:///C:/Users/sento/Downloads/Main%20form1.html") {
//
//        }
//    routing {
//        get("/") {
//            // Отправить HTML с формой для загрузки
//        }
//        post("/upload") {
//            // Логика обработки загрузки файла в MinIO
//        }
//        get("/stream/{videoId}") {
//            // Логика стриминга видео из MinIO
//        }
//        // Статические файлы, в том числе HTML, JS для Video.js
//        static("/") {
//            resources("static")
//        }
//    }
/*
fun main(args: Array<String>) {
    try {
        // Создание объекта клиента MinioClient.
        val minioClient = MinioClient.builder()
            .endpoint("https://play.min.io")
            .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
            .build()

        // Объектно-ориентированный путь, например "mybucket/myobject.txt"
        val objectName = "myobject.txt"
        // Имя бакета
        val bucketName = "mybucket"

        // Скачивание объекта и сохранение его в файл.

        val targetFile = File("downloaded-$objectName")
        Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
        stream.close()
        println("$objectName has been downloaded successfully to $targetFile")

    } catch (e: MinioException) {
        println("Error occurred: ${e.message}")
        e.printStackTrace()
    }
}*/
/*
fun Application.module() {
    routing {
        get("/video/{ClimbHighTutorialVideo}") {
            val videoName = call.parameters["ClimbHighTutorialVideo"] ?: return@get call.respondText(
                "Missing or malformed video name",
                status = HttpStatusCode.BadRequest
            )

            // Бинарные данные видео
            val videoStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket("stream-service")
                    .`object`("ClimbHighTutorialVideo")
                    .build()
            )
            // Отправляем клиенту
            call.respondOutputStream(ContentType.Video.MP4) {
                videoStream.copyTo(this)
            }
        }
}
*/
