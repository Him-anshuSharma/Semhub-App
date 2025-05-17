package com.himanshu.semhub.data.repository

import com.himanshu.semhub.data.model.DashboardSummary
import com.himanshu.semhub.data.model.GoalStats
import com.himanshu.semhub.data.model.TaskStats
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DashboardRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val taskDao: TaskDao,
    private val subtaskDao: SubtaskDao
) {
    /**
     * Get dashboard summary data including task and goal counts
     */
    fun getDashboardSummary(): Flow<DashboardSummary> = flow {
        val tasks = taskDao.getAllTasks()
        val goals = goalDao.getAllGoals()

        val summary = DashboardSummary(
            taskCount = tasks.size,
            goalCount = goals.size
        )

        emit(summary)
    }.flowOn(Dispatchers.IO)

    /**
     * Get all tasks
     */
    fun getAllTasks() = flow {
        emit(taskDao.getAllTasks())
    }.flowOn(Dispatchers.IO)

    /**
     * Get all goals
     */
    fun getAllGoals() = flow {
        emit(goalDao.getAllGoals())
    }.flowOn(Dispatchers.IO)

    /**
     * Get tasks with their subtasks
     */
    fun getTasksWithSubtasks() = flow {
        emit(taskDao.getTasksWithSubtasks())
    }.flowOn(Dispatchers.IO)

    /**
     * Get goals with their associated tasks
     */
    fun getGoalsWithTasks() = flow {
        emit(goalDao.getGoalsWithTasks())
    }.flowOn(Dispatchers.IO)

    /**
     * Get task completion statistics
     */
    fun getTaskStats() = flow {
        val tasks = taskDao.getAllTasks()
        val completedTasks = tasks.count { it.priority == "COMPLETED" }
        val pendingTasks = tasks.size - completedTasks

        emit(TaskStats(
            total = tasks.size,
            completed = completedTasks,
            pending = pendingTasks,
            completionRate = if (tasks.isNotEmpty()) completedTasks.toFloat() / tasks.size else 0f
        ))
    }.flowOn(Dispatchers.IO)

    /**
     * Get goal completion statistics
     */
    fun getGoalStats() = flow {
        val goals = goalDao.getAllGoals()
        val completedGoals = goals.count { it.type == "COMPLETED" }
        val pendingGoals = goals.size - completedGoals

        emit(GoalStats(
            total = goals.size,
            completed = completedGoals,
            pending = pendingGoals,
            completionRate = if (goals.isNotEmpty()) completedGoals.toFloat() / goals.size else 0f
        ))
    }.flowOn(Dispatchers.IO)
}


