package common.util

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

interface HttpRequestSerializer {
    suspend fun <T> executeHttpRequest(
        deserializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol = URLProtocol.HTTPS,
        urlBuilderPort: Int = 443,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T
}

class HttpClientHttpRequestSerializer(private val httpClient: HttpClient = HttpClient()) : HttpRequestSerializer {

    private val json = Json(strictMode = false)

    override suspend fun <T> executeHttpRequest(
        deserializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol,
        urlBuilderPort: Int,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T {
        httpClient.get<String> {
            url {
                protocol = urlBuilderProtocol
                port = urlBuilderPort
                host = urlBuilderHost   //IllegalArgumentException
                encodedPath = urlBuilderEncodedPath
            }
        }.also { httpResponseJson ->
            return json.parse(deserializer, httpResponseJson) //JsonException
        }
    }
}