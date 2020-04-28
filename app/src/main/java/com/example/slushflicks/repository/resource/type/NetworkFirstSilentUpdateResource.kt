package com.example.slushflicks.repository.resource.type

import com.example.slushflicks.api.ApiErrorResponse
import com.example.slushflicks.api.ApiSuccessResponse
import com.example.slushflicks.repository.resource.NetworkBoundResource
import com.example.slushflicks.ui.state.DataErrorResponse
import com.example.slushflicks.ui.state.DataState
import com.example.slushflicks.utils.api.NetworkStateManager

abstract class NetworkFirstSilentUpdateResource<ApiData, CacheData, AppData>(private val networkStateManager: NetworkStateManager) :
    NetworkBoundResource<ApiData, CacheData, AppData>() {

    override fun execute() {
        super.execute()
        if (networkStateManager.isOnline()) {
            doNetworkRequest()
        } else {
            doCacheRequest()
        }
    }

    override fun onErrorReturn(apiErrorResponse: ApiErrorResponse<ApiData>) {
        doCacheRequest()
    }

    override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ApiData>) {
        /**
         * Change Api response to data response which viewModel will use to
         * show relevant information in view
         * */
        val dataSuccessResponse = getDataSuccessResponse(response)
        updateLocalDb(dataSuccessResponse.data)
        onCompleteJob(
            DataState.Success<AppData>(getAppDataSuccessResponse(dataSuccessResponse.data))
        )
    }

    override suspend fun createCacheRequestAndReturn() {
        val cacheResponse = getFromCache()
        cacheResponse?.let { data ->
            onCompleteJob(DataState.Success<AppData>(getAppDataSuccessResponse(data)))
        } ?: onCompleteJob(DataState.Error<AppData>(DataErrorResponse()))
    }
}