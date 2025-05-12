package com.himanshu.semhub.data.local

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase

@Entity
data class SampleEntity(
    @PrimaryKey val id: Int,
    val name: String
)

@Database(entities = [SampleEntity::class], version = 3)
abstract class AppDatabase: RoomDatabase(){
}