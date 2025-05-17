package com.himanshu.semhub.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.himanshu.semhub.data.local.dao.GoalDao
import com.himanshu.semhub.data.local.dao.TaskDao
import com.himanshu.semhub.data.local.entities.GoalEntity
import com.himanshu.semhub.data.local.entities.GoalTaskCrossRef
import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.local.entities.TaskEntity

@Entity
data class SampleEntity(
    @PrimaryKey val id: Int,
    val name: String
)

@Database(
    entities = [
        TaskEntity::class,
        SubtaskEntity::class,
        GoalEntity::class,
        GoalTaskCrossRef::class
    ],
    version = 6
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun taskDao(): TaskDao
    abstract fun goalDao(): GoalDao
}