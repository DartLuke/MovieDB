package com.danielpasser.moviedb.ui.MovieDetailsFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielpasser.moviedb.api.Api
import com.danielpasser.moviedb.model.BodyFavorite
import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(private val retrofit: Api)

    : ViewModel() {
    val dataStateIsFavorite: LiveData<DataState<Boolean>> get() = _dataStateIsFavorite
    private var _dataStateIsFavorite = MutableLiveData<DataState<Boolean>>()
    private val TAG = "MovieDetailsViewModel"
    private var isFavorite = false;

    fun isfavorite(movieId: Int) {

        if (Constants.session_id == "") {
            _dataStateIsFavorite.value =
                DataState.Error(java.lang.Exception(Constants.AUTH_FAILED))
            return
        }
        _dataStateIsFavorite.value = DataState.Loading
        _dataStateIsFavorite.value.toString()
        viewModelScope.async(Dispatchers.IO) {
            try {
                val movieList =
                    retrofit.getFavorite(Constants.API_KEY, Constants.session_id).results
                for (movie in movieList) {
                    if (movie.id == movieId) {
                        isFavorite = true
                        break
                    }
                }
                _dataStateIsFavorite.postValue(DataState.Success(isFavorite))
            } catch (e: Exception) {
                _dataStateIsFavorite.postValue(DataState.Error(e))
                throw e
            }
        }
    }


    fun changeFavorite(movieId: Int) {

        if (Constants.session_id == "") {
            _dataStateIsFavorite.value =
                DataState.Error(java.lang.Exception(Constants.AUTH_FAILED))
            return
        }
        _dataStateIsFavorite.value = DataState.Loading
        viewModelScope.async(Dispatchers.IO) {
            try {

                retrofit.changeFavorite(
                    Constants.API_KEY,
                    Constants.session_id,
                    BodyFavorite(
                        "movie",
                        movieId,
                        !isFavorite
                    )
                )
                isFavorite = !isFavorite
                _dataStateIsFavorite.postValue(DataState.Success(isFavorite))

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _dataStateIsFavorite.postValue(DataState.Error(e))
                throw e
            }
        }
    }


    fun clearLiveData() {
        _dataStateIsFavorite.value = DataState.Error(null)
    }
}