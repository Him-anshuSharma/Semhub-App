package com.himanshu.semhub.data.model.timetable

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.himanshu.semhub.data.local.timetable.converters.TimetableConverter

@Entity(tableName = "timetable_semhub")
@TypeConverters(TimetableConverter::class)
data class Timetable(
    @PrimaryKey
    val id: Int = 1,
    val Monday: List<List<String>>,
    val Tuesday: List<List<String>>,
    val Wednesday: List<List<String>>,
    val Thursday: List<List<String>>,
    val Friday: List<List<String>>,
    val Saturday: List<List<String>>,
    val Sunday: List<List<String>>
)

data class SubjectSchedule(
    val time: String,
    val subject: String
)
