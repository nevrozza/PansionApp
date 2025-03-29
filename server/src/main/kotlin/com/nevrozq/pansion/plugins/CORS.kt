package com.nevrozq.pansion.plugins

import com.nevrozq.pansion.https_port
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.content.OutgoingContent
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureCORS() {
    install(CORS) {
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        allowHeader("Bearer-Authorization")
        allowHeader("Vzlom")
        allowNonSimpleContentTypes = true
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Options)
        anyHost()
    }
}