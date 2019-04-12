package common.tmdb.url

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbMovie

class TMDbImageUrlBuilder {
    fun buildPosterUrl(tmDbConfiguration: TMDbConfiguration, tmDbMovie: TMDbMovie): String? {
        return if (tmDbMovie.posterPath == null) null
        else {
           when (tmDbConfiguration) {
               is TMDbConfiguration.Loaded -> "${tmDbConfiguration.images.imagesBaseUrl}${tmDbConfiguration.images.posterSizes[tmDbConfiguration.images.posterSizes.size/2]}${tmDbMovie.posterPath}"
               is TMDbConfiguration.NotLoaded -> null
           }
        }
    }

    fun buildBackdropUrl(tmDbConfiguration: TMDbConfiguration, tmDbMovie: TMDbMovie): String? {
        return if (tmDbMovie.backdropPath == null) null
        else {
            when (tmDbConfiguration) {
                is TMDbConfiguration.Loaded -> "${tmDbConfiguration.images.imagesBaseUrl}${tmDbConfiguration.images.backdropSizes[tmDbConfiguration.images.backdropSizes.size/2]}${tmDbMovie.backdropPath}"
                is TMDbConfiguration.NotLoaded -> null
            }
        }
    }
}