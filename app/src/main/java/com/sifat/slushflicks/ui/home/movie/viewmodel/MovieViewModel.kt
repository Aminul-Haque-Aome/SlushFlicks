package com.sifat.slushflicks.ui.home.movie.viewmodel

import com.sifat.slushflicks.di.home.movie.MovieScope
import com.sifat.slushflicks.model.CollectionModel
import com.sifat.slushflicks.repository.movie.MovieHomeRepository
import com.sifat.slushflicks.ui.base.BaseActionViewModel
import com.sifat.slushflicks.ui.helper.getCollectionListLoadingModel
import com.sifat.slushflicks.ui.helper.getCollectionListModel
import com.sifat.slushflicks.ui.home.movie.state.event.MovieHomeEventState
import com.sifat.slushflicks.ui.home.movie.state.event.MovieHomeEventState.MovieCollectionClickEvent
import com.sifat.slushflicks.ui.home.movie.state.event.MovieHomeEventState.MovieCollectionEvent
import com.sifat.slushflicks.ui.home.movie.state.viewaction.MovieHomeViewAction
import com.sifat.slushflicks.ui.home.movie.state.viewstate.MovieHomeViewState
import com.sifat.slushflicks.ui.state.DataState
import com.sifat.slushflicks.ui.state.DataState.Error
import com.sifat.slushflicks.ui.state.ViewState
import javax.inject.Inject

@MovieScope
class MovieViewModel
@Inject constructor(
    private val repository: MovieHomeRepository
) : BaseActionViewModel<MovieHomeViewAction, MovieHomeViewState>() {
    override val viewState by lazy {
        MovieHomeViewState()
    }

    fun handleEvent(homeEvent: MovieHomeEventState) {
        when (homeEvent) {
            is MovieCollectionEvent -> {
                fetchMovieCollection(homeEvent)
            }
            is MovieCollectionClickEvent -> {
                updateCollection(homeEvent)
            }
        }
    }

    private fun updateCollection(homeEvent: MovieCollectionClickEvent) {
        if (viewState.selectedIndex != homeEvent.index) {
            viewState.selectedIndex = homeEvent.index
            viewState.updateListSelection()
            sendCollectionSuccessAction()
        }
    }

    private fun fetchMovieCollection(event: MovieCollectionEvent) {
        if (!event.forceUpdate) {
            if (!viewState.movieCollectionList.isNullOrEmpty()) {
                sendCollectionSuccessAction()
                return
            }
        }
        sendCollectionLoadingAction()

        dataState.addSource(repository.getMovieCollection()) { dataResponse ->
            setMovieCollection(dataResponse)
        }
    }

    private fun setMovieCollection(dataState: DataState<List<CollectionModel>>) {
        when (dataState) {
            is DataState.Success<List<CollectionModel>> -> {
                dataState.dataResponse.data?.let { collection ->
                    viewState.movieCollectionList = getCollectionListModel(collection)
                    sendCollectionSuccessAction()
                }
            }
            is Error<List<CollectionModel>> -> {
                sendCollectionErrorAction(dataState)
            }
        }
    }

    private fun sendCollectionLoadingAction() {
        getAction().value = MovieHomeViewAction.MovieCollectionViewAction(
            viewState = ViewState.Success(getCollectionListLoadingModel())
        )
    }

    private fun sendCollectionSuccessAction() {
        getAction().value = MovieHomeViewAction.MovieCollectionViewAction(
            viewState = ViewState.Success(viewState.movieCollectionList)
        )

        getAction().value = MovieHomeViewAction.CollectionContainerUpdateViewAction(
            viewState = ViewState.Success(viewState.getCurrentLabel())
        )
    }

    private fun sendCollectionErrorAction(dataState: Error<List<CollectionModel>>) {
        getAction().value = MovieHomeViewAction.MovieCollectionViewAction(
            ViewState.Error(
                errorMessage = dataState.dataResponse.errorMessage
            )
        )
    }
}