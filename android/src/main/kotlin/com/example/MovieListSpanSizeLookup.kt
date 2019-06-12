package com.example

import android.support.v7.widget.GridLayoutManager

class MovieListSpanSizeLookup(private val adapter: MovieListAdapter): GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when (adapter.getItemViewType(position)) {
            R.layout.movie_list_item -> 1
            else -> throw IndexOutOfBoundsException("Position $position can't be allocated to a SpanSize")
        }
    }

}