package com.himanshu.semhub.data.local.relations

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task

data class GoalWithTasks(
    @Embedded val goal: Goal,
    @Relation(
        entity = Task::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = GoalTaskCrossRef::class,
            parentColumn = "goalId",
            entityColumn = "taskId"
        )
    )
    val tasks: List<Task>
)
