package common.tmdb.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class representing a movie page returned by the movies-endpoint of the TMDB API.
 */
@Serializable
data class TMDbMoviePage(
    val page: Int,
    @SerialName("total_results")
    val totalResults: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    val results: List<TMDbMovie>
)