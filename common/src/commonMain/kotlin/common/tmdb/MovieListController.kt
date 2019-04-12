package common.tmdb

class MovieListController(private val movieListInteractor: MovieListInteractorInputBoundary) {

    fun onCreate() {
        movieListInteractor.loadCurrentMovies()
    }

    fun onPageEnd() {
        movieListInteractor.loadNextPage()
    }
}