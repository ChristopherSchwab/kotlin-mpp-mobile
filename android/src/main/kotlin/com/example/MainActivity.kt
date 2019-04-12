package com.example

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.rvMovieList
import common.tmdb.entities.MovieViewItem
import common.tmdb.MovieListController
import common.tmdb.MovieListInteractor
import common.tmdb.MovieListPresenter
import common.tmdb.MovieListView
import common.util.HttpClientHttpRequestSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivity : MovieListView, AppCompatActivity(), OnPageEndCallback, CoroutineScope {

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    val movieListController = MovieListController(
        MovieListInteractor(
            MovieListPresenter(this),
            HttpClientHttpRequestSerializer(),
            "api.themoviedb.org",
            "0a055ad296b0a5d7496d9a0f0cb2a7b0"
        )
    )

    private val movieListAdapter = MovieListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        job = Job()

        val movieListLayoutManager = GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false).apply {
            spanSizeLookup = MovieListSpanSizeLookup(movieListAdapter)
        }

        val movieListScrollListener = MovieListScrollListener(movieListLayoutManager, 4, this)

        rvMovieList.apply {
            adapter = movieListAdapter
            layoutManager = movieListLayoutManager
            addOnScrollListener(movieListScrollListener)
        }

        movieListController.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun showMovieViewItems(movieViewItems: List<MovieViewItem>) {
        launch {
            movieListAdapter.addMovies(movieViewItems)
        }
    }

    override fun onPageEnd() {
        movieListController.onPageEnd()
    }

    override fun showError() {
        Toast.makeText(this, R.string.error, Toast.LENGTH_LONG)
    }

}