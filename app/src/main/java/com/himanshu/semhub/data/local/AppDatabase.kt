package com.himanshu.semhub.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import com.himanshu.semhub.data.local.dao.GoalDao
import com.himanshu.semhub.data.local.dao.SubtaskDao
import com.himanshu.semhub.data.local.dao.TaskDao
import com.himanshu.semhub.data.local.relations.GoalTaskCrossRef
import com.himanshu.semhub.data.model.Goal
import com.himanshu.semhub.data.model.Subtask
import com.himanshu.semhub.data.model.Task
import com.himanshu.semhub.data.model.User

@Entity
data class SampleEntity(
    @PrimaryKey val id: Int,
    val name: String
)

@Database(
    entities = [
        Task::class,
        Subtask::class,
        Goal::class,
        GoalTaskCrossRef::class
    ],
    version = 5
)
abstract class AppDatabase: RoomDatabase(){
    abstract fun taskDao(): TaskDao
    abstract fun subtaskDao(): SubtaskDao
    abstract fun goalDao(): GoalDao
}