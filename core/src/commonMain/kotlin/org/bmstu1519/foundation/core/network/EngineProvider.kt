package org.bmstu1519.foundation.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

interface EngineProvider {
    fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient
}

expect fun getEngine(): EngineProvider
