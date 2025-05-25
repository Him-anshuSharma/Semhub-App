package com.himanshu.semhub.data.local

//write all imports

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.himanshu.semhub.data.local.dao.GoalDao
import com.himanshu.semhub.data.local.dao.GoalTaskCrossRefDao
import com.himanshu.semhub.data.local.dao.SubtaskDao
import com.himanshu.semhub.data.local.dao.TaskDao
import com.himanshu.semhub.data.local.entities.GoalEntity
import com.himanshu.semhub.data.local.entities.SubtaskEntity
import com.himanshu.semhub.data.local.entities.TaskEntity
import com.himanshu.semhub.data.local.relationships.GoalTaskCrossRef
import com.himanshu.semhub.data.local.converter.SubtaskConverter

@Database(
    entities = [TaskEntity::class, SubtaskEntity::class, GoalEntity::class, GoalTaskCrossRef::class],
    version = 11,
    exportSchema = false
)
@TypeConverters(SubtaskConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun goalDao(): GoalDao
    abstract fun goalTaskCrossRefDao(): GoalTaskCrossRefDao
}
