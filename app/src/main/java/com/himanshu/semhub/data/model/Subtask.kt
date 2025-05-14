package com.himanshu.semhub.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "subtasks")
data class Subtask(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long = 0,
    val title: String,
    @SerializedName("estimated_hours")
    @ColumnInfo(name = "estimated_hours") val estimatedHours: Float? = null
)