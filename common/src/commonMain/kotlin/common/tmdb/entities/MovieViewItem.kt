package common.tmdb.entities

data class MovieViewItem(
    val title: String,
    val overview: String,
    val releaseDate: String,
    val posterUrl: String?,
    val backdropUrl: String?
)