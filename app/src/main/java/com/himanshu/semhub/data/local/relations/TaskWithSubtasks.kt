package com.himanshu.semhub.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.local.entities.TaskEntity

data class TaskWithSubtasks(
    @Embedded val task: TaskEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks: List<SubtaskEntity>
)