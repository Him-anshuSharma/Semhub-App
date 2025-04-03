package com.himanshu.semhub.data.model.timetable

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Entity(tableName = "timetable_semhub")
@TypeConverters(TimetableConverter::class)
data class Timetable(
    @PrimaryKey
    val id: Int = 1,
    val days: TimetableDays
)

data class TimetableDays(
    val Monday: List<SubjectSchedule>,
    val Tuesday: List<SubjectSchedule>,
    val Wednesday: List<SubjectSchedule>,
    val Thursday: List<SubjectSchedule>,
    val Friday: List<SubjectSchedule>,
    val Saturday: List<SubjectSchedule>,
    val Sunday: List<SubjectSchedule>
)

data class SubjectSchedule(
    val time: String,
    val subject: String
)

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
