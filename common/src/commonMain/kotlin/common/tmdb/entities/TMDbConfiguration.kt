package common.tmdb.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TMDbConfiguration(
    val images: TMDbConfigurationImages
)

@Serializable
data class TMDbConfigurationImages(
    @SerialName("secure_base_url")
    val imagesBaseUrl: String,
    @SerialName("poster_sizes")
    val posterSizes: List<String>,
    @SerialName("backdrop_sizes")
    val backdropSizes: List<String>
)