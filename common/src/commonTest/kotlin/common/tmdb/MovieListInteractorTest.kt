package common.tmdb

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbConfigurationImages
import common.tmdb.entities.TMDbMoviePage
import common.util.*
import io.ktor.http.URLProtocol
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test

class MovieListInteractorTest {

    lateinit var dateTime: DateTime
    lateinit var dateFormat: DateFormat

    lateinit var testMovieListPresenter: MovieListInteractorOutputBoundary

    lateinit var testTMDbConfiguration: TMDbConfiguration
    lateinit var testTMDbConfigurationJson: String

    lateinit var testTMDbMoviePage: TMDbMoviePage
    lateinit var testTMDbMoviePageJson: String

    lateinit var testHttpRequestSerializer: HttpRequestSerializer

    @BeforeTest
    fun setup() {
        dateTime = KlockDateTime().now()
        dateFormat = KlockDateFormat("yyyy-MM-dd")

        testMovieListPresenter = mockk(relaxed = true)

        testTMDbConfiguration = TMDbConfiguration(
            TMDbConfigurationImages(
                "http://example.com",
                listOf("w100"),
                listOf("w200")
            )
        )

        testTMDbConfigurationJson = Json.stringify(
            TMDbConfiguration.serializer(),
            testTMDbConfiguration
        )

        testTMDbMoviePage = TMDbMoviePage(
            1,
            0,
            1,
            results = emptyList()
        )

        testTMDbMoviePageJson = Json.stringify(
            TMDbMoviePage.serializer(),
            testTMDbMoviePage
        )

        testHttpRequestSerializer = TestHttpRequestSerializer(
            mapOf(
                TMDbConfiguration.serializer() to testTMDbConfigurationJson,
                TMDbMoviePage.serializer() to testTMDbMoviePageJson
            )
        )
    }

    @Test
    fun `Presenter configuration is set before presentMovieList is called`() {
        runBlocking(ApplicationDispatcher) {
            MovieListInteractor(
                testMovieListPresenter,
                dateTime,
                dateFormat,
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                it.nextPage()
            }
        }

        verifySequence {
            testMovieListPresenter.setConfiguration(testTMDbConfiguration)
            testMovieListPresenter.presentMovieList(testTMDbMoviePage)
        }
    }

    @Test
    fun `Calling loadNextPage while still waiting for a response does not cause another request`() {
        runBlocking {
            MovieListInteractor(
                testMovieListPresenter,
                dateTime,
                dateFormat,
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                val firstCall = async {
                    it.nextPage()
                }

                val secondCall = async {
                    it.nextPage()
                }

                firstCall.await()
                secondCall.await()
            }
        }

        verify(exactly = 1) {
            testMovieListPresenter.presentMovieList(testTMDbMoviePage)
        }
    }

    @Test
    fun `Calling loadNextPage does not execute a request if page is greater or equal to totalPages`() {
        val testMovieListInteractor = runBlocking {
            MovieListInteractor(
                testMovieListPresenter,
                dateTime,
                dateFormat,
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                it.nextPage()
            }
        }

        runBlocking {
            testMovieListInteractor.nextPage()
        }

        verify(exactly = 1) {
            testMovieListPresenter.presentMovieList(testTMDbMoviePage)
        }
    }

    @Test
    fun `Calling loadCurrentMovies calls loadNextPage`() {
        val testMovieListInteractor = runBlocking {
            spyk(MovieListInteractor(
                testMovieListPresenter,
                dateTime,
                dateFormat,
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            )).also {
                it.loadCurrentMovies()
            }
        }

        verify(exactly = 1) {
            testMovieListInteractor.loadNextPage()
        }
    }
}

class TestHttpRequestSerializer(private val jsons: Map<KSerializer<*>, String>) : HttpRequestSerializer {
    override suspend fun <T> executeHttpRequest(
        deserializer: KSerializer<T>,
        urlBuilderProtocol: URLProtocol,
        urlBuilderPort: Int,
        urlBuilderHost: String,
        urlBuilderEncodedPath: String
    ): T {
        delay(250)
        Json.parse(deserializer, jsons[deserializer]!!).also { return it }
    }
}

