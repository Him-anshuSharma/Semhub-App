package com.himanshu.semhub.data.model
import com.google.gson.annotations.SerializedName


data class Goal(
    val id: Int,
    val name: String,
    val type: String,
    @SerializedName("target_tasks")
    val targetTasks: List<GoalTask>,
    @SerializedName("target_date")
    val targetDate: String?
)

data class GoalTask(
    val id: Int,
    val title: String
)

