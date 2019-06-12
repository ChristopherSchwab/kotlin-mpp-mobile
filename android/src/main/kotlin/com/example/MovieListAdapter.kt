package com.example

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import common.tmdb.entities.MovieViewItem
import kotlinx.android.synthetic.main.movie_list_item.view.*
import java.lang.Exception

class MovieListAdapter : RecyclerView.Adapter<MovieListAdapter.MovieListItemViewHolder>() {

    private val movies: MutableList<MovieViewItem> = mutableListOf()

    fun addMovies(movies: List<MovieViewItem>) {
        this.movies.size.also {
            this.movies.addAll(movies)
            notifyItemRangeInserted(it, movies.size)
        }
    }

    override fun getItemViewType(position: Int): Int = R.layout.movie_list_item

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieListItemViewHolder {
        return MovieListItemViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_list_item, parent, false))
    }

    override fun getItemCount(): Int = movies.size

    override fun onBindViewHolder(viewHolder: MovieListItemViewHolder, position: Int) = viewHolder.bind(movies[position])

    class MovieListItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(movieViewItem: MovieViewItem) {
            itemView.tvTitle.text = movieViewItem.title
            itemView.tvReleaseDate.text = movieViewItem.releaseDate

            itemView.ivPoster.visibility = View.VISIBLE
            itemView.pbPoster.visibility = View.VISIBLE
            itemView.tvPosterMissing.visibility = View.INVISIBLE

            if (movieViewItem.posterUrl != null) {
                Picasso.get()
                    .load(movieViewItem.posterUrl)
                    .into(
                        itemView.ivPoster,
                        object : Callback {
                            override fun onSuccess() {
                                itemView.pbPoster.visibility = View.GONE
                            }

                            override fun onError(e: Exception?) {
                                itemView.ivPoster.visibility = View.INVISIBLE
                                itemView.pbPoster.visibility = View.GONE
                                itemView.tvPosterMissing.visibility = View.VISIBLE
                            }
                        }
                    )
            } else {
                itemView.ivPoster.visibility = View.INVISIBLE
                itemView.pbPoster.visibility = View.GONE
                itemView.tvPosterMissing.visibility = View.VISIBLE
            }
        }
    }
}