package common.tmdb

import common.tmdb.entities.MovieViewItem

/**
 * Defines what the view of the movie list use case should be able to show.
 */
interface MovieListView {
    fun showMovieViewItems(movieViewItems: List<MovieViewItem>)
    fun showError(errorMessage: String)
}