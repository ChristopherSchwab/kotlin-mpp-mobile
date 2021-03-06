package common.tmdb

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMoviePage
import common.tmdb.url.TMDbConfigurationPath
import common.tmdb.url.TMDbDiscoverPath
import common.util.ApplicationDispatcher
import common.util.DateFormat
import common.util.HttpRequestSerializer
import kotlinx.coroutines.*

/**
 * The input boundary of the interactor.
 * Defines what the interactor is capable of doing.
 */
interface MovieListInteractorInputBoundary {
    fun loadCurrentMovies()
    fun loadNextPage()
}

/**
 * The interactor of the movie list use case.
 * Loads current movies or the movies for a next page and sends them to the presenter.
 */
class MovieListInteractor(
    private val movieListPresenter: MovieListInteractorOutputBoundary,
    private val dateTimeNow: common.util.DateTime,
    tmDbDateFormat: DateFormat,
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
    private val discoverPathBuilder = TMDbDiscoverPath.TMDbDiscoverPathBuilder(tmDbDateFormat, tmDbApiKey)

    private var tmDbConfiguration: TMDbConfiguration? = null

    override fun loadCurrentMovies() {
        page = 0
        nextPage = 1
        totalPages = Int.MAX_VALUE

        discoverPathBuilder
            .reset()
            .primaryReleaseDateGTE(dateTimeNow.plus(-7))
            .primaryReleaseDateLTE(dateTimeNow.plus(1))
            .sortByPopularity()

        loadNextPage()
    }

    override fun loadNextPage() {
        GlobalScope.launch(ApplicationDispatcher) { nextPage() }
    }

    internal suspend fun nextPage() {
        if (nextPage in (page + 1)..totalPages) {
            page = nextPage

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

            try {
                httpRequestSerializer.executeHttpRequest(
                    deserializer = TMDbMoviePage.serializer(),
                    urlBuilderHost = tmDbApiHost,
                    urlBuilderEncodedPath = discoverPathBuilder.page(nextPage).build().path
                ).also { tmDbMoviePage ->
                    page = tmDbMoviePage.page
                    nextPage = page + 1
                    totalPages = tmDbMoviePage.totalPages

                    movieListPresenter.presentMovieList(tmDbMoviePage)
                }
            } catch (e: Exception) {
                movieListPresenter.presentException(e)
            }
        }
    }
}

/**
 * The output boundary of the interactor.
 * Defines what the presenter should be able to receive.
 */
interface MovieListInteractorOutputBoundary {
    fun setConfiguration(tmDbConfiguration: TMDbConfiguration)
    fun presentMovieList(tmDbMoviePage: TMDbMoviePage)
    fun presentException(exception: Exception)
}