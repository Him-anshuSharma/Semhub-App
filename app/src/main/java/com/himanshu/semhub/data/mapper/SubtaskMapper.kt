package com.himanshu.semhub.data.mapper

import com.himanshu.semhub.data.localDb.entities.SubtaskEntity
import com.himanshu.semhub.data.model.Subtask

object SubtaskMapper {
    fun mapToEntity(subtask: Subtask, taskId: Int): SubtaskEntity {
        return SubtaskEntity(
            id = subtask.id ?: 0,
            taskId = taskId,
            title = subtask.title,
            estimatedHours = subtask.estimatedHours ?: "0"
        )
    }

    fun mapFromEntity(entity: SubtaskEntity): Subtask {
        return Subtask(
            id = entity.id,
            title = entity.title,
            taskId = entity.taskId,
            estimatedHours = entity.estimatedHours
        )
    }
}
