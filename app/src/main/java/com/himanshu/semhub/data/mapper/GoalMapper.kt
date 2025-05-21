package com.himanshu.semhub.data.mapper

import com.himanshu.semhub.data.local.entities.GoalEntity
import com.himanshu.semhub.data.model.Goal

object GoalMapper {
    fun mapToEntity(goal: Goal): GoalEntity {
        return GoalEntity(
            id = goal.id ?: 0,
            name = goal.name,
            type = goal.type,
            targetDate = goal.targetDate
        )
    }

    fun mapFromEntity(entity: GoalEntity, targetTasks: List<Int>): Goal {
        return Goal(
            id = entity.id,
            name = entity.name,
            type = entity.type,
            targetDate = entity.targetDate,
            targetTasks = targetTasks.map { taskId->
                taskId.toString()
            }
        )
    }
}
