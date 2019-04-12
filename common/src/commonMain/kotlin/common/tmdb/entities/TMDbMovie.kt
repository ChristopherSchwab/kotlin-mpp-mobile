package common.tmdb.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbMovie(
    @SerialName("vote_count")
    val voteCount: Int,
    val id: Int,
    val video: Boolean,
    @SerialName("vote_average")
    val voteAverage: Double,
    val title: String,
    val popularity: Double,
    @SerialName("poster_path")
    val posterPath: String?,
    @SerialName("original_language")
    val originalLanguage: String,
    @SerialName("original_title")
    val originalTitle: String,
    @SerialName("backdrop_path")
    val backdropPath: String?,
    val adult: Boolean,
    val overview: String,
    @SerialName("release_date")
    val releaseDate: String
)
