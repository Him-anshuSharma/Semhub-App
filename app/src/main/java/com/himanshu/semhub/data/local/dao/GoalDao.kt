package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.himanshu.semhub.data.local.relations.GoalTaskCrossRef
import com.himanshu.semhub.data.local.relations.GoalWithTasks
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Task

@Dao
interface GoalDao {
    @Query("SELECT * FROM goals")
    fun getAllGoals(): List<Goal>

    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalById(goalId: Long): Goal?

    @Transaction
    @Query("SELECT * FROM goals")
    fun getGoalsWithTasks(): List<GoalWithTasks>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId")
    fun getGoalWithTasks(goalId: Long): GoalWithTasks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(goal: Goal): Long

    @Update
    fun update(goal: Goal)

    @Delete
    fun delete(goal: Goal)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGoalTaskCrossRef(crossRef: GoalTaskCrossRef)
}

