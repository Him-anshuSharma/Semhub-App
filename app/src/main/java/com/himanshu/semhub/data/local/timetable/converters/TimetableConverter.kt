package com.himanshu.semhub.data.local.timetable.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.himanshu.semhub.data.model.timetable.TimetableDays

class TimetableConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromTimetableDays(days: TimetableDays): String {
        return gson.toJson(days)
    }

    @TypeConverter
    fun toTimetableDays(json: String): TimetableDays {
        val type = object : TypeToken<TimetableDays>() {}.type
        return gson.fromJson(json, type)
    }
}
