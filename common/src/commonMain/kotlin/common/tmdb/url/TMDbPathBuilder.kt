package common.tmdb.url

import common.util.Date
import common.util.Timestamp

abstract class TMDbPath internal constructor(private val builder: TMDbPathBuilder<out TMDbPath>) {

    abstract val pathBase: String
    val path: String
        get() = pathBase.plus(
            builder.parameters.map { parameter -> "&${parameter.key}=${parameter.value}" }.joinToString(separator = "")
        )

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

class TMDbConfigurationPath private constructor(builder: TMDbConfigurationPathBuilder) : TMDbPath(builder) {

    override val pathBase: String
        get() = "3/configuration?"

    class TMDbConfigurationPathBuilder(apiKey: String) : TMDbPathBuilder<TMDbConfigurationPath>(apiKey) {

        override fun build() = TMDbConfigurationPath(this)
    }
}

class TMDbDiscoverPath private constructor(builder: TMDbDiscoverPathBuilder) : TMDbPath(builder) {

    override val pathBase: String
        get() = "3/discover/movie?"

    class TMDbDiscoverPathBuilder(apiKey: String) : TMDbPathBuilder<TMDbDiscoverPath>(apiKey) {

        fun reset() = apply { super.resetParameters() }

        fun primaryReleaseDateGTE(primaryReleaseDateGTE: Timestamp) = apply {
            parameters["primary_release_date.gte"] = Date.format(primaryReleaseDateGTE, "yyyy-MM-dd")
        }

        fun primaryReleaseDateLTE(primaryReleaseDateLTE: Timestamp) = apply {
            parameters["primary_release_date.lte"] = Date.format(primaryReleaseDateLTE, "yyyy-MM-dd")
        }

        fun sortByPopularity() = apply {
            parameters["sort_by"] = "popularity.desc"
        }

        fun sortByRevenue() = apply {
            parameters["sort_by"] = "revenue.desc"
        }

        fun page(page: Int) = apply {
            parameters["page"] = page.toString()
        }

        override fun build() = TMDbDiscoverPath(this)
    }
}