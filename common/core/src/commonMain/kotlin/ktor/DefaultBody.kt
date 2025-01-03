package ktor

import checkOnNoOk
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun HttpClient.dPost(
    p: String,
    body: Any? = null,
    isBearer: Boolean = true,
    block: HttpRequestBuilder.() -> Unit = {}
) : HttpResponse {
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

suspend fun HttpResponse.check() : Boolean {
    if (this.status != HttpStatusCode.OK) {
        throw Throwable(this.bodyAsText())
    }
    return true
}

