package com.himanshu.semhub.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.himanshu.semhub.data.model.Subtask

@Dao
interface SubtaskDao {
    @Query("SELECT * FROM subtasks")
    fun getAllSubtasks(): List<Subtask>

    @Query("SELECT * FROM subtasks WHERE taskId = :taskId")
    fun getSubtasksByTaskId(taskId: Long): List<Subtask>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(subtask: Subtask): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(subtasks: List<Subtask>)

    @Update
    fun update(subtask: Subtask)

    @Delete
    fun delete(subtask: Subtask)

    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    fun deleteByTaskId(taskId: Long)
}
