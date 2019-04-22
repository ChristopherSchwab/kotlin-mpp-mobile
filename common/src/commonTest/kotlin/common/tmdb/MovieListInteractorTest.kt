package common.tmdb

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbConfigurationImages
import common.tmdb.entities.TMDbMoviePage
import common.util.HttpRequestSerializer
import common.util.runBlocking
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

    lateinit var testMovieListPresenter: MovieListInteractorOutputBoundary

    lateinit var testTMDbConfiguration: TMDbConfiguration.Loaded
    lateinit var testTMDbConfigurationJson: String

    lateinit var testTMDbMoviePage: TMDbMoviePage
    lateinit var testTMDbMoviePageJson: String

    lateinit var testHttpRequestSerializer: HttpRequestSerializer

    @BeforeTest
    fun setup() {
        testMovieListPresenter = mockk(relaxed = true)

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
                TMDbConfiguration.Loaded.serializer() to testTMDbConfigurationJson,
                TMDbMoviePage.serializer() to testTMDbMoviePageJson
            )
        )
    }

    @Test
    fun `Presenter configuration is set before presentMovieList is called`() {
        runBlocking {
            MovieListInteractor(
                testMovieListPresenter,
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                it.loadNextPage()

                delay(500)
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
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                val firstCall = async {
                    it.loadNextPage()
                }

                val secondCall = async {
                    it.loadNextPage()
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
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            ).also {
                it.loadNextPage()

                delay(500)
            }
        }

        runBlocking {
            testMovieListInteractor.loadNextPage()

            delay(500)
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
                testHttpRequestSerializer,
                "apiHost",
                "apiKey"
            )).also {
                it.loadCurrentMovies()

                delay(500)
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

