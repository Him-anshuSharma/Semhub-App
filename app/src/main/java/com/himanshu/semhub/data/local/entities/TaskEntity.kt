package com.himanshu.semhub.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Task Entity
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val type: String,
    val subject: String,
    val deadline: String?,
    val priority: String?,
    @ColumnInfo(name = "estimated_hours") val estimatedHours: String?
)