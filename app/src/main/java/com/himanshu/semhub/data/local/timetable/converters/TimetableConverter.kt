package com.himanshu.semhub.data.local.timetable.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TimetableConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromList(list: List<List<String>>): String {
        return gson.toJson(list)

    }

    @TypeConverter
    fun toList(json: String): List<List<String>> {
        val type = object : TypeToken<List<List<String>>>() {}.type
        println(type)
        return gson.fromJson(json, type)

    }
}