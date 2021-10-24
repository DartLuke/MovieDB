package com.danielpasser.moviedb.api

import com.danielpasser.moviedb.model.BodyFavorite
import com.danielpasser.moviedb.model.MovieResult
import com.danielpasser.moviedb.model.Session
import com.danielpasser.moviedb.model.Token
import retrofit2.http.*

interface Api {

    @GET("trending/all/day")
    suspend fun getTrendingMovies(
        @Query("api_key") api_key: String

    ): MovieResult


    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") api_key: String

    ): MovieResult


    @GET("authentication/token/new")
    suspend fun getToken(
        @Query("api_key") api_key: String

    ): Token

    @FormUrlEncoded
    @POST("authentication/session/new")
    suspend fun getSession(
        @Query("api_key") api_key: String, @Field("request_token") request_token: String

    ): Session


    @POST("account/{account_id}/favorite")
    suspend fun changeFavorite(
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String,
        @Body bodyFavorite: BodyFavorite
    ): Session

    @GET("account/{account_id/favorite/movies")
    suspend fun getFavorite(
        @Query("api_key") api_key: String,
        @Query("session_id") session_id: String
    ): MovieResult


}