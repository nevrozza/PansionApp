package com.nevrozq.pansion.plugins

import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.KotlinxSerializationConverter
import io.ktor.serialization.kotlinx.json.*
import io.ktor.serialization.kotlinx.serialization
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.StringFormat
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json()
//        contentType(ContentType.Application.Json)
//        accept(ContentType.Application.Json)
//        this.clearIgnoredTypes() //!
//        register(ContentType.Any, KotlinxSerializationConverter(
//            Json {
//                prettyPrint = true
//                isLenient = true
//                ignoreUnknownKeys = true
//            }
//        ))
    }

    routing {
        get("/json/kotlinx-serialization") {
            call.respond(mapOf("hello" to "world"))
        }
    }
}
