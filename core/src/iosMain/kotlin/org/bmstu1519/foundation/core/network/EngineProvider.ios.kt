package org.bmstu1519.foundation.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.Darwin

object EngineProviderIOS : EngineProvider {
    override fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(Darwin) {
            block()
        }
    }
}

actual fun getEngine(): EngineProvider = EngineProviderIOS
