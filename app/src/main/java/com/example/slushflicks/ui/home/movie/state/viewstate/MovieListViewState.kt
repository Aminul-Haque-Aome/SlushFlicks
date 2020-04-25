package com.example.slushflicks.ui.home.movie.state.viewstate

import androidx.lifecycle.MutableLiveData
import com.example.slushflicks.ui.base.BaseViewState
import com.example.slushflicks.ui.home.adapter.model.MovieListModel
import com.example.slushflicks.ui.home.movie.state.MovieListViewAction

class MovieListViewState : BaseViewState<MovieListViewAction>() {
    var movieList: List<MovieListModel>? = null
}