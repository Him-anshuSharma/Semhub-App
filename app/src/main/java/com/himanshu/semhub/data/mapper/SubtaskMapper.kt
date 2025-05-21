package com.himanshu.semhub.data.mapper

import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.model.Subtask

object SubtaskMapper {
    fun mapToEntity(subtask: Subtask, taskId: Int): SubtaskEntity {
        return SubtaskEntity(
            subtaskId = subtask.id ?: 0,
            taskId = taskId,
            title = subtask.title,
            completed = subtask.completed ?: false
        )
    }

    fun mapFromEntity(entity: SubtaskEntity): Subtask {
        return Subtask(
            id = entity.subtaskId,
            title = entity.title,
            completed = entity.completed
        )
    }
}
