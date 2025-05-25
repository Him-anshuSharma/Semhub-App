package com.himanshu.semhub.data.repository

import android.util.Log
import com.himanshu.semhub.data.local.dao.GoalDao
import com.himanshu.semhub.data.local.dao.GoalTaskCrossRefDao
import com.himanshu.semhub.data.local.dao.SubtaskDao
import com.himanshu.semhub.data.local.dao.TaskDao
import com.himanshu.semhub.data.mapper.GoalMapper
import com.himanshu.semhub.data.mapper.TaskMapper
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.remote.ApiService
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class UserDataRepository @Inject constructor(
    val taskDao: TaskDao,
    val goalDao: GoalDao,
    val goalTaskCrossRefDao: GoalTaskCrossRefDao,
    val subtaskDao: SubtaskDao,
    val apiService: ApiService
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
            val subtasks = subtaskDao.getSubtasksForTask(taskEntity.id).first()
            tasks.add(TaskMapper.mapFromEntity(taskEntity, subtasks))
        }
        return tasks
    }

    suspend fun getTaskById(taskId: Int): Task? {
        val taskEntity = taskDao.getTaskById(taskId)
        return if (taskEntity != null) {
            val subtasks = subtaskDao.getSubtasksForTask(taskId).first()
            TaskMapper.mapFromEntity(taskEntity, subtasks)
        } else {
            null
        }
    }

    suspend fun getGoalById(goalId: Int): Goal? {
        val goalEntity = goalDao.getGoalById(goalId)
        return if (goalEntity != null) {
            val taskIds = goalTaskCrossRefDao.getTaskIdsForGoal(goalId)
            GoalMapper.mapFromEntity(goalEntity, taskIds)
        } else {
            null
        }
    }

    suspend fun deleteTask(taskId: Int, authHeader: String) {
        apiService.deleteTask(taskId, authHeader)
        taskDao.deleteTaskById(taskId)
    }

    suspend fun deleteGoal(goalId: Int, authHeader: String) {
        apiService.deleteGoal(goalId, authHeader)
        val tasksIds = goalTaskCrossRefDao.getTaskIdsForGoal(goalId)
        Log.d(Tag, tasksIds.toString())
        goalDao.deleteGoalById(goalId)
        tasksIds.map { id ->
            taskDao.deleteTaskById(id)
        }
    }

    suspend fun updateTaskStatus(taskId: Int) {
        taskDao.updateTaskStatus(taskId)
    }

    suspend fun updateTaskPriority(taskId: Int, priority: String) {
        taskDao.updateTaskPriority(taskId, priority)
    }

    suspend fun updateGoalStatus(goalId: Int) {
        goalDao.updateGoalStatus(goalId)
    }
}
