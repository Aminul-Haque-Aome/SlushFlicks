package com.sifat.slushflicks.ui.details

import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import com.google.android.material.appbar.AppBarLayout
import com.sifat.slushflicks.R
import com.sifat.slushflicks.databinding.ActivityMovieDetailsBinding
import com.sifat.slushflicks.model.MovieModel
import com.sifat.slushflicks.model.ReviewModel
import com.sifat.slushflicks.model.ShowModelMinimal
import com.sifat.slushflicks.ui.details.adapter.viewholder.ShowViewHolder
import com.sifat.slushflicks.ui.details.state.dataaction.MovieDetailDataAction.*
import com.sifat.slushflicks.ui.details.state.event.MovieDetailsViewEvent.*
import com.sifat.slushflicks.ui.details.state.viewaction.MovieDetailsViewAction.*
import com.sifat.slushflicks.ui.details.viewmodel.MovieDetailsViewModel
import com.sifat.slushflicks.ui.home.adapter.model.ShowListModel
import com.sifat.slushflicks.ui.state.ViewState
import com.sifat.slushflicks.utils.INVALID_ID
import com.sifat.slushflicks.utils.showToast


class MovieDetailsActivity :
    BaseDetailsActivity<ActivityMovieDetailsBinding, MovieDetailsViewModel>(),
    View.OnClickListener, ShowViewHolder.OnShowClickListener, AppBarLayout.OnOffsetChangedListener {
    private val TAG = "DetailsActivity"
    override fun getLayoutRes() = R.layout.activity_movie_details

    override fun getViewModelClass() = MovieDetailsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        extractData()
        setupToolbar(binding.toolbarDetails)
        initVariable()
        initListener()
        setupList()
        subscribeAction()
        fetchMovieDetails()
    }

    private fun extractData() {
        val movieId = intent.getLongExtra(KEY_SHOW_ID, INVALID_ID.toLong())
        viewModel.setMovieId(movieId)
    }

    private fun setupList() {
        binding.rvCast.adapter = castAdapter
        binding.rvRecommended.adapter = recommendedAdapter
        binding.rvSimilar.adapter = similarAdapter
        binding.rvReview.adapter = reviewAdapter
    }

    private fun initListener() {
        binding.ivPoster.setOnClickListener(this)
        binding.appBarPoster.addOnOffsetChangedListener(this)
        recommendedAdapter.onShowClickedListener = this
        similarAdapter.onShowClickedListener = this
    }

    private fun subscribeAction() {
        viewModel.observeViewAction().observe(this, Observer { action ->
            when (action) {
                is FetchMovieDetailsViewAction -> {
                    showMovieDetails(action)
                }
                is FetchRecommendedMovieViewAction -> {
                    showRecommendedMovies(action)
                }
                is FetchSimilarMovieViewAction -> {
                    showSimilarMovies(action)
                }
                is FetchMovieReviewViewAction -> {
                    showReviews(action)
                }
            }
        })

        viewModel.observeDataAction().observe(this, Observer { action ->
            when (action) {
                is FetchMovieDetailsDataAction -> {
                    viewModel.setDataAction(action)
                }
                is FetchMovieSimilarDataAction -> {
                    viewModel.setDataAction(action)
                }
                is FetchMovieRecommendationDataAction -> {
                    viewModel.setDataAction(action)
                }
                is FetchMovieReviewDataAction -> {
                    viewModel.setDataAction(action)
                }
            }
        })
    }

    private fun fetchMovieDetails() {
        viewModel.handleEvent(FetchMovieDetailsViewEvent())
        viewModel.handleEvent(FetchRecommendedMovieViewEvent())
        viewModel.handleEvent(FetchSimilarMovieViewEvent())
        viewModel.handleEvent(FetchMovieReviewsViewEvent())
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.ivPoster.id -> {
                showTrailer(binding.model?.video)
            }
        }
    }

    override fun onShowClicked(showModelMinimal: ShowModelMinimal) {
        refreshWith(showModelMinimal)
    }

    private fun refreshWith(model: ShowModelMinimal) {
        updateMovieDetails(model)
        fetchMovieDetails()
        binding.nsvContent.smoothScrollTo(0, 0)
        binding.appBarPoster.setExpanded(true, true)
    }

    private fun updateMovieDetails(model: ShowModelMinimal) {
        viewModel.handleEvent(UpdateMovieViewEvent(model))
    }

    private fun showMovieDetails(action: FetchMovieDetailsViewAction) {
        when (val viewState = action.viewState) {
            is ViewState.Success<MovieModel> -> {
                binding.model = viewState.data
                castAdapter.submitList(viewState.data?.casts?.toMutableList())
                checkMissingData(viewState.data)
            }
        }
    }

    private fun showRecommendedMovies(action: FetchRecommendedMovieViewAction) {
        when (val viewState = action.viewState) {
            is ViewState.Loading<List<ShowListModel>> -> {
                recommendedAdapter.submitList(viewState.data)
            }
            is ViewState.Success<List<ShowListModel>> -> {
                recommendedAdapter.submitList(viewState.data)
                if (viewState.data.isNullOrEmpty()) hideRecommendedSection()
            }
            is ViewState.Error<List<ShowListModel>> -> {
                showToast(viewState.errorMessage ?: getString(R.string.similar_error_message))
                hideRecommendedSection()
            }
        }
    }

    private fun showSimilarMovies(action: FetchSimilarMovieViewAction) {
        when (val viewState = action.viewState) {
            is ViewState.Loading<List<ShowListModel>> -> {
                similarAdapter.submitList(viewState.data)
            }
            is ViewState.Success<List<ShowListModel>> -> {
                similarAdapter.submitList(viewState.data)
                if (viewState.data.isNullOrEmpty()) hideSimilarSection()
            }
            is ViewState.Error<List<ShowListModel>> -> {
                showToast(viewState.errorMessage ?: getString(R.string.similar_error_message))
                hideSimilarSection()
            }
        }
    }

    private fun showReviews(action: FetchMovieReviewViewAction) {
        when (val viewState = action.viewState) {
            is ViewState.Success<PagedList<ReviewModel>> -> {
                reviewAdapter.submitList(viewState.data)
                if (viewState.data.isNullOrEmpty()) hideReviewList()
            }
        }
    }

    private fun checkMissingData(data: MovieModel?) {
        data?.run {
            if (video.isEmpty()) viewModel.handleEvent(FetchMovieVideoViewEvent())
            if (casts.isEmpty()) viewModel.handleEvent(FetchMovieCastViewEvent())
        }
    }

    private fun hideRecommendedSection() {
        binding.tvTitleRecommended.visibility = GONE
        binding.rvRecommended.visibility = GONE
    }

    private fun hideSimilarSection() {
        binding.tvTitleSimilar.visibility = GONE
        binding.rvSimilar.visibility = GONE
    }

    private fun hideReviewList() {
        //binding.rvReview.visibility = INVISIBLE
    }

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        val scrollRange = appBarLayout?.totalScrollRange ?: 0
        binding.shadow.visibility = if ((scrollRange + verticalOffset) == 0) VISIBLE else GONE
    }
}