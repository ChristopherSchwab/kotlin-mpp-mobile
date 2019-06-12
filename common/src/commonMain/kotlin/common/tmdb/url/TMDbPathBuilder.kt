package common.tmdb.url

import common.util.DateTime

/**
 * An abstract path of the TMDB API.
 */
abstract class TMDbPath internal constructor(private val builder: TMDbPathBuilder<out TMDbPath>) {

    abstract val pathBase: String

    val path: String
        get() = pathBase.plus(builder.parameters.map { parameter -> "&${parameter.key}=${parameter.value}" }.joinToString(separator = ""))

    /**
     * An abstract builder for creating a path of the TMDB API.
     */
    abstract class TMDbPathBuilder<T : TMDbPath>(private val apiKey: String) {

        internal val parameters: MutableMap<String, String> = mutableMapOf()

        init {
            apiKey(apiKey)
        }

        private fun apiKey(apiKey: String) = apply {
            parameters["api_key"] = apiKey
        }

        internal fun resetParameters() = apply {
            parameters.clear()
            apiKey(apiKey)
        }

        abstract fun build() : T
    }
}

/**
 * A configuration path of the TMDB API for accessing the configuration-endpoint.
 */
class TMDbConfigurationPath private constructor(builder: TMDbConfigurationPathBuilder) : TMDbPath(builder) {

    override val pathBase: String
        get() = "3/configuration?"

    /**
     * A builder for creating a configuration path of the TMDB API
     */
    class TMDbConfigurationPathBuilder(apiKey: String) : TMDbPathBuilder<TMDbConfigurationPath>(apiKey) {

        override fun build() = TMDbConfigurationPath(this)
    }
}

/**
 * A discover path of the TMDB API for accessing the movies-endpoint.
 */
class TMDbDiscoverPath private constructor(builder: TMDbDiscoverPathBuilder) : TMDbPath(builder) {

    override val pathBase: String
        get() = "3/discover/movie?"

    /**
     * A builder for creating a discover/movies path of the TMDB API
     */
    class TMDbDiscoverPathBuilder(
        private val tmDbDateFormat: common.util.DateFormat,
        apiKey: String
    ) : TMDbPathBuilder<TMDbDiscoverPath>(apiKey) {

        //private val tmDbDateFormat = DateFormat("yyyy-MM-dd")

        fun reset() = apply { super.resetParameters() }

        fun primaryReleaseDateGTE(primaryReleaseDateGTE: DateTime) = apply {
            parameters["primary_release_date.gte"] = tmDbDateFormat.format(primaryReleaseDateGTE)
        }

        fun primaryReleaseDateLTE(primaryReleaseDateLTE: DateTime) = apply {
            parameters["primary_release_date.lte"] = tmDbDateFormat.format(primaryReleaseDateLTE)
        }

        fun sortByPopularity() = apply {
            parameters["sort_by"] = "popularity.desc"
        }

        fun page(page: Int) = apply {
            parameters["page"] = page.toString()
        }

        override fun build() = TMDbDiscoverPath(this)
    }
}