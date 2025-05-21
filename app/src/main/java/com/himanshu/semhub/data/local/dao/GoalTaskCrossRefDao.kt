package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.himanshu.semhub.data.local.relationships.GoalTaskCrossRef
import com.himanshu.semhub.data.model.Task
import kotlinx.coroutines.flow.Flow

// GoalTaskCrossRefDao.kt
@Dao
interface GoalTaskCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalTaskCrossRefs(crossRefs: List<GoalTaskCrossRef>)

    @Query("SELECT taskId FROM goal_task_cross_ref WHERE goalId = :goalId")
    suspend fun getTaskIdsForGoal(goalId: Int): List<Int>

    @Query("SELECT * FROM tasks WHERE id IN (SELECT taskId FROM goal_task_cross_ref WHERE goalId = :goalId)")
    fun getTasksForGoal(goalId: Int): Flow<List<Task>>

    @Query("DELETE FROM goal_task_cross_ref WHERE goalId = :goalId")
    suspend fun deleteGoalTaskCrossRefsForGoal(goalId: Int)
}