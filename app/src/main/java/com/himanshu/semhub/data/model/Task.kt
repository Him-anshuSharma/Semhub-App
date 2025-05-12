package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val title: String,
    val type: String,
    val subject: String,
    val deadline: String? = null,
    val priority: String,
    @SerializedName("estimated_hours") val estimatedHours: Float? = null,
    val subtasks: List<Subtask>? = null
)
