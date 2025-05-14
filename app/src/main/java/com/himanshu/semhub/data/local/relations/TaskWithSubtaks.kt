package com.himanshu.semhub.data.local.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.himanshu.semhub.data.model.Subtask
import com.himanshu.semhub.data.model.Task

data class TaskWithSubtasks(
    @Embedded val task: Task,
    @Relation(
        parentColumn = "id",
        entityColumn = "taskId"
    )
    val subtasks: List<Subtask>
)
