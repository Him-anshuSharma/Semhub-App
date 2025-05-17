package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.local.entities.TaskEntity

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubtasks(subtasks: List<SubtaskEntity>)

    @Transaction
    @Query("SELECT * FROM tasks")
    suspend fun getTasksWithSubtasks(): List<TaskWithSubtasks>

    @Query("DELETE FROM tasks")
    suspend fun clearAllTasks()
}