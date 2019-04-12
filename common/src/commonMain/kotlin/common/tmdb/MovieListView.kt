package common.tmdb

import common.tmdb.entities.MovieViewItem

interface MovieListView {
    fun showMovieViewItems(movieViewItems: List<MovieViewItem>)
    fun showError()
}