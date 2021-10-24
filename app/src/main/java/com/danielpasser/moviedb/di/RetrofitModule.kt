package com.danielpasser.moviedb.di

import android.util.Log
import com.danielpasser.moviedb.api.Api
import com.danielpasser.moviedb.utils.Constants
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import javax.inject.Singleton
import com.google.gson.JsonObject
import java.io.IOException


@Module
@InstallIn(SingletonComponent::class)
class RetrofitModule {
  private val TAG="RetrofitModule"
    @Singleton
    @Provides
    fun provideRetrofit(client: OkHttpClient) =
        Retrofit.Builder().baseUrl(Constants.BASE_URL).client(client)
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideApi(retrofit: Retrofit.Builder): Api = retrofit.build().create(Api::class.java)

    @Singleton
    @Provides
    fun provideOkHttpClient(interceptor: Interceptor): OkHttpClient {
        val interceptorLogger = HttpLoggingInterceptor()
        interceptorLogger.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().addInterceptor(interceptorLogger)
            .addInterceptor(interceptor).build()
    }

    @Singleton
    @Provides
    fun interceptor(): Interceptor = Interceptor() { chain ->
        val response = chain.proceed(chain.request())
        if (response.code > 399) {
            val body = response.body
            var errorMessage = ""

            try {
                val source = body?.source()
                source?.request(Long.MAX_VALUE)
                val buffer = source?.buffer()
                val charset = body?.contentType()?.charset(Charset.forName("UTF-8"));
                val json = buffer?.clone()?.readString(charset!!)
                if (json != null) {
                    Log.e("Test", json)
                }
                val obj: JsonElement? = JsonParser().parse(json)
                if (obj is JsonObject && obj.has("status_message")) {
                    errorMessage = (obj as JsonObject)["status_message"].asString
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error: " + e.message);
            }
            throw IOException("Code " + response.code + " " + errorMessage)
        }
        return@Interceptor response
    }


}