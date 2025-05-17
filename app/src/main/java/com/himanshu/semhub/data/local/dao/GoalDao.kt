package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.himanshu.semhub.data.local.entities.GoalEntity
import com.himanshu.semhub.data.local.entities.GoalTaskCrossRef

@Dao
interface GoalDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoalTaskCrossRefs(crossRefs: List<GoalTaskCrossRef>)

    @Transaction
    @Query("SELECT * FROM goals")
    suspend fun getGoalsWithTasks(): List<GoalWithTasks>

    @Query("DELETE FROM goals")
    suspend fun clearAllGoals()
}