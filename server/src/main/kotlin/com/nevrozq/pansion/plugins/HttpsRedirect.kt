package com.nevrozq.pansion.plugins

import com.nevrozq.pansion.https_port
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.httpsredirect.HttpsRedirect
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing



fun Application.configureHttpsRedirect() {
    install(HttpsRedirect) {
        sslPort = https_port
        this.permanentRedirect = true
    }
}