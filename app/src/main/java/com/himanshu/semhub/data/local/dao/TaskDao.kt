package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.himanshu.semhub.data.local.relations.TaskWithSubtasks
import com.himanshu.semhub.data.model.Task

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Task?

    @Transaction
    @Query("SELECT * FROM tasks")
    fun getTasksWithSubtasks(): List<TaskWithSubtasks>

    @Query("SELECT * FROM tasks WHERE title = :title LIMIT 1")
    suspend fun findTaskByTitle(title: String): Task?

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskWithSubtasks(taskId: Long): TaskWithSubtasks?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(task: Task): Long

    @Update
    fun update(task: Task)

    @Delete
    fun delete(task: Task)
}
