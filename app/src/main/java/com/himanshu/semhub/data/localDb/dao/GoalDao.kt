package com.himanshu.semhub.data.localDb.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.himanshu.semhub.data.localDb.entities.GoalEntity
import kotlinx.coroutines.flow.Flow

// GoalDao.kt
@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): Flow<List<GoalEntity>>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    suspend fun getGoalById(goalId: Int): GoalEntity?

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoals(goals: List<GoalEntity>)

    @Delete
    suspend fun deleteGoal(goal: GoalEntity)

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun getGoalCount(): Int

    @Query("SELECT * FROM goals ORDER BY id DESC LIMIT :limit")
    fun getRecentGoals(limit: Int): Flow<List<GoalEntity>>

    @Query("DELETE FROM goals")
    suspend fun deleteAllGoals()

    // In GoalDao
    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun updateGoalStatus(goalId: Int)
}