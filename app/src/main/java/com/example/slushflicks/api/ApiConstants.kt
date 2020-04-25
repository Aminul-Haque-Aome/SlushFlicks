package com.example.slushflicks.api

import com.example.slushflicks.api.ApiEndPoint.Companion.TRENDING_MOVIE_URL
import com.example.slushflicks.api.MethodName.Companion.GET

enum class ImageDimension(val dimension : String) {
    // "w92", "w154", "w185", "w342", "w500", "w780", or "original"
    W92("/w92"),
    W154("/w154"),
    W185("/w185"),
    W342("/w342"),
    W500("/w500"),
    W780("/w780"),
    Original("/original")
}

const val KEY_API_KEY = "api_key"
const val KEY_TIME_WINDOW = "time_window"

class ApiEndPoint {
    companion object {
        const val TRENDING_MOVIE_URL = "/trending/movie/{time_window}"
    }
}

class MethodName {
    companion object {
        const val GET = 1
    }
}

class ApiTags {
    companion object {
        const val TRENDING_API_TAG = TRENDING_MOVIE_URL + GET
    }
}