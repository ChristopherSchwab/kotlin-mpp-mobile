package common.util

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.http.URLProtocol
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

/**
 * An interface expecting a function for executing a passed HTTP request and serializign the result as class T.
 */
interface HttpRequestSerializer {
    suspend fun <T> executeHttpRequest(
        deserializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol = URLProtocol.HTTPS,
        urlBuilderPort: Int = 443,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T
}

/**
 * An implementation executing the HTTP request with HttpClient and serializing the result with the Json class.
 */
class HttpClientHttpRequestSerializer(private val httpClient: HttpClient) : HttpRequestSerializer {

    constructor() : this(HttpClient())

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