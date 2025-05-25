package com.himanshu.semhub.data.localDb

//write all imports

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.himanshu.semhub.data.localDb.dao.GoalDao
import com.himanshu.semhub.data.localDb.dao.GoalTaskCrossRefDao
import com.himanshu.semhub.data.localDb.dao.SubtaskDao
import com.himanshu.semhub.data.localDb.dao.TaskDao
import com.himanshu.semhub.data.localDb.entities.GoalEntity
import com.himanshu.semhub.data.localDb.entities.SubtaskEntity
import com.himanshu.semhub.data.localDb.entities.TaskEntity
import com.himanshu.semhub.data.localDb.relationships.GoalTaskCrossRef
import com.himanshu.semhub.data.localDb.converter.SubtaskConverter

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
