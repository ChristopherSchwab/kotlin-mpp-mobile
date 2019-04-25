package common.tmdb

import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMoviePage
import common.tmdb.url.TMDbConfigurationPath
import common.tmdb.url.TMDbDiscoverPath
import common.util.ApplicationDispatcher
import common.util.HttpRequestSerializer
import kotlinx.coroutines.*

interface MovieListInteractorInputBoundary {
    fun loadCurrentMovies()
    fun loadNextPage()
}

class MovieListInteractor(
    private val movieListPresenter: MovieListInteractorOutputBoundary,
    private val httpRequestSerializer: HttpRequestSerializer,
    private val tmDbApiHost: String,
    tmDbApiKey: String
): MovieListInteractorInputBoundary {

    var page: Int = 0
        private set
    var nextPage: Int = 1
        private set
    var totalPages: Int = Int.MAX_VALUE
        private set

    private val configurationPathBuilder = TMDbConfigurationPath.TMDbConfigurationPathBuilder(tmDbApiKey)
    private val discoverPathBuilder = TMDbDiscoverPath.TMDbDiscoverPathBuilder(tmDbApiKey)

    private var tmDbConfiguration: TMDbConfiguration? = null

    override fun loadCurrentMovies() {
        page = 0
        nextPage = 1
        totalPages = Int.MAX_VALUE

        discoverPathBuilder
            .reset()
            .primaryReleaseDateGTE(DateTime.now() - 7.days)
            .primaryReleaseDateLTE(DateTime.now() + 1.days)
            .sortByPopularity()

        loadNextPage()
    }

    override fun loadNextPage() {
        if (nextPage in (page + 1)..totalPages) {
            page = nextPage

            GlobalScope.launch(ApplicationDispatcher) {
                val awaitTMDbMoviePage = async {
                    try {
                        httpRequestSerializer.executeHttpRequest(
                            deserializer = TMDbMoviePage.serializer(),
                            urlBuilderHost = tmDbApiHost,
                            urlBuilderEncodedPath = discoverPathBuilder.page(nextPage).build().path
                        )
                    } catch (e: Exception) {
                        movieListPresenter.presentException(e)
                        TMDbMoviePage(1, 0, 1, emptyList())
                    }
                }

                if (tmDbConfiguration == null) {
                    try {
                        httpRequestSerializer.executeHttpRequest(
                            deserializer = TMDbConfiguration.serializer(),
                            urlBuilderHost = tmDbApiHost,
                            urlBuilderEncodedPath = configurationPathBuilder.build().path
                        ).also { tmDbConfigurationLoaded ->
                            tmDbConfiguration = tmDbConfigurationLoaded
                            movieListPresenter.setConfiguration(tmDbConfigurationLoaded)
                        }
                    } catch (e: Exception) {
                        movieListPresenter.presentException(e)
                    }
                }

                awaitTMDbMoviePage.await().also { tmDbMoviePage ->
                    page = tmDbMoviePage.page
                    nextPage = page + 1
                    totalPages = tmDbMoviePage.totalPages

                    movieListPresenter.presentMovieList(tmDbMoviePage)
                }
            }
        }
    }
}

interface MovieListInteractorOutputBoundary {
    fun setConfiguration(tmDbConfiguration: TMDbConfiguration)
    fun presentMovieList(tmDbMoviePage: TMDbMoviePage)
    fun presentException(exception: Exception)
}