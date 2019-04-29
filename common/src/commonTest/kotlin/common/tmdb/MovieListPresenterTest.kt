package common.tmdb

import common.tmdb.entities.MovieViewItem
import common.tmdb.entities.TMDbMovie
import common.tmdb.entities.TMDbMoviePage
import common.util.DateFormat
import common.util.KlockDateFormat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class MovieListPresenterTest {

    lateinit var tmDbDateFormat: DateFormat
    lateinit var movieViewItemDateFormat: DateFormat
    lateinit var showMovieViewItemsList: List<MovieViewItem>
    lateinit var testMovieListView: MovieListView
    lateinit var testMovieListPresenter: MovieListPresenter
    lateinit var testTMDbMoviePage: TMDbMoviePage

    @BeforeTest
    fun setup() {
        tmDbDateFormat = KlockDateFormat("yyyy-MM-dd")
        movieViewItemDateFormat = KlockDateFormat("MMMM d, yyyy")

        testMovieListView = mockk(relaxed = true)
        every { testMovieListView.showMovieViewItems(movieViewItems = any()) } answers { showMovieViewItemsList = firstArg()}
        testMovieListPresenter = MovieListPresenter(testMovieListView, tmDbDateFormat, movieViewItemDateFormat)

        testTMDbMoviePage = TMDbMoviePage(
            1,
            1,
            1,
            listOf(
                TMDbMovie(
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
            )
        )
    }

    @Test
    fun `Calling presentMovieList calls showMovieViewItems on view`() {
        testMovieListPresenter.presentMovieList(testTMDbMoviePage)

        verify(exactly = 1) { testMovieListView.showMovieViewItems(any())}
        assertEquals(testTMDbMoviePage.results.size, showMovieViewItemsList.size)
        showMovieViewItemsList.forEachIndexed { index, movieViewItem ->
            assertEquals(testTMDbMoviePage.results[index].title, movieViewItem.title)
            assertEquals(testTMDbMoviePage.results[index].overview, movieViewItem.overview)
        }
    }

    @Test
    fun `Calling presentException calls showError on view`() {
        testMovieListPresenter.presentException(Exception())

        verify(exactly = 1) { testMovieListView.showError(any()) }
    }
}