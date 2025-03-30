package com.himanshu.semhub.viewmodel.timetable

import com.himanshu.semhub.data.model.timetable.SubjectSchedule
import com.himanshu.semhub.data.model.timetable.Timetable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.repository.TimetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
@HiltViewModel
class TimeTableViewModel @Inject constructor(
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _timetableState = MutableStateFlow<Timetable?>(null)
    val timetableState: StateFlow<Timetable?> = _timetableState.asStateFlow()

    fun getTimeTable(file: File) {
        viewModelScope.launch {
            try {
                Log.d("HomeViewModel", "Uploading file: ${file.name}")  // Debugging
                val response = timetableRepository.getTimeTable(file)
                if (response.isSuccessful) {
                    _timetableState.value = response.body()
                    Log.d("HomeViewModel", "Response Success: ${response.body()}")
                } else {
                    Log.e("HomeViewModel", "Response Error: ${response.errorBody()?.string()}")
                    _timetableState.value = null
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception: ${e.message}", e)
                _timetableState.value = null
            }
        }
    }

    fun getTimeTableDayWise(day:String):List<SubjectSchedule>?{
        if(timetableState.value!=null){
            return timetableState.value?.getDaySchedule(day.lowercase())

        }
        else return null
    }

}
