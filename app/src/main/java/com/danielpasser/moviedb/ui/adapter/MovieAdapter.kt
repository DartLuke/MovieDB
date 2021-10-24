package com.danielpasser.moviedb.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.danielpasser.moviedb.R
import com.danielpasser.moviedb.model.Movie
import com.danielpasser.moviedb.utils.Constants

class MovieAdapter(private var onClickListener: OnClickListener) :
    RecyclerView.Adapter<MovieAdapter.MyViewHolder>() {

    private val movies: ArrayList<Movie> = arrayListOf()

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(movie: Movie) {
            itemView.apply {
                setOnClickListener { onClickListener.onClickItem(movie) }
                findViewById<TextView>(R.id.item_view_text_view_movie_name).text=movie.title
                Glide.with(context).load(Constants.BASE_URL_IMAGE_ORIGINAL+movie.poster_path)
                    .into(findViewById(R.id.item_view_image_view))
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder = MyViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
    )

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount() = movies.size

    fun updateData(movies: List<Movie>) {
        this.movies.apply {
            clear()
            addAll(movies)
        }
        notifyDataSetChanged()
    }
    interface OnClickListener {
        fun onClickItem(movie: Movie)

    }

}
