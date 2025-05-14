package com.himanshu.semhub.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val type: String,
    val subject: String,
    val deadline: String? = null,
    val priority: String,
    @SerializedName("estimated_hours")
    @ColumnInfo(name = "estimated_hours") val estimatedHours: Float? = null,
) {

    @Ignore
    var subtasks: List<Subtask>? = null

}
