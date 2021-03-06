package com.sifat.slushflicks.ui.home.tvshow.viewmodel

import com.sifat.slushflicks.di.home.tvshow.TvShowScope
import com.sifat.slushflicks.model.CollectionModel
import com.sifat.slushflicks.repository.tv.TvHomeRepository
import com.sifat.slushflicks.ui.base.BaseActionViewModel
import com.sifat.slushflicks.ui.helper.getCollectionListLoadingModel
import com.sifat.slushflicks.ui.helper.getCollectionListModel
import com.sifat.slushflicks.ui.home.tvshow.state.event.TvHomeEventState
import com.sifat.slushflicks.ui.home.tvshow.state.viewaction.TvHomeViewAction
import com.sifat.slushflicks.ui.home.tvshow.state.viewstate.TvHomeViewState
import com.sifat.slushflicks.ui.state.DataState
import com.sifat.slushflicks.ui.state.ViewState
import javax.inject.Inject

@TvShowScope
class TvShowViewModel @Inject constructor(
    private val repository: TvHomeRepository
) : BaseActionViewModel<TvHomeViewAction, TvHomeViewState>() {
    override val viewState = TvHomeViewState()

    fun handleEvent(homeEvent: TvHomeEventState) {
        when (homeEvent) {
            is TvHomeEventState.TvCollectionEvent -> {
                fetchMovieCollection(homeEvent)
            }
            is TvHomeEventState.TvCollectionClickEvent -> {
                updateCollection(homeEvent)
            }
        }
    }

    private fun updateCollection(homeEvent: TvHomeEventState.TvCollectionClickEvent) {
        if (viewState.selectedIndex != homeEvent.index) {
            viewState.selectedIndex = homeEvent.index
            viewState.updateListSelection()
            sendCollectionSuccessAction()
        }
    }

    private fun fetchMovieCollection(event: TvHomeEventState.TvCollectionEvent) {
        if (!event.forceUpdate) {
            if (!viewState.movieCollectionList.isNullOrEmpty()) {
                sendCollectionSuccessAction()
                return
            }
        }
        sendCollectionLoadingAction()

        dataState.addSource(repository.getTvCollection()) { dataResponse ->
            setTvCollection(dataResponse)
        }
    }

    private fun setTvCollection(dataState: DataState<List<CollectionModel>>) {
        when (dataState) {
            is DataState.Success<List<CollectionModel>> -> {
                dataState.dataResponse.data?.let { collection ->
                    viewState.movieCollectionList = getCollectionListModel(collection)
                    sendCollectionSuccessAction()
                }
            }
            is DataState.Error<List<CollectionModel>> -> {
                sendCollectionErrorAction(dataState)
            }
        }
    }

    private fun sendCollectionLoadingAction() {
        getAction().value = TvHomeViewAction.TvCollectionViewAction(
            viewState = ViewState.Success(getCollectionListLoadingModel())
        )
    }

    private fun sendCollectionSuccessAction() {
        getAction().value = TvHomeViewAction.TvCollectionViewAction(
            viewState = ViewState.Success(viewState.movieCollectionList)
        )
        getAction().value = TvHomeViewAction.CollectionContainerUpdateViewAction(
            viewState = ViewState.Success(viewState.getCurrentLabel())
        )
    }

    private fun sendCollectionErrorAction(dataState: DataState.Error<List<CollectionModel>>) {
        getAction().value = TvHomeViewAction.TvCollectionViewAction(
            ViewState.Error(
                errorMessage = dataState.dataResponse.errorMessage
            )
        )
    }
}