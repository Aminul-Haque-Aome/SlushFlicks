package com.sifat.slushflicks.repository

import androidx.lifecycle.LiveData
import com.sifat.slushflicks.api.ApiTag.Companion.TOP_RATED_API_TAG
import com.sifat.slushflicks.api.home.movie.MovieService
import com.sifat.slushflicks.data.DataManager
import com.sifat.slushflicks.model.MovieModelMinimal
import com.sifat.slushflicks.repository.resource.impl.MovieListNetworkResource
import com.sifat.slushflicks.ui.state.DataState
import com.sifat.slushflicks.utils.Label.Companion.TOP_RATED_LABEL
import com.sifat.slushflicks.utils.api.NetworkStateManager

class TopRatedRepository(
    movieService: MovieService,
    apiKey: String,
    dataManager: DataManager,
    networkStateManager: NetworkStateManager
) : BaseMovieListRepository(movieService, apiKey, dataManager, networkStateManager) {

    override fun getMovieList(nextPage: Int): LiveData<DataState<List<MovieModelMinimal>>> {
        val requestModel = MovieListNetworkResource.RequestModel(
            page = nextPage,
            apiKey = apiKey,
            apiTag = TOP_RATED_API_TAG
        )
        val movieListNetworkResource = MovieListNetworkResource(
            requestModel = requestModel,
            movieService = movieService,
            networkStateManager = networkStateManager,
            dataManager = dataManager,
            collection = TOP_RATED_LABEL
        )
        return movieListNetworkResource.asLiveData()
    }
}