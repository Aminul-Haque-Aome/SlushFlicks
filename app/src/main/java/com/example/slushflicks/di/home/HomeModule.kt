package com.example.slushflicks.di.home

import com.example.slushflicks.api.home.movie.MovieService
import com.example.slushflicks.data.DataManager
import com.example.slushflicks.di.constant.NAME_API_KEY
import com.example.slushflicks.repository.*
import com.example.slushflicks.utils.api.NetworkStateManager
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module
class HomeModule {

    @HomeScope
    @Provides
    fun provideMovieService(retrofit: Retrofit): MovieService =
        retrofit.create(MovieService::class.java)

    @HomeScope
    @Provides
    fun provideTrendingRepository(
        movieService: MovieService,
        @Named(NAME_API_KEY) apiKey: String,
        networkStateManager: NetworkStateManager,
        dataManager: DataManager
    ): TrendingRepository {
        return TrendingRepository(movieService, apiKey, dataManager, networkStateManager)
    }

    @HomeScope
    @Provides
    fun providePopularRepository(
        movieService: MovieService,
        @Named(NAME_API_KEY) apiKey: String,
        networkStateManager: NetworkStateManager,
        dataManager: DataManager
    ): PopularMovieRepository {
        return PopularMovieRepository(movieService, apiKey, dataManager, networkStateManager)
    }

    @HomeScope
    @Provides
    fun provideTopRatedRepository(
        movieService: MovieService,
        @Named(NAME_API_KEY) apiKey: String,
        networkStateManager: NetworkStateManager,
        dataManager: DataManager
    ): TopRatedRepository {
        return TopRatedRepository(movieService, apiKey, dataManager, networkStateManager)
    }

    @HomeScope
    @Provides
    fun provideUpcomingRepository(
        movieService: MovieService,
        @Named(NAME_API_KEY) apiKey: String,
        networkStateManager: NetworkStateManager,
        dataManager: DataManager
    ): UpcomingRepository {
        return UpcomingRepository(movieService, apiKey, dataManager, networkStateManager)
    }

    @HomeScope
    @Provides
    fun provideNowPlayingRepository(
        movieService: MovieService,
        @Named(NAME_API_KEY) apiKey: String,
        networkStateManager: NetworkStateManager,
        dataManager: DataManager
    ): NowPlayingRepository {
        return NowPlayingRepository(movieService, apiKey, dataManager, networkStateManager)
    }
}