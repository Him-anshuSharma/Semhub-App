package com.himanshu.semhub.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val type: String,
    val subject: String,
    val priority: String?,
    val deadline: String?,
    @ColumnInfo(name = "estimated_hours")
    val estimatedHours: String?,
){


    // Ignore subtasks for Room storage - we'll handle them separately
    @Ignore
    var subtasks: List<SubtaskEntity>? = emptyList()
}