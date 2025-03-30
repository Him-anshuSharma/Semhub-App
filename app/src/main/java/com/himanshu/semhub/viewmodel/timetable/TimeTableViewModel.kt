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
import java.sql.Time
import javax.inject.Inject
@HiltViewModel
class TimeTableViewModel @Inject constructor(
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    var timetable: Timetable? = null

    private val _timetableState = MutableStateFlow<TimetableState?>(TimetableState.Idle)
    val timetableState: StateFlow<TimetableState?> = _timetableState.asStateFlow()

    fun getTimeTable(file: File) {
        _timetableState.value = TimetableState.Loading
        viewModelScope.launch {
            try {
                Log.d(TAG, "Uploading file: ${file.name}")  // Debugging
                val response = timetableRepository.getTimeTable(file)
                if (response.isSuccessful) {
                    timetable = response.body()
                    _timetableState.value = TimetableState.Success
                    Log.d(TAG, "Response Success: ${response.body()}")
                } else {
                    Log.e(TAG, "Response Error: ${response.errorBody()?.string()}")
                    _timetableState.value = TimetableState.Error(response.errorBody().toString())
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception: ${e.message}", e)
                _timetableState.value = TimetableState.Error(e.message.toString())
            }
        }
    }

    fun resetState(){
        _timetableState.value = TimetableState.Idle
    }

    fun getTimeTableDayWise(day:String):List<SubjectSchedule>?{

        return if(timetableState.value!=null)
            timetable?.getDaySchedule(day.lowercase())
        else
            null
    }

    companion object{
        const val TAG = "HomeViewModel"
    }

}

sealed class TimetableState {
    data object Idle : TimetableState()
    data object Loading : TimetableState()
    data object Success : TimetableState()
    data class Error(val message: String = "Something went wrong") : TimetableState()
}