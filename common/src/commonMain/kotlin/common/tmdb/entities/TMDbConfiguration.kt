package common.tmdb.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A data class representing the response of the configuration-endpoint of the TMDB API.
 */
@Serializable
data class TMDbConfiguration(
    val images: TMDbConfigurationImages
)

/**
 * A data class representing a part of response of the configuration-endpoint of the TMDB API.
 */
@Serializable
data class TMDbConfigurationImages(
    @SerialName("secure_base_url")
    val imagesBaseUrl: String,
    @SerialName("poster_sizes")
    val posterSizes: List<String>,
    @SerialName("backdrop_sizes")
    val backdropSizes: List<String>
)