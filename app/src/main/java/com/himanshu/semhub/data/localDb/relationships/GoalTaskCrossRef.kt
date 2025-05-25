package com.himanshu.semhub.data.localDb.relationships

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import com.himanshu.semhub.data.localDb.entities.GoalEntity
import com.himanshu.semhub.data.localDb.entities.TaskEntity

@Entity(
    tableName = "goal_task_cross_ref",
    primaryKeys = ["goalId", "taskId"],
    foreignKeys = [
        ForeignKey(
            entity = GoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = TaskEntity::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId"), Index("taskId")]
)
data class GoalTaskCrossRef(
    val goalId: Int,
    val taskId: Int
)