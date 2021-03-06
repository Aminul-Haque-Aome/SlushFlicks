package com.sifat.slushflicks.repository.resource.impl

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import com.sifat.slushflicks.api.ApiErrorResponse
import com.sifat.slushflicks.api.ApiResponse
import com.sifat.slushflicks.api.ApiSuccessResponse
import com.sifat.slushflicks.api.ApiTag.Companion.TV_CREDITS_API_TAG
import com.sifat.slushflicks.api.StatusCode.Companion.INTERNAL_ERROR
import com.sifat.slushflicks.api.details.model.CreditsApiModel
import com.sifat.slushflicks.api.home.tv.TvService
import com.sifat.slushflicks.data.DataManager
import com.sifat.slushflicks.helper.JobManager
import com.sifat.slushflicks.model.CastModel
import com.sifat.slushflicks.repository.resource.type.CacheUpdateResource
import com.sifat.slushflicks.ui.state.DataSuccessResponse
import com.sifat.slushflicks.utils.api.NetworkStateManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class TvCastNetworkResource(
    private val tvService: TvService,
    private val dataManager: DataManager,
    private val requestModel: RequestModel,
    private val jobManager: JobManager,
    networkStateManager: NetworkStateManager,
    dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CacheUpdateResource<CreditsApiModel, List<CastModel>, Int>(networkStateManager, dispatcher) {

    @VisibleForTesting
    private val maxCreditSize = 15

    override fun createCall(): LiveData<ApiResponse<CreditsApiModel>> {
        return tvService.getTvShowCredits(
            tvShowId = requestModel.tvShowId,
            apiKey = requestModel.apiKey
        )
    }

    override suspend fun updateLocalDb(cacheData: List<CastModel>?) {
        cacheData?.let {
            // Saving only first 15 casts
            val size = if (cacheData.size > maxCreditSize) maxCreditSize else cacheData.size
            val castList = cacheData.subList(0, size)
            dataManager.updateTvDetails(castList, requestModel.tvShowId)
        }
    }

    override fun setJob(job: Job) {
        jobManager.addJob(TAG, job)
    }

    override fun getAppDataSuccessResponse(response: DataSuccessResponse<List<CastModel>>): DataSuccessResponse<Int> {
        return DataSuccessResponse(
            data = response.data?.size,
            message = response.message
        )
    }

    override fun getDataSuccessResponse(response: ApiSuccessResponse<CreditsApiModel>): DataSuccessResponse<List<CastModel>> {
        return DataSuccessResponse(
            data = response.data?.casts,
            message = response.message
        )
    }

    override fun getInternalErrorResponse() = ApiErrorResponse<CreditsApiModel>(
        statusCode = INTERNAL_ERROR,
        apiTag = TV_CREDITS_API_TAG
    )

    data class RequestModel(val apiKey: String, val tvShowId: Long)

    companion object {
        private const val TAG = "TvCastNetworkResource"
    }
}