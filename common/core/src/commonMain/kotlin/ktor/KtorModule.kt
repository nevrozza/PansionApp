package ktor

import AuthRepository
import RequestPaths
import deviceSupport.DeviceTypex
import deviceSupport.deviceType
import di.Inject
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import isTestMode
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

internal val ktorModule = DI.Module("ktorModule") {
    bind<HttpClient>() with singleton {
        HttpClient(HttpEngineFactory().createEngine()) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(DefaultRequest)

            install(ContentNegotiation) {
                json(Json {
                    allowSpecialFloatingPointValues = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    prettyPrint = true
                })
            }

            install(HttpTimeout) {
                connectTimeoutMillis = 15000
                requestTimeoutMillis = 30000
            }

            defaultRequest {
                header("Content-Type", "application/json; charset=UTF-8")
//                contentType(ContentType.Application.Json)
//                accept(ContentType.Application.Json)
//                header("Access-Control-Allow-Origin", true)
                url {
                    protocol = if (deviceType != DeviceTypex.WEB || isTestMode) URLProtocol.HTTP else URLProtocol.HTTPS
//                    protocol = URLProtocol.HTTP
                    host = RequestPaths.ip //127.0.0.1:8081 //192.168.137.1
                }
            }
        }
    }
}


fun HttpMessageBuilder.bearer(isActive: Boolean = true) {
    if (isActive) {
        val token = Inject.instance<AuthRepository>().fetchToken()
        header("Bearer-Authorization", token)
    }
}

