package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class Subtask(
    val id: Int?,
    val title: String,
    @SerializedName("task_id")
    val taskId:Int,
    @SerializedName("estimated_hours")
    val estimatedHours: String
)
