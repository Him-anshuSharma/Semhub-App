package com.himanshu.semhub.data.localDb.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.himanshu.semhub.data.model.Subtask

class SubtaskConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromSubtaskList(subtasks: List<Subtask>?): String? {
        return if (subtasks == null) null else gson.toJson(subtasks)
    }

    @TypeConverter
    fun toSubtaskList(subtasksString: String?): List<Subtask>? {
        if (subtasksString == null) return null
        val type = object : TypeToken<List<Subtask>>() {}.type
        return gson.fromJson(subtasksString, type)
    }
}

