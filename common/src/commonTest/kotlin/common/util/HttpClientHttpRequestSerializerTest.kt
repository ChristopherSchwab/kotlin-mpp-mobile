package common.util

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbConfigurationImages
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockHttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.io.ByteReadChannel
import kotlinx.io.charsets.Charsets
import kotlinx.io.core.toByteArray
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HttpClientHttpRequestSerializerTest {

    lateinit var testTMDbConfiguration: TMDbConfiguration.Loaded
    lateinit var testTMDbConfigurationJson: String

    @BeforeTest
    fun setup() {
        testTMDbConfiguration = TMDbConfiguration.Loaded(
            TMDbConfigurationImages(
                "http://example.com",
                listOf("w100"),
                listOf("w200")
            )
        )

        testTMDbConfigurationJson = Json.stringify(
            TMDbConfiguration.Loaded.serializer(),
            testTMDbConfiguration
        )
    }

    @Test
    fun `Executing a (mocked) HttpRequest returns a deserialized object`() {
        val testMockEngine = MockEngine {
            MockHttpResponse(
                call,
                HttpStatusCode.OK,
                ByteReadChannel(testTMDbConfigurationJson.toByteArray(Charsets.UTF_8)),
                headersOf("Content-Type" to listOf(ContentType.Text.Plain.toString()))
            )
        }

        val testHttpClientHttpRequestSerializer = HttpClientHttpRequestSerializer(HttpClient(testMockEngine))

        val tmDbConfiguration = runBlocking {
            testHttpClientHttpRequestSerializer.executeHttpRequest(
                TMDbConfiguration.Loaded.serializer(),
                urlBuilderHost = "example.com",
                urlBuilderEncodedPath = "some/path"
            )
        }

        assertEquals(testTMDbConfiguration, tmDbConfiguration)
    }
}