package com.himanshu.semhub.ui.viewmodel.timetable

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.timetable.SubjectSchedule
import com.himanshu.semhub.data.model.timetable.Timetable
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

    private val _timetableState = MutableStateFlow<TimetableState?>(TimetableState.Idle)
    val timetableState: StateFlow<TimetableState?> = _timetableState.asStateFlow()


    fun ifTimeTableExists() {
        _timetableState.value = TimetableState.Loading
        if (timetable != null) {  // Prevent redundant API call
            _timetableState.value = TimetableState.Success
            return
        }

        _timetableState.value = TimetableState.Loading
        viewModelScope.launch {
            timetable = timetableRepository.ifTimeTableExists()
            Log.d(TAG, "Timetable Exists: $timetable")
            _timetableState.value = if (timetable != null) TimetableState.Success else TimetableState.Idle
        }
    }

    fun getTimeTable(file: File) {
        if (timetable != null) {  // If timetable already exists, avoid API call
            Log.d(TAG, "Timetable already exists, skipping API call.")
            return
        }

        _timetableState.value = TimetableState.Loading
        viewModelScope.launch {
            try {
                Log.d(TAG, "Uploading file: ${file.name}")
                val response = timetableRepository.getTimeTable(file)
                if (response.isSuccessful) {
                    timetable = response.body()
                    Log.d(TAG,"Saving timetable")
                    timetable?.let { timetableRepository.saveTimeTable(it) }
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

    fun resetState() {
        _timetableState.value = TimetableState.Idle
        ifTimeTableExists()
    }

    fun getTimeTableDayWise(day: String): List<SubjectSchedule>? {
        val currentTimetable = timetable ?: return null

        val daySchedule: List<List<String>> = when (day.lowercase()) {
            "monday" -> currentTimetable.Monday
            "tuesday" -> currentTimetable.Tuesday
            "wednesday" -> currentTimetable.Wednesday
            "thursday" -> currentTimetable.Thursday
            "friday" -> currentTimetable.Friday
            "saturday" -> currentTimetable.Saturday
            "sunday" -> currentTimetable.Sunday
            else -> emptyList()
        }

        return daySchedule.map { SubjectSchedule(it[0], it[1]) }
    }

    companion object {
        const val TAG = "TimetableViewModel"
        var timetable: Timetable? = null
    }
}

sealed class TimetableState {
    data object Idle : TimetableState()
    data object Loading : TimetableState()
    data object Success : TimetableState()
    data class Error(val message: String = "Something went wrong") : TimetableState()
}
