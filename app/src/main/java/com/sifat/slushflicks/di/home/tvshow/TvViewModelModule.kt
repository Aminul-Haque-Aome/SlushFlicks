package com.sifat.slushflicks.di.home.tvshow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sifat.slushflicks.di.ViewModelKey
import com.sifat.slushflicks.di.constant.NAME_CONTENT_FACTORY
import com.sifat.slushflicks.ui.home.tvshow.viewmodel.TvShowViewModel
import com.sifat.slushflicks.ui.viewmodel.ContentViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Named

@Module
abstract class TvViewModelModule {

    @TvShowScope
    @Binds
    @Named(NAME_CONTENT_FACTORY)
    abstract fun bindFactory(contentViewModelFactory: ContentViewModelFactory): ViewModelProvider.Factory

    @TvShowScope
    @Binds
    @IntoMap
    @ViewModelKey(TvShowViewModel::class)
    abstract fun bindTvShowViewModel(tvShowViewModel: TvShowViewModel): ViewModel
}