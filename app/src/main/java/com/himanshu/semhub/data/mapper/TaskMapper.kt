package com.himanshu.semhub.data.mapper

import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.local.entities.TaskEntity
import com.himanshu.semhub.data.model.Task

object TaskMapper {
    fun mapToEntity(task: Task): TaskEntity {
        return TaskEntity(
            id = task.id ?: 0,
            title = task.title,
            type = task.type,
            subject = task.subject,
            priority = task.priority,
            deadline = task.deadline,
            estimatedHours = task.estimatedHours
        )
    }

    fun mapFromEntity(entity: TaskEntity, subtasks: List<SubtaskEntity>): Task {
        return Task(
            id = entity.id,
            title = entity.title,
            type = entity.type,
            subject = entity.subject,
            priority = entity.priority,
            deadline = entity.deadline,
            estimatedHours = entity.estimatedHours,
            subtasks = subtasks.map { subtask->
                SubtaskMapper.mapFromEntity(subtask)
            }
        )
    }
}
