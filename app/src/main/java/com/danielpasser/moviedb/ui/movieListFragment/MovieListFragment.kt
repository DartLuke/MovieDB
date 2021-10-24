package com.danielpasser.moviedb.ui.movieListFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.danielpasser.moviedb.R
import com.danielpasser.moviedb.model.Movie
import com.danielpasser.moviedb.ui.adapter.MovieAdapter
import com.danielpasser.moviedb.ui.decorator.ItemDecorator
import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieListFragment : Fragment(), MovieAdapter.OnClickListener {
    private val viewModel: MovieListViewModel by viewModels()
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private val movieAdapter = MovieAdapter(this)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_movie_list, container, false)


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_movie_list_fragment, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_now_playing -> {
                viewModel.getNowPlaying()
                true
            }
            R.id.action_trending -> {
                viewModel.getTrending()
                true
            }
            R.id.action_favorite -> {
                viewModel.getFavorite()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter(view)
        setupUI(view)
        setupObservers()
        viewModel.firstLoad()


    }

    private fun switchAuthFragment() {
        viewModel.clearLiveData()
        findNavController().navigate(R.id.action_MovieListFragment_to_WebViewFragment)
    }

    private fun switchMovieDetailsFragment(movie: Movie) {
        viewModel.clearLiveData()
        Log.e("Test", "switching to movie")
        val nav = MovieListFragmentDirections.actionMovieListFragmentToMovieDetails(movie)
        findNavController().navigate(nav)
    }

    private fun setupUI(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupAdapter(view: View) {
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview_movies)

        recyclerView.apply {

            layoutManager = LinearLayoutManager(view.context)
            addItemDecoration(ItemDecorator(10))
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {

        viewModel.dataState.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<Movie>> -> {
                    changeListViewData(dataState.data)
                    showRecycleView(true)
                    showProgressBar(false)

                }
                is DataState.Error -> {
                    if (dataState != null) {
                        showProgressBar(false)
                        showRecycleView(false)
                        if (dataState.exception?.message?.contains(Constants.AUTH_FAILED)==true) {
                            switchAuthFragment()
                        }
                        displayError(dataState.exception?.message)
                    }
                }
                is DataState.Loading -> {
                    showRecycleView(false)
                    showProgressBar(true)
                }
            }

        })
    }

    private fun displayError(message: String?) {
        if (message != null)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun showRecycleView(isVisible: Boolean) {
        recyclerView.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    private fun changeListViewData(movies: List<Movie>) {
        movieAdapter.updateData(movies)
    }

    override fun onClickItem(movie: Movie) {

        switchMovieDetailsFragment(movie)
    }


}