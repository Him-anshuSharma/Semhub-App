package com.himanshu.semhub.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: String,
    @SerializedName("target_date")
    @ColumnInfo(name = "target_date") val targetDate: String? = null,
){
    @Ignore
    @SerializedName("target_tasks") val targetTasks: List<Task>? = null
}
