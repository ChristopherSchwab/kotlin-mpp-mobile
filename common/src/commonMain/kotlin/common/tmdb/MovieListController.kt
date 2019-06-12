package common.tmdb

/**
 * The controller for the movie list use case.
 * Translates user actions or states of the app into calls to the interactor.
 */
class MovieListController(private val movieListInteractor: MovieListInteractorInputBoundary) {

    fun onCreate() {
        movieListInteractor.loadCurrentMovies()
    }

    fun onPageEnd() {
        movieListInteractor.loadNextPage()
    }
}