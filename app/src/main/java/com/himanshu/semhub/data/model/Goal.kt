package com.himanshu.semhub.data.model

import com.google.gson.annotations.SerializedName

data class Goal(
    val name: String,
    val type: String,
    @SerializedName("target_tasks") val targetTasks: List<String>,
    @SerializedName("target_date") val targetDate: String? = null
)
