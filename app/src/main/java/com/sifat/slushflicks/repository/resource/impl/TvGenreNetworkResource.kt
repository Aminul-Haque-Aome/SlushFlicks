package com.sifat.slushflicks.repository.resource.impl

import androidx.lifecycle.LiveData
import com.sifat.slushflicks.api.ApiResponse
import com.sifat.slushflicks.api.home.genre.GenreService
import com.sifat.slushflicks.api.home.genre.model.GenreListApiModel
import com.sifat.slushflicks.data.DataManager
import com.sifat.slushflicks.utils.api.NetworkStateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

class TvGenreNetworkResource(
    private val genreService: GenreService,
    private val apiKey: String,
    dataManager: DataManager,
    networkStateManager: NetworkStateManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseGenreResource(dataManager, networkStateManager, dispatcher) {
    override fun createCall(): LiveData<ApiResponse<GenreListApiModel>> {
        return genreService.getTvGenre(apiKey)
    }
}