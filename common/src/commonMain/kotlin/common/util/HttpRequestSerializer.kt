package common.util

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface HttpRequestSerializer {
    suspend fun <T> executeHttpRequest(
        serializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol = URLProtocol.HTTPS,
        urlBuilderPort: Int = 443,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T
}

class HttpClientHttpRequestSerializer : HttpRequestSerializer {

    private val httpClient = HttpClient()

    override suspend fun <T> executeHttpRequest(
        serializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol,
        urlBuilderPort: Int,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T {
        httpClient.get<String> {
            url {
                protocol = urlBuilderProtocol
                port = urlBuilderPort
                host = urlBuilderHost
                encodedPath = urlBuilderEncodedPath
            }
        }.also { httpResponseString ->
            Json(strictMode = false).parse(
                serializer,
                httpResponseString
            ).also { httpResponse ->
                return httpResponse
            }
        }
    }
}