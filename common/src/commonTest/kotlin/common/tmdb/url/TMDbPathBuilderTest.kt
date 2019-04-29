package common.tmdb.url

import common.util.DateFormat
import common.util.DateTime
import common.util.KlockDateFormat
import common.util.KlockDateTime
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TMDbPathBuilderTest {

    var urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

    lateinit var dateTime: DateTime
    lateinit var dateFormat: DateFormat

    @BeforeTest
    fun setup() {
        dateTime = KlockDateTime().now()
        dateFormat = KlockDateFormat("yyyy-MM-dd")
    }

    @Test
    fun `Build configuration path is a valid url path`() {
        val testTMDbConfigurationPathBuilder = TMDbConfigurationPath.TMDbConfigurationPathBuilder("apiKey")

        assertTrue(urlRegex.containsMatchIn("http://example.com/".plus(testTMDbConfigurationPathBuilder.build().path)))
    }

    @Test
    fun `Build discover path is a valid url path`() {
        val testTMDbDiscoverPathBuilder = TMDbDiscoverPath.TMDbDiscoverPathBuilder(dateFormat, "apiKey")
            .reset()
            .primaryReleaseDateGTE(dateTime)
            .primaryReleaseDateLTE(dateTime.plus(1))
            .sortByPopularity()
            .page(1)

        assertTrue(urlRegex.containsMatchIn("http://example.com/".plus(testTMDbDiscoverPathBuilder.build().path)))
    }
}