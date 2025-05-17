package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class Subtask(
    val id: Int?,
    val title: String,
    @SerializedName("estimated_hours")
    val estimatedHours: String?
)