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

