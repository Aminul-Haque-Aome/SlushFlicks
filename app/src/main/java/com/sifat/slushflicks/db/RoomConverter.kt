package com.sifat.slushflicks.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sifat.slushflicks.model.GenreModel

object RoomConverter {

    @TypeConverter
    @JvmStatic
    fun genresToString(genres: List<GenreModel>): String {
        return Gson().toJson(genres)
    }

    @TypeConverter
    @JvmStatic
    fun stringToGenres(genres: String?): List<GenreModel> {
        val token = object : TypeToken<List<GenreModel>>() {}.type
        return Gson().fromJson<List<GenreModel>>(genres, token)
    }
}