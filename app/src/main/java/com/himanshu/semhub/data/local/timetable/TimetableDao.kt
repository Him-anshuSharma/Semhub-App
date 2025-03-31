package com.himanshu.semhub.data.local.timetable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.himanshu.semhub.data.model.timetable.Timetable

@Dao
interface TimetableDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimetable(timetable: Timetable)

    @Query("SELECT * FROM timetable_semhub WHERE id = 1")
    suspend fun getTimeTable(): Timetable?

    @Query("DELETE FROM timetable_semhub")
    suspend fun clearTimetable()
}