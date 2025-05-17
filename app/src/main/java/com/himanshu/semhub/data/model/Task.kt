package com.himanshu.semhub.data.model
import com.google.gson.annotations.SerializedName

data class Task(
    val id: Int,
    val title: String,
    val type: String,
    val subject: String,
    val deadline: String?,
    val priority: String?,
    @SerializedName("estimated_hours")
    val estimatedHours: String?,
    val subtasks: List<Subtask> = emptyList()
)


