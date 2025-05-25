package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.himanshu.semhub.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Int)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()

    @Query("SELECT * FROM tasks ORDER BY id DESC LIMIT :limit")
    fun getRecentTasks(limit: Int): Flow<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTaskCount(): Int

    // In TaskDao
    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: Int)

    @Query("UPDATE tasks SET priority = :priority WHERE id = :taskId")
    suspend fun updateTaskPriority(taskId: Int, priority: String)

}