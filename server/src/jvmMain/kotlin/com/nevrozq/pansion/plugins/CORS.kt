package com.nevrozq.pansion.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureCORS() {
    install(CORS) {
//        HttpMethod.DefaultMethods.forEach() {
//            allowMethod(it)
//        }
//        allowHeader("any header")
//
//        allowHeader(HttpHeaders.ContentType)
////        allowHeader(HttpHeaders.)
//        allowHeader("key")
//        allowCredentials = true
//        allowHost("*", listOf("http", "https"))
//        allowSameOrigin = true

        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader("Bearer-Authorization")
        allowHeader("Vzlom")
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        anyHost()
    }
}