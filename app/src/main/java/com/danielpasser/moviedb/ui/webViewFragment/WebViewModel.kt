package com.danielpasser.moviedb.ui.webViewFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.danielpasser.moviedb.api.Api
import com.danielpasser.moviedb.model.Session
import com.danielpasser.moviedb.model.Token
import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WebViewModel @Inject constructor(private val retrofit: Api) : ViewModel() {
    val dataStateToken: LiveData<DataState<Token>> get() = _dataStateToken
    private var _dataStateToken = MutableLiveData<DataState<Token>>()

    val dataStateSession: LiveData<DataState<Session>> get() = _dataStateSession
    private var _dataStateSession = MutableLiveData<DataState<Session>>()


    fun getToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dataStateToken.postValue(DataState.Loading)
                val token = retrofit.getToken(Constants.API_KEY)
                _dataStateToken.postValue(DataState.Success(token))
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                _dataStateToken.postValue(DataState.Error(e))
            }
        }
    }

    fun getSession(request_token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _dataStateSession.postValue(DataState.Loading)
                val session = retrofit.getSession(Constants.API_KEY, request_token)
                _dataStateSession.postValue(DataState.Success(session))
            } catch (e: Exception) {
                Log.e("Error", e.message.toString())
                _dataStateSession.postValue(DataState.Error(e))
            }
        }
    }
}
