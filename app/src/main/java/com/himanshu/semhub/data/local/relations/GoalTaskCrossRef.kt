package com.himanshu.semhub.data.local.relations

import androidx.room.Entity

@Entity(primaryKeys = ["goalId", "taskId"])
data class GoalTaskCrossRef(
    val goalId: Long,
    val taskId: Long
)
