package com.himanshu.semhub.data.model.timetable

data class Timetable(
    val Monday: List<List<String>>,
    val Tuesday: List<List<String>>,
    val Wednesday: List<List<String>>,
    val Thursday: List<List<String>>,
    val Friday: List<List<String>>,
    val Saturday: List<List<String>>,
    val Sunday: List<List<String>>
) {
    fun getDaySchedule(day: String): List<SubjectSchedule> {
        val daySchedule = when (day.lowercase()) {
            "monday" -> Monday
            "tuesday" -> Tuesday
            "wednesday" -> Wednesday
            "thursday" -> Thursday
            "friday" -> Friday
            "saturday" -> Saturday
            "sunday" -> Sunday
            else -> emptyList()
        }
        return daySchedule.map { SubjectSchedule(it[0], it[1]) } // Convert manually
    }
}

data class SubjectSchedule(
    val time: String,
    val subject: String
)
