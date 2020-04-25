package com.example.slushflicks.di.app

import com.example.slushflicks.di.home.HomeFragmentBuilderModule
import com.example.slushflicks.di.home.HomeModule
import com.example.slushflicks.di.home.HomeViewModelModule
import com.example.slushflicks.di.home.HomeScope
import com.example.slushflicks.ui.home.HomeActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilderModule {

    @HomeScope
    @ContributesAndroidInjector(modules = [HomeModule::class, HomeViewModelModule::class, HomeFragmentBuilderModule::class])
    internal abstract fun bindHomeActivity(): HomeActivity

}