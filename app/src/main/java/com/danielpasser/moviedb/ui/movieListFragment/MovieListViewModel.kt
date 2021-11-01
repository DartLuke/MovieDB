package com.danielpasser.moviedb.ui.movieListFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielpasser.moviedb.api.Api
import com.danielpasser.moviedb.model.Movie
import com.danielpasser.moviedb.utils.Category
import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(private val retrofit: Api)

    : ViewModel() {

    private val TAG = "MovieListViewModel"
    lateinit var corutin: Deferred<Unit>;
    val dataState: LiveData<DataState<List<Movie>>> get() = _dataState
    private var _dataState = MutableLiveData<DataState<List<Movie>>>()
    private var category = Category.TRENDING

    fun firstLoad() {
        when (category) {
            Category.TRENDING -> getTrending()
            Category.FAVORITE -> getFavorite()
            Category.NOW_PLAYING -> getNowPlaying()
        }
    }

    fun getTrending() {
        category = Category.TRENDING
        if (this::corutin.isInitialized && corutin.isActive) corutin.cancel()
        corutin = viewModelScope.async(Dispatchers.IO) {
            try {
                _dataState.postValue(DataState.Loading)
                val movieList = retrofit.getTrendingMovies(Constants.API_KEY).results
                _dataState.postValue(DataState.Success(movieList))
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _dataState.postValue(DataState.Error(e))
                throw e
            }
        }
    }

    fun getNowPlaying() {
        category = Category.NOW_PLAYING
        if (this::corutin.isInitialized && corutin.isActive) corutin.cancel()
        corutin = viewModelScope.async(Dispatchers.IO) {
            try {
                _dataState.postValue(DataState.Loading)
                val movieList = retrofit.getNowPlaying(Constants.API_KEY).results
                _dataState.postValue(DataState.Success(movieList))
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _dataState.postValue(DataState.Error(e))
            }
        }
    }

    fun getFavorite() {
        category = Category.FAVORITE

        if (this::corutin.isInitialized && corutin.isActive) corutin.cancel()
        _dataState.value = DataState.Loading
        _dataState.value.toString()
        corutin = viewModelScope.async(Dispatchers.IO) {
            try {
                val movieList =
                    retrofit.getFavorite(Constants.API_KEY, Constants.session_id).results
                _dataState.postValue(DataState.Success(movieList))
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _dataState.postValue(DataState.Error(e))
            }
        }
    }

    fun clearLiveData() {
        _dataState.value = DataState.Error(null)
    }
}