package com.example.slushflicks.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.slushflicks.di.home.HomeScope

import javax.inject.Inject
import javax.inject.Provider

@HomeScope
open class HomeViewModelFactory @Inject
constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) : BaseViewModelFactory(creators)