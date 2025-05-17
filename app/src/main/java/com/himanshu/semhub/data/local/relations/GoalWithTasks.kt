package com.himanshu.semhub.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.himanshu.semhub.data.local.entities.GoalEntity
import com.himanshu.semhub.data.local.entities.GoalTaskCrossRef
import com.himanshu.semhub.data.local.entities.TaskEntity

data class GoalWithTasks(
    @Embedded val goal: GoalEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GoalTaskCrossRef::class,
            parentColumn = "goalId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<TaskEntity>
)