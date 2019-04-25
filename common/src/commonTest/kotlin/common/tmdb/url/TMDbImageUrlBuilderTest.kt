package common.tmdb.url

import common.tmdb.entities.TMDbConfiguration
import common.tmdb.entities.TMDbConfigurationImages
import common.tmdb.entities.TMDbMovie
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TMDbImageUrlBuilderTest {

    var urlRegex = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]".toRegex()

    lateinit var testTMDbImageUrlBuilder: TMDbImageUrlBuilder
    lateinit var testTMDbConfiguration: TMDbConfiguration
    lateinit var testTMDbMovie: TMDbMovie

    @BeforeTest
    fun setup() {
        testTMDbImageUrlBuilder = TMDbImageUrlBuilder()

        testTMDbConfiguration = TMDbConfiguration(
            TMDbConfigurationImages(
                "http://example.com/",
                listOf("w100", "w240"),
                listOf("w160", "w320")
            )
        )

        testTMDbMovie = TMDbMovie(
            0,
            1,
            false,
            0.0,
            "title",
            0.0,
            "some/image.png",
            "originalLanguage",
            "originalTitle",
            "some/other/image.jpg",
            false,
            "overview",
            "1970-01-01"
        )
    }

    @Test
    fun `Build poster image url is a valid url`() {
        val posterUrl = TMDbImageUrlBuilder().buildPosterUrl(testTMDbConfiguration, testTMDbMovie)

        assertNotNull(posterUrl)
        assertTrue { urlRegex.containsMatchIn(posterUrl) }
    }

    @Test
    fun `Build backdrop image url is a valid url`() {
        val backdropUrl = TMDbImageUrlBuilder().buildBackdropUrl(testTMDbConfiguration, testTMDbMovie)

        assertNotNull(backdropUrl)
        assertTrue { urlRegex.containsMatchIn(backdropUrl) }
    }
}