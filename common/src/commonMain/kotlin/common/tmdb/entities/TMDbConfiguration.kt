package common.tmdb.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class TMDbConfiguration {
    @Serializable
    data class Loaded(
        val images: TMDbConfigurationImages
    ) : TMDbConfiguration()

    object NotLoaded : TMDbConfiguration()
}

@Serializable
data class TMDbConfigurationImages(
    @SerialName("secure_base_url")
    val imagesBaseUrl: String,
    @SerialName("poster_sizes")
    val posterSizes: List<String>,
    @SerialName("backdrop_sizes")
    val backdropSizes: List<String>
)