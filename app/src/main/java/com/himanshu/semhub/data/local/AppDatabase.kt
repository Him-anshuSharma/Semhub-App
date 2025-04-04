package com.himanshu.semhub.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.himanshu.semhub.data.local.chat.ChatDao
import com.himanshu.semhub.data.local.timetable.TimetableDao
import com.himanshu.semhub.data.model.chat.Chat
import com.himanshu.semhub.data.model.timetable.Timetable

@Database(entities = [Timetable::class, Chat::class], version = 5)
abstract class AppDatabase: RoomDatabase(){
    abstract fun timetableDao(): TimetableDao
    abstract fun chatDao(): ChatDao
}