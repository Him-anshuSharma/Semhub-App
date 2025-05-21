package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class Task(
    val id: Int?,
    val title: String,
    val type: String,
    val subject: String,
    val priority: String?,
    val deadline: String?,
    @SerializedName("estimated_hours")
    val estimatedHours: Int?,
    val subtasks: List<Subtask>
)