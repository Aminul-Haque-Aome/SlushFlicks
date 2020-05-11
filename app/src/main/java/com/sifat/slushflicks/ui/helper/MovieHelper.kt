package com.sifat.slushflicks.ui.helper

import com.sifat.slushflicks.api.home.movie.model.MovieApiModel
import com.sifat.slushflicks.api.home.movie.model.MovieDetailsApiModel
import com.sifat.slushflicks.api.home.movie.model.MovieListApiModel
import com.sifat.slushflicks.model.GenreModel
import com.sifat.slushflicks.model.MovieCollectionModel
import com.sifat.slushflicks.model.MovieModel
import com.sifat.slushflicks.model.ShowModelMinimal
import com.sifat.slushflicks.ui.base.ListViewState.VIEW
import com.sifat.slushflicks.ui.home.adapter.model.ShowListModel
import com.sifat.slushflicks.ui.state.MetaData
import com.sifat.slushflicks.utils.EMPTY_STRING
import com.sifat.slushflicks.utils.PAGE_SIZE

/**
 * This conversion discard unnecessary data returned from api
 * */
fun getMovieList(
    movieApiModels: List<MovieApiModel>?,
    genres: Map<Long, String>
): List<MovieModel> {
    val movieModels = mutableListOf<MovieModel>()
    movieApiModels?.let {
        for (movie in movieApiModels) {
            val genresModels = getGenresModels(movie.genreIds, genres)
            val movieModel = MovieModel(
                id = movie.id,
                backdropPath = movie.backdropPath ?: EMPTY_STRING,
                overview = movie.overview,
                genres = genresModels,
                popularity = movie.popularity,
                posterPath = movie.posterPath ?: EMPTY_STRING,
                releaseData = movie.releaseDate ?: EMPTY_STRING,
                title = movie.title,
                voteAvg = movie.voteAverage,
                voteCount = movie.voteCount
            )
            movieModels.add(movieModel)
        }
    }
    return movieModels
}

/**
 * This conversion converts model to List models for view state
 * */
fun getMovieListModel(shows: List<ShowModelMinimal>?): List<ShowListModel> {
    val movieListModels = mutableListOf<ShowListModel>()
    shows?.let {
        for (movie in shows) {
            val movieListModel = ShowListModel(movie, VIEW)
            movieListModels.add(movieListModel)
        }
    }
    return movieListModels
}

fun getMovieMinimalModel(movies: List<MovieModel>?): List<ShowModelMinimal>? {
    if (movies.isNullOrEmpty()) return null
    val moviesMinimalList = mutableListOf<ShowModelMinimal>()
    for (movie in movies) {
        val movieModelMinimal = ShowModelMinimal(
            id = movie.id,
            overview = movie.overview,
            title = movie.title,
            genres = movie.genres,
            voteAvg = movie.voteAvg,
            backdropPath = movie.backdropPath
        )
        moviesMinimalList.add(movieModelMinimal)
    }
    return moviesMinimalList
}

fun getMovieMinimalApiModels(
    movies: List<MovieApiModel>?,
    genreMap: Map<Long, String>
): List<ShowModelMinimal> {
    if (movies.isNullOrEmpty()) return emptyList()
    val moviesMinimalList = mutableListOf<ShowModelMinimal>()
    for (movie in movies) {
        moviesMinimalList.add(getMovieMinimalApiModel(movie, genreMap))
    }
    return moviesMinimalList
}

fun getMovieMinimalApiModel(
    movie: MovieApiModel,
    genreMap: Map<Long, String>
): ShowModelMinimal {
    val genresModels = getGenresModels(movie.genreIds, genreMap)
    return ShowModelMinimal(
        id = movie.id,
        overview = movie.overview,
        title = movie.title,
        genres = genresModels,
        voteAvg = movie.voteAverage,
        backdropPath = movie.backdropPath ?: EMPTY_STRING
    )
}


fun getGenresModels(
    genreIds: List<Long>,
    genreMap: Map<Long, String>
): List<GenreModel> {
    val genres = mutableListOf<GenreModel>()
    for (id in genreIds) {
        val genre = GenreModel(
            id = id,
            name = genreMap[id] ?: EMPTY_STRING
        )
        genres.add(genre)
    }
    return genres
}

fun getMetaData(movieListApiModel: MovieListApiModel?): MetaData? {
    return movieListApiModel?.let { model ->
        MetaData(
            page = model.page,
            totalResult = model.totalResults,
            totalPage = model.totalPages
        )
    }
}

fun getCollectionModels(
    movies: List<MovieModel>,
    collection: String,
    page: Int
): List<MovieCollectionModel> {
    val collectionList = mutableListOf<MovieCollectionModel>()
    for (index in movies.indices) {
        val collectionModel = MovieCollectionModel(
            collection = collection,
            id = movies[index].id,
            index = ((page - 1) * PAGE_SIZE) + index
        )
        collectionList.add(collectionModel)
    }
    return collectionList
}

fun getMovieDetails(apiModel: MovieDetailsApiModel?): MovieModel? {
    return apiModel?.run {
        MovieModel(
            id = id,
            voteAvg = voteAverage,
            overview = overview,
            voteCount = voteCount,
            backdropPath = backdropPath ?: EMPTY_STRING,
            title = title,
            genres = genres,
            releaseData = releaseDate,
            posterPath = posterPath ?: EMPTY_STRING,
            popularity = popularity,
            budget = budget,
            revenue = revenue,
            tagline = tagline,
            status = status,
            runtime = runtime
        )
    }
}
