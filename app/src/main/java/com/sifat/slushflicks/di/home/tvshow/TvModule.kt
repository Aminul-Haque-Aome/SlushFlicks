package com.sifat.slushflicks.di.home.tvshow

import com.sifat.slushflicks.api.home.tv.TvService
import com.sifat.slushflicks.di.constant.NAME_AIR_TODAY_TV_REPO
import com.sifat.slushflicks.di.constant.NAME_POPULAR_TV_REPO
import com.sifat.slushflicks.di.constant.NAME_TOP_RATED_TV_REPO
import com.sifat.slushflicks.di.constant.NAME_TRENDING_TV_REPO
import com.sifat.slushflicks.repository.tv.TvHomeRepository
import com.sifat.slushflicks.repository.tv.TvListRepository
import com.sifat.slushflicks.repository.tv.impl.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

@Module(includes = [InnerModule::class])
abstract class TvModule {

    @TvShowScope
    @Binds
    @Named(NAME_TRENDING_TV_REPO)
    abstract fun provideTrendingTvRepository(trendingTvRepositoryImpl: TrendingTvRepositoryImpl): TvListRepository

    @TvShowScope
    @Binds
    @Named(NAME_POPULAR_TV_REPO)
    abstract fun providePopularTvRepository(popularTvRepositoryImpl: PopularTvRepositoryImpl): TvListRepository

    @TvShowScope
    @Binds
    @Named(NAME_AIR_TODAY_TV_REPO)
    abstract fun provideAirTodayTvRepository(airTodayTvRepositoryImpl: AirTodayTvRepositoryImpl): TvListRepository

    @TvShowScope
    @Binds
    @Named(NAME_TOP_RATED_TV_REPO)
    abstract fun provideTopRatedTvRepository(topRatedTvRepositoryImpl: TopRatedTvRepositoryImpl): TvListRepository

    @TvShowScope
    @Binds
    abstract fun provideTvHomeRepository(tvHomeRepositoryImpl: TvHomeRepositoryImpl): TvHomeRepository

}

@Module
class InnerModule {
    @TvShowScope
    @Provides
    fun provideTvService(retrofit: Retrofit): TvService =
        retrofit.create(TvService::class.java)
}