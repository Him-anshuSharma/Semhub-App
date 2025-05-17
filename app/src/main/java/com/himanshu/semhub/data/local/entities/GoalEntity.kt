package com.himanshu.semhub.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Goal Entity
@Entity(tableName = "goals")
data class GoalEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    @ColumnInfo(name = "target_date") val targetDate: String?
)