package com.danielpasser.moviedb.ui.MovieDetailsFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.danielpasser.moviedb.R
import com.danielpasser.moviedb.model.Movie

import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieDetailsFragment : Fragment() {
    private val safeArgs: MovieDetailsFragmentArgs by navArgs()
    private val movie: Movie by lazy { safeArgs.movie }
    private val viewModel: MovieDetailsViewModel by viewModels()
    private lateinit var imageViewFavorite: ImageView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movie_details, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setupObserver()
        viewModel.isfavorite(movie.id)

    }

    private fun setupObserver() {

        viewModel.dataStateIsFavorite.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<Boolean> -> {
                    setFavorite(dataState.data)
                    showProgressBar(false)

                }
                is DataState.Error -> {

                        showProgressBar(false)

                        if (dataState.exception?.message?.contains(Constants.AUTH_FAILED)==true) {
                            switchAuthFragment()
                        }
                        displayError(dataState.exception?.message)
                }
                is DataState.Loading -> {

                    showProgressBar(true)
                }
            }

        })


    }

    private fun setFavorite(isFavorite: Boolean) {
        if (isFavorite) imageViewFavorite.setImageResource(R.drawable.ic_favorite_true_foreground)
        else imageViewFavorite.setImageResource(R.drawable.ic_favorite_false_foreground)
    }




    private fun switchAuthFragment() {
        viewModel.clearLiveData()
        findNavController().navigate(R.id.action_MovieDetails_to_WebViewFragment)
    }

    private fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun displayError(message: String?) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun setupUI(view: View) {
        progressBar = view.findViewById(R.id.progressBar)

        view.findViewById<TextView>(R.id.text_view_movie_name).text = movie.original_title
        view.findViewById<TextView>(R.id.text_view_movie_description).text = movie.overview

        val imageUrl = Constants.BASE_URL_IMAGE_ORIGINAL + movie.backdrop_path
        Glide.with(view.context).load(imageUrl)
            .into(view.findViewById(R.id.image_view_background));

        imageViewFavorite = view.findViewById(R.id.image_view_favorite)
        imageViewFavorite.setOnClickListener { view ->
            changeFavorite()
        }

    }

    private fun changeFavorite() {
        viewModel.changeFavorite(movie.id)
    }
}

