package common.tmdb.url

import com.soywiz.klock.DateTime
import com.soywiz.klock.days
import kotlin.test.Test
import kotlin.test.assertTrue

class TMDbPathBuilderTest {

    var urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

    @Test
    fun `Build configuration path is a valid url path`() {
        val testTMDbConfigurationPathBuilder = TMDbConfigurationPath.TMDbConfigurationPathBuilder("apiKey")

        assertTrue(urlRegex.containsMatchIn("http://example.com/".plus(testTMDbConfigurationPathBuilder.build().path)))
    }

    @Test
    fun `Build discover path is a valid url path`() {
        val testTMDbDiscoverPathBuilder = TMDbDiscoverPath.TMDbDiscoverPathBuilder("apiKey")
            .reset()
            .primaryReleaseDateGTE(DateTime.now())
            .primaryReleaseDateLTE(DateTime.now() + 1.days)
            .sortByPopularity()
            .page(1)

        assertTrue(urlRegex.containsMatchIn("http://example.com/".plus(testTMDbDiscoverPathBuilder.build().path)))
    }
}