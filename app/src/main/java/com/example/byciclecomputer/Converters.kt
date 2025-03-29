package com.example.byciclecomputer

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromWeek(week: Week): String {
        return gson.toJson(week)
    }

    @TypeConverter
    fun toWeek(data: String): Week {
        val type = object : TypeToken<Week>() {}.type
        return gson.fromJson(data, type)
    }
}

