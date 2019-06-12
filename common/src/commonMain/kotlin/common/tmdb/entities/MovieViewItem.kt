package common.tmdb.entities

/**
 * A data class holding information about a movie that are necessary for presenting it.
 */
data class MovieViewItem(
    val title: String,
    val overview: String,
    val releaseDate: String,
    val posterUrl: String?,
    val backdropUrl: String?
)