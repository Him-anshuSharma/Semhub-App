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

    private val _timetableState = MutableStateFlow<TimetableState>(TimetableState.Idle)
    val timetableState: StateFlow<TimetableState> = _timetableState.asStateFlow()

    private var timetable: Timetable? = null  // Cached timetable to avoid redundant calls

    init {
        ifTimeTableExists() // Load timetable when ViewModel is created
    }

    fun ifTimeTableExists() {
        if (timetable != null) {
            _timetableState.value = TimetableState.Success  // Avoid unnecessary DB call
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
        if (timetable != null) {  // Skip API call if already fetched
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
                    timetable?.let {
                        Log.d(TAG, "Saving timetable")
                        timetableRepository.saveTimeTable(it)
                    }
                    _timetableState.value = TimetableState.Success
                } else {
                    handleError(response.errorBody()?.string())
                }
            } catch (e: Exception) {
                handleError(e.message)
            }
        }
    }

    fun resetState() {
        _timetableState.value = TimetableState.Idle
        ifTimeTableExists() // Recheck if timetable exists after resetting state
    }

    fun getTimeTableDayWise(day: String): List<SubjectSchedule>? {
        return timetable?.getScheduleForDay(day)
    }

    private fun Timetable.getScheduleForDay(day: String): List<SubjectSchedule> {
        val schedule = when (day.lowercase()) {
            "monday" -> Monday
            "tuesday" -> Tuesday
            "wednesday" -> Wednesday
            "thursday" -> Thursday
            "friday" -> Friday
            "saturday" -> Saturday
            "sunday" -> Sunday
            else -> emptyList()
        }
        return schedule.map { SubjectSchedule(it[0], it[1]) }
    }

    private fun handleError(message: String?) {
        val errorMsg = message ?: "Something went wrong"
        Log.e(TAG, "Error: $errorMsg")
        _timetableState.value = TimetableState.Error(errorMsg)
    }

    companion object {
        private const val TAG = "TimetableViewModel"
    }
}


sealed class TimetableState {
    data object Idle : TimetableState()
    data object Loading : TimetableState()
    data object Success : TimetableState()
    data class Error(val message: String = "Something went wrong") : TimetableState()
}
