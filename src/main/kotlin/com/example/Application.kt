package com.example

import com.example.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.minio.MinioClient

val minioClient: MinioClient
    get() = MinioClient.builder()
        .endpoint("http://localhost:9000")
        .credentials("senton", "89314951rrsad/")
        .build()

fun main() {
    embeddedServer(Netty, port = 8080,  module = Application::module).start(wait = true)
}

fun Application.module() {
    configureRouting()
}
