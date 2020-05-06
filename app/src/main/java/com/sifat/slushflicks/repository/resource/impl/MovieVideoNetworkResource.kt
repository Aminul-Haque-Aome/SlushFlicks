package com.sifat.slushflicks.repository.resource.impl

import androidx.lifecycle.LiveData
import com.sifat.slushflicks.api.ApiResponse
import com.sifat.slushflicks.api.details.model.VideoApiModel
import com.sifat.slushflicks.api.details.model.VideoListApiModel
import com.sifat.slushflicks.api.home.movie.MovieService
import com.sifat.slushflicks.data.DataManager
import com.sifat.slushflicks.utils.api.NetworkStateManager

class MovieVideoNetworkResource(
    private val movieService: MovieService,
    private val dataManager: DataManager,
    private val requestModel: RequestModel,
    networkStateManager: NetworkStateManager
) : BaseVideoNetworkResource(networkStateManager) {

    override fun createCall(): LiveData<ApiResponse<VideoListApiModel>> {
        return movieService.getMovieVideos(
            movieId = requestModel.movieId,
            apiKey = requestModel.apiKey
        )
    }

    override suspend fun updateLocalDb(cacheData: VideoApiModel?) {
        cacheData?.let { model ->
            dataManager.updateMovieDetails(model, requestModel.movieId)
        }
    }

    data class RequestModel(val apiKey: String, val movieId: Long)
}