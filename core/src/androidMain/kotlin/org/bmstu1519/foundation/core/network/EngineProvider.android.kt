package org.bmstu1519.foundation.core.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttp

object EngineProviderAndroid : EngineProvider {
    override fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
        return HttpClient(OkHttp) {
            block()
        }
    }
}

actual fun getEngine(): EngineProvider = EngineProviderAndroid
