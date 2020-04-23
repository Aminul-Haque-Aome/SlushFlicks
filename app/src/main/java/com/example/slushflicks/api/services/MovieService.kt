package com.example.slushflicks.api.services

import com.example.slushflicks.api.KEY_API_KEY
import com.example.slushflicks.api.KEY_TIME_WINDOW
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {

    @GET("/movie/popular")
    fun getPopularMovies()

    @GET("/trending/movie/{time_window}")
    fun getTrendingMovies(@Path(KEY_TIME_WINDOW) timeRange : String, @Query(KEY_API_KEY) apiKey : String)
}