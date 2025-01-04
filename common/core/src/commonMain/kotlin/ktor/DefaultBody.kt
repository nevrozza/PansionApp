package ktor

import checkOnNoOk
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.reflect.*

suspend fun HttpClient.dPost(
    p: String,
    body: Any? = null,
    isBearer: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
): HttpResponse {
    return this.post {
        url {
            contentType(ContentType.Application.Json)
            bearer(isBearer)
            path(p)
            if (body != null) setBody(body)
            block()
        }
    }
}


public suspend inline fun <reified T> HttpResponse.dBody(): T {
    call.response.check()
    return call.bodyNullable(typeInfo<T>()) as T
}

suspend fun HttpResponse.check(): Boolean {
    if (this.status != HttpStatusCode.OK) {
        throw Throwable("${this.status.value} ${this.call.request.url.encodedPath.removeSuffix("/").removePrefix("/")}\n${this.bodyAsText()}")
    }
    return true
}

