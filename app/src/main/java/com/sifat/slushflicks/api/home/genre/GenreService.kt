package com.sifat.slushflicks.api.home.genre

import com.sifat.slushflicks.api.ApiEndPoint
import com.sifat.slushflicks.api.ApiEndPoint.Companion.GENRES_TV_URL
import com.sifat.slushflicks.api.ApiRequest.Companion.QUERY_KEY_API_KEY
import com.sifat.slushflicks.api.ApiTag.Companion.MOVIE_GENRE_API_TAG
import com.sifat.slushflicks.api.ApiTag.Companion.TV_GENRE_API_TAG
import com.sifat.slushflicks.api.home.genre.model.GenreListApiModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Tag

interface GenreService {

    @GET(GENRES_TV_URL)
    suspend fun getTvGenre(
        @Query(QUERY_KEY_API_KEY) apiKey: String,
        @Tag tag: String = TV_GENRE_API_TAG
    ): Response<GenreListApiModel>

    @GET(ApiEndPoint.GENRES_MOVIE_URL)
    suspend fun getMovieGenre(
        @Query(QUERY_KEY_API_KEY) apiKey: String,
        @Tag tag: String = MOVIE_GENRE_API_TAG
    ): Response<GenreListApiModel>
}