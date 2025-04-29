package com.nevrozq.pansion.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.cors.routing.CORS

fun Application.configureCORS() {
    install(CORS) {
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader("Bearer-Authorization")
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        anyHost()
    }
}