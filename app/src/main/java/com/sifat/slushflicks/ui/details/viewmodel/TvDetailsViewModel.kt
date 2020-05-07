package com.sifat.slushflicks.ui.details.viewmodel

import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.sifat.slushflicks.di.details.TvDetailsScope
import com.sifat.slushflicks.model.ReviewModel
import com.sifat.slushflicks.model.ShowModelMinimal
import com.sifat.slushflicks.model.TvModel
import com.sifat.slushflicks.provider.DynamicLinkProvider
import com.sifat.slushflicks.repository.tv.TvDetailsRepository
import com.sifat.slushflicks.ui.base.BaseActionViewModel
import com.sifat.slushflicks.ui.details.state.dataaction.TvDetailDataAction
import com.sifat.slushflicks.ui.details.state.dataaction.TvDetailDataAction.*
import com.sifat.slushflicks.ui.details.state.event.TvDetailsViewEvent
import com.sifat.slushflicks.ui.details.state.event.TvDetailsViewEvent.*
import com.sifat.slushflicks.ui.details.state.viewaction.TvDetailsViewAction
import com.sifat.slushflicks.ui.details.state.viewaction.TvDetailsViewAction.*
import com.sifat.slushflicks.ui.details.state.viewstate.TvDetailsViewState
import com.sifat.slushflicks.ui.helper.getMovieListModel
import com.sifat.slushflicks.ui.helper.getShowListLoadingModels
import com.sifat.slushflicks.ui.home.adapter.model.ShowListModel
import com.sifat.slushflicks.ui.state.DataErrorResponse
import com.sifat.slushflicks.ui.state.DataState
import com.sifat.slushflicks.ui.state.ViewState
import com.sifat.slushflicks.utils.DynamicLinkConst
import java.lang.ref.WeakReference
import javax.inject.Inject

@TvDetailsScope
class TvDetailsViewModel
@Inject constructor(
    private val detailsRepository: TvDetailsRepository,
    private val dynamicLinkProvider: DynamicLinkProvider
) : BaseActionViewModel<TvDetailDataAction, TvDetailsViewAction, TvDetailsViewState>(),
    DynamicLinkProvider.OnEventShareCallback {

    override val viewState by lazy {
        TvDetailsViewState()
    }

    fun setTvShowId(tvShowId: Long) {
        viewState.tvShowId = tvShowId
    }

    fun handleEvent(tvDetailsViewEvent: TvDetailsViewEvent) {
        when (tvDetailsViewEvent) {
            is FetchTvDetailsViewEvent -> {
                fetchTvShowDetails(viewState.tvShowId)
            }
            is FetchTvVideoViewEvent -> {
                fetchTvShowVideo(viewState.tvShowId, viewState.tvModel.numOfSeason)
            }
            is FetchTvCastViewEvent -> {
                fetchTvCast(viewState.tvShowId)
            }
            is FetchRecommendedTvViewEvent -> {
                fetchRecommendedTvShows(viewState.tvShowId)
            }
            is FetchSimilarTvViewEvent -> {
                fetchSimilarTvShows(viewState.tvShowId)
            }
            is FetchTvReviewsViewEvent -> {
                fetchTvShowReviews(viewState.tvShowId)
            }
            is UpdateTvViewEvent -> {
                updateTvShowInfo(tvDetailsViewEvent.showModelMinimal)
            }
            is ShareTvSeriesViewEvent -> {
                shareTvSeries()
            }
        }
    }

    private fun shareTvSeries() {
        getAction().value = ShareTvSeriesViewAction(ViewState.Loading())
        val dynamicLinkParam = DynamicLinkProvider.DynamicLinkParam(
            showId = viewState.tvShowId,
            showName = viewState.tvModel.title,
            showType = DynamicLinkConst.TV_SERIES_TYPE,
            overview = viewState.tvModel.overview,
            imageUrl = viewState.tvModel.backdropPath
        )
        val callback = WeakReference<DynamicLinkProvider.OnEventShareCallback>(this)
        dynamicLinkProvider.generateDynamicLink(dynamicLinkParam, callback)
    }

    private fun updateTvShowInfo(showModelMinimal: ShowModelMinimal) {
        setTvShowId(showModelMinimal.id)
        val tvShow = TvModel(
            id = showModelMinimal.id,
            title = showModelMinimal.title,
            overview = showModelMinimal.overview,
            backdropPath = showModelMinimal.backdropPath,
            voteAvg = showModelMinimal.voteAvg,
            genres = showModelMinimal.genres
        )
        viewState.tvModel = tvShow
        sendTvSuccessAction()
    }

    /*********** Fetch data from repo ************/

    private fun fetchSimilarTvShows(tvShowId: Long) {
        getAction().value = FetchSimilarTvViewAction(
            ViewState.Loading<List<ShowListModel>>(getShowListLoadingModels())
        )
        val similarSource = detailsRepository.getSimilarTvShows(tvShowId)
        dataState.addSource(similarSource) { similarTvShows ->
            dataState.removeSource(similarSource)
            dataState.value = FetchTvSimilarDataAction(
                dataState = similarTvShows
            )
        }
    }

    private fun fetchRecommendedTvShows(tvShowId: Long) {
        getAction().value = FetchRecommendedTvViewAction(
            ViewState.Loading<List<ShowListModel>>(getShowListLoadingModels())
        )
        val recommendedSource = detailsRepository.getRecommendationTvShows(tvShowId)
        dataState.addSource(recommendedSource) { recommendedMovies ->
            dataState.removeSource(recommendedSource)
            dataState.value = FetchRecommendedTvDataAction(
                dataState = recommendedMovies
            )
        }
    }

    private fun fetchTvShowVideo(tvShowId: Long, seasonNumber: Int) {
        if (viewState.isAlreadyVideoAttempted || seasonNumber == 0) return
        viewState.isAlreadyVideoAttempted = true
        val videoSource = detailsRepository.getTvShowVideo(tvShowId, seasonNumber)
        dataState.addSource(videoSource) { videoKey ->
            dataState.removeSource(videoSource)
            dataState.value = FetchTvVideoDataAction(
                dataState = videoKey
            )
        }
    }

    private fun fetchTvCast(tvShowId: Long) {
        // Checking if the tv model is being fetched from database.
        // if not casting data won't be saved in the database since it performs update operation
        if (viewState.isAlreadyCastAttempted && viewState.tvModel.voteCount == 0) return
        viewState.isAlreadyCastAttempted = true
        val castSource = detailsRepository.getTvShowCast(tvShowId)
        dataState.addSource(castSource) { castCount ->
            dataState.removeSource(castSource)
            dataState.value = FetchTvCastDataAction(
                dataState = castCount
            )
        }
    }

    private fun fetchTvShowDetails(tvShowId: Long) {
        dataState.addSource(
            detailsRepository.getTvShowDetails(tvShowId = tvShowId),
            Observer { dataResponse ->
                dataState.value = FetchTvDetailsDataAction(dataResponse)
            })
    }

    private fun fetchTvShowReviews(tvShowId: Long) {
        dataState.addSource(detailsRepository.getTvShowReviews(tvShowId),
            Observer { reviewList ->
                dataState.value = FetchTvReviewDataAction(reviewList)
            })
    }

    /*********** Send action to view ************/

    fun setDataAction(action: FetchTvDetailsDataAction) {
        when (val dataState = action.dataState) {
            is DataState.Success<TvModel> -> {
                dataState.dataResponse.data?.let { tvShow ->
                    viewState.tvModel = tvShow
                    sendTvSuccessAction()
                }
            }
        }
    }

    fun setDataAction(action: FetchTvSimilarDataAction) {
        when (val dataState = action.dataState) {
            is DataState.Success<List<ShowModelMinimal>> -> {
                viewState.similarTvShows = getMovieListModel(dataState.dataResponse.data)
                sendSimilarSuccessAction(dataState)
            }
            is DataState.Error<List<ShowModelMinimal>> -> {
                sendSimilarErrorAction(dataState.dataResponse)
            }
        }
    }

    fun setDataAction(action: FetchRecommendedTvDataAction) {
        when (val dataState = action.dataState) {
            is DataState.Success<List<ShowModelMinimal>> -> {
                viewState.recommendedTvShows = getMovieListModel(dataState.dataResponse.data)
                sendRecommendationSuccessAction(dataState)
            }
            is DataState.Error<List<ShowModelMinimal>> -> {
                sendRecommendationErrorAction(dataState.dataResponse)
            }
        }
    }

    fun setDataAction(action: FetchTvReviewDataAction) {
        when (val dataState = action.dataState) {
            is DataState.Success<PagedList<ReviewModel>> -> {
                viewState.reviews = dataState.dataResponse.data
                sendReviewSuccessAction(dataState)
            }
        }
    }

    /*********** Send success action to view ************/

    private fun sendRecommendationSuccessAction(dataState: DataState.Success<List<ShowModelMinimal>>) {
        getAction().value = FetchRecommendedTvViewAction(
            ViewState.Success(
                data = viewState.recommendedTvShows,
                message = dataState.dataResponse.message
            )
        )
    }

    private fun sendSimilarSuccessAction(dataState: DataState.Success<List<ShowModelMinimal>>) {
        getAction().value = FetchSimilarTvViewAction(
            ViewState.Success(
                data = viewState.similarTvShows,
                message = dataState.dataResponse.message
            )
        )
    }

    private fun sendTvSuccessAction() {
        getAction().value = FetchTvDetailsViewAction(
            ViewState.Success(viewState.tvModel)
        )
    }

    private fun sendReviewSuccessAction(dataState: DataState.Success<PagedList<ReviewModel>>) {
        getAction().value = FetchTvReviewViewAction(
            ViewState.Success(viewState.reviews)
        )
    }

    override fun onSuccess(shortUrl: String?) {
        getAction().value = ShareTvSeriesViewAction(
            ViewState.Success(
                data = shortUrl
            )
        )
    }

    /*********** Send error action to view ************/

    private fun sendRecommendationErrorAction(dataResponse: DataErrorResponse<List<ShowModelMinimal>>) {
        getAction().value = FetchRecommendedTvViewAction(
            ViewState.Error(
                errorMessage = dataResponse.errorMessage
            )
        )
    }

    private fun sendSimilarErrorAction(dataResponse: DataErrorResponse<List<ShowModelMinimal>>) {
        getAction().value = FetchSimilarTvViewAction(
            ViewState.Error(
                errorMessage = dataResponse.errorMessage
            )
        )
    }

    override fun onFailure() {
        getAction().value = ShareTvSeriesViewAction(
            ViewState.Error()
        )
    }
}