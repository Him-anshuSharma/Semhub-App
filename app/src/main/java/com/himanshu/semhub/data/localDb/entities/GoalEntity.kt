package com.himanshu.semhub.data.localDb.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Goal.kt
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val type: String,
    @ColumnInfo(name = "target_date")
    val targetDate: String?
)