package common.tmdb

import common.tmdb.entities.MovieViewItem
import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMoviePage
import common.tmdb.url.TMDbImageUrlBuilder
import common.util.DateFormat
import kotlinx.serialization.json.JsonException

/**
 * The presenter of the movie list use case.
 * Receives data and processes it for showing back to the user by sending it to the view.
 */
class MovieListPresenter(
    private val view: MovieListView,
    private val tmDbDateFormat: DateFormat,
    private val movieViewItemDateFormat: DateFormat
): MovieListInteractorOutputBoundary {

    private val tmDbImageUrlBuilder: TMDbImageUrlBuilder = TMDbImageUrlBuilder()
    private var tmDbConfiguration: TMDbConfiguration? = null

    override fun setConfiguration(tmDbConfiguration: TMDbConfiguration) {
        this.tmDbConfiguration = tmDbConfiguration
    }

    override fun presentMovieList(tmDbMoviePage: TMDbMoviePage) {
        tmDbMoviePage.results.map { tmDbMovie ->
            MovieViewItem(
                tmDbMovie.title,
                tmDbMovie.overview,
                try {
                    movieViewItemDateFormat.format(tmDbDateFormat.parse(tmDbMovie.releaseDate))
                } catch (e: IllegalArgumentException) {
                    "No date provided"
                },
                tmDbImageUrlBuilder.buildPosterUrl(tmDbConfiguration, tmDbMovie),
                tmDbImageUrlBuilder.buildBackdropUrl(tmDbConfiguration, tmDbMovie)
            )
        }.also {
            view.showMovieViewItems(it)
        }
    }

    override fun presentException(exception: Exception) {
        when (exception) {
            is JsonException -> view.showError("a")
            is IllegalArgumentException -> view.showError("b")
            else -> view.showError("c")
        }
    }
}