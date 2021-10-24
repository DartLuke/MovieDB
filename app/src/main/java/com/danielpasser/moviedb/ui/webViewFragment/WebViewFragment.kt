package com.danielpasser.moviedb.ui.webViewFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.danielpasser.moviedb.R
import com.danielpasser.moviedb.model.Movie
import com.danielpasser.moviedb.model.Session
import com.danielpasser.moviedb.model.Token
import com.danielpasser.moviedb.ui.movieListFragment.MovieListViewModel
import com.danielpasser.moviedb.utils.Constants
import com.danielpasser.moviedb.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WebViewFragment : Fragment() {
    private val viewModel: WebViewModel by viewModels()
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    private var request_token: String = "";


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_web_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(view)
        setupObservers()
        viewModel.getToken()
    }

    private fun setupUI(view: View) {
        progressBar = view.findViewById(R.id.progressBar)
        webView = view.findViewById(R.id.web_view)
        webView.settings.javaScriptEnabled = true



        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)

            }

        }

        webView.webViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(view: WebView?, url: String?, isReload: Boolean) {
                super.doUpdateVisitedHistory(view, url, isReload)
                if (url != null && url.contains("allow")) {

//
                    webView.visibility = View.GONE
                    viewModel.getSession(request_token)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView, title: String) {
                super.onReceivedTitle(view, title)

            }

        }

    }

    private fun setupObservers() {

        ///dateStateToken
        viewModel.dataStateToken.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<Token> -> {
                    Log.v("Test", dataState.data.toString())
                    request_token = dataState.data.request_token
                    startWebView(request_token)
                    showProgressBar(false)
                }
                is DataState.Error -> {
                    showProgressBar(false)

                    displayError(dataState.exception?.message)
                }
                is DataState.Loading -> {

                    showProgressBar(true)
                }
            }
        })

///dateStateSession
        viewModel.dataStateSession.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<Session> -> {
                    Log.v("Test", dataState.data.toString())
                    showProgressBar(false)
                    Constants.session_id=dataState.data.session_id
                    activity?.onBackPressed()
                }
                is DataState.Error -> {
                    showProgressBar(false)

                    displayError(dataState.exception?.message)
                }
                is DataState.Loading -> {

                    showProgressBar(true)
                }
            }
        })


    }

    private fun startWebView(request_token: String) {
        webView.loadUrl(Constants.BASE_URL_AUTH + request_token)


    }

    private fun displayError(message: String?) {

    }

    private fun showProgressBar(b: Boolean) {

    }

}
