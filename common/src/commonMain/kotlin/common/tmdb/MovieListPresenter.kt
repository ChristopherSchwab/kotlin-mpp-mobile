package common.tmdb

import common.tmdb.entities.MovieViewItem
import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMoviePage
import common.tmdb.url.TMDbImageUrlBuilder
import common.util.Date

class MovieListPresenter(private val view: MovieListView): MovieListInteractorOutputBoundary {

    private val tmDbImageUrlBuilder: TMDbImageUrlBuilder = TMDbImageUrlBuilder()
    private var tmDbConfiguration: TMDbConfiguration = TMDbConfiguration.NotLoaded

    override fun setConfiguration(tmDbConfiguration: TMDbConfiguration) {
        this.tmDbConfiguration = tmDbConfiguration
    }

    override fun presentMovieList(tmDbMoviePage: TMDbMoviePage) {
        tmDbMoviePage.results.map { tmDbMovie ->
            MovieViewItem(
                tmDbMovie.title,
                tmDbMovie.overview,
                Date.format(Date.parse(tmDbMovie.releaseDate, "yyyy-MM-dd"), "MMMM d, yyyy"),
                tmDbImageUrlBuilder.buildPosterUrl(tmDbConfiguration, tmDbMovie),
                tmDbImageUrlBuilder.buildBackdropUrl(tmDbConfiguration, tmDbMovie)
            )
        }.also {
            view.showMovieViewItems(it)
        }
    }

    override fun presentException(exception: Exception) {
        view.showError()
    }
}