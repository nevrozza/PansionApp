package ktor

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.js.JsClient

internal actual class HttpEngineFactory {
    actual fun createEngine(): HttpClientEngineFactory<HttpClientEngineConfig> = JsClient()
}