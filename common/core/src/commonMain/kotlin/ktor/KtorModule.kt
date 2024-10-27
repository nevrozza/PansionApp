package ktor

import AuthRepository
import RequestPaths
import deviceType
import di.Inject
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMessageBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton
import server.DeviceTypex

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
//                    protocol = if (deviceType != DeviceTypex.web) URLProtocol.HTTP else URLProtocol.HTTPS
                    protocol = URLProtocol.HTTP
                    host = RequestPaths.ip //127.0.0.1:8081 //192.168.137.1
                }
            }
        }
    }
}

val vzlomList = listOf<String>(
    "Tebe ne stidno?",
    "Samiy umniy?",
    "I see you.",
    "Ono togo stoilo?",
    "Kuda smotrim?"
)

fun HttpMessageBuilder.bearer() {
    val token = Inject.instance<AuthRepository>().fetchToken()
    val vzlom = vzlomList.random()
    header("Vzlom", vzlom)
    header("Bearer-Authorization", token)
}

