package ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual class HttpEngineFactory actual constructor() {
    actual fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = OkHttp
}