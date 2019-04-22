package common.tmdb

import com.soywiz.klock.DateException
import com.soywiz.klock.DateFormat
import com.soywiz.klock.parse
import common.tmdb.entities.MovieViewItem
import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMoviePage
import common.tmdb.url.TMDbImageUrlBuilder
import kotlinx.serialization.json.JsonException

class MovieListPresenter(private val view: MovieListView): MovieListInteractorOutputBoundary {

    private val tmDbImageUrlBuilder: TMDbImageUrlBuilder = TMDbImageUrlBuilder()
    private var tmDbConfiguration: TMDbConfiguration = TMDbConfiguration.NotLoaded

    private val tmDbDateFormat = DateFormat("yyyy-MM-dd")
    private val movieViewItemDateFormat = DateFormat("MMMM d, yyyy")

    override fun setConfiguration(tmDbConfiguration: TMDbConfiguration) {
        this.tmDbConfiguration = tmDbConfiguration
    }

    override fun presentMovieList(tmDbMoviePage: TMDbMoviePage) {
        tmDbMoviePage.results.map { tmDbMovie ->
            MovieViewItem(
                tmDbMovie.title,
                tmDbMovie.overview,
                try {
                    tmDbDateFormat.parse(tmDbMovie.releaseDate).format(movieViewItemDateFormat)
                } catch (e: DateException) {
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