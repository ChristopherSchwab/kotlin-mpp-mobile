package com.example

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView

interface OnPageEndCallback {
    fun onPageEnd()
}

class MovieListScrollListener(
    private val gridLayoutManager: GridLayoutManager,
    private val visibleThreshold: Int,
    private val callback: OnPageEndCallback
) : RecyclerView.OnScrollListener() {

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val lastVisibleItemPosition = gridLayoutManager.findLastVisibleItemPosition()
        val totalItemCount = gridLayoutManager.itemCount

        if (lastVisibleItemPosition + visibleThreshold > totalItemCount) {
            callback.onPageEnd()
        }
    }
}

