package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.local.dao.GoalDao
import com.himanshu.semhub.data.local.dao.GoalTaskCrossRefDao
import com.himanshu.semhub.data.local.dao.TaskDao
import com.himanshu.semhub.data.mapper.GoalMapper
import com.himanshu.semhub.data.mapper.TaskMapper
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GoalsTasksRepository @Inject constructor(
    val taskDao: TaskDao, val goalDao: GoalDao, val goalTaskCrossRefDao: GoalTaskCrossRefDao
) {

    val Tag = "GoalsTasksRepository"

    suspend fun getTotalGoals(): Int = goalDao.getGoalCount()

    suspend fun getTotalTasks(): Int = taskDao.getTaskCount()

    suspend fun getGoals(): List<Goal> {
        val goals = mutableListOf<Goal>()
        val goalEntities = goalDao.getAllGoals().first()
        for (goalEntity in goalEntities) {
            val taskIds = goalTaskCrossRefDao.getTaskIdsForGoal(goalEntity.id)
            goals.add(GoalMapper.mapFromEntity(goalEntity, taskIds))
        }
        return goals
    }

    suspend fun getTasks(): List<Task> {
        val tasks = mutableListOf<Task>()
        val taskEntities = taskDao.getAllTasks().first()
        for (taskEntity in taskEntities) {
            tasks.add(TaskMapper.mapFromEntity(taskEntity, emptyList()))
        }
        return tasks
    }

    suspend fun deleteTask(taskId:Int){
        taskDao.deleteTaskById(taskId)
    }

    suspend fun deleteGoal(goalId:Int){
        val tasksIds = goalTaskCrossRefDao.getTaskIdsForGoal(goalId)
        Log.d(Tag,tasksIds.toString())
        goalDao.deleteGoalById(goalId)
        tasksIds.map { id->
            taskDao.deleteTaskById(id)
        }
    }

    suspend fun updateTaskStatus(taskId: Int, completed: Boolean) {
        taskDao.updateTaskStatus(taskId)
    }

    suspend fun updateTaskPriority(taskId: Int, priority: String) {
        taskDao.updateTaskPriority(taskId, priority)
    }

    suspend fun updateGoalStatus(goalId: Int, completed: Boolean) {
        goalDao.updateGoalStatus(goalId)
    }

}
