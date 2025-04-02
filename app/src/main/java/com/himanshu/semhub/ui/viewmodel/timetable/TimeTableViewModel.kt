package com.himanshu.semhub.ui.viewmodel.timetable

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.timetable.SubjectSchedule
import com.himanshu.semhub.data.model.timetable.Timetable
import com.himanshu.semhub.data.repository.AuthRepository
import com.himanshu.semhub.data.repository.TimetableRepository
import com.himanshu.semhub.utils.getCurrentDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
@RequiresApi(Build.VERSION_CODES.O)
class TimeTableViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _selectedDay = MutableStateFlow(getCurrentDay())
    val selectedDay: StateFlow<String> = _selectedDay.asStateFlow()

    private val uid = authRepository.getCurrentUser()?.uid

    private val _timetableState = MutableStateFlow<TimetableState>(TimetableState.Idle)
    val timetableState: StateFlow<TimetableState> = _timetableState.asStateFlow()

    private var timetable: Timetable? = null // Cached timetable

    init {
        Log.d(TAG, "ViewModel initialized")
        loadTimeTableIfExists()
    }

    private fun loadTimeTableIfExists() {
        Log.d(TAG, "Checking if timetable exists")

        if (timetable != null) {
            Log.d(TAG, "Timetable already loaded")
            _timetableState.update { TimetableState.Success }
            return
        }

        _timetableState.update { TimetableState.Loading }
        viewModelScope.launch {
            timetable = timetableRepository.ifTimeTableExists()
            Log.d(TAG, "Timetable fetch result: ${timetable != null}")
            _timetableState.update { if (timetable != null) TimetableState.Success else TimetableState.Idle }
        }
    }

    fun getTimeTable(file: File) {
        Log.d(TAG, "Fetching timetable from file: ${file.name}")

        if (timetable != null) {
            Log.d(TAG, "Timetable already exists, skipping API call.")
            return
        }

        _timetableState.update { TimetableState.Loading }
        viewModelScope.launch {
            try {
                val response = timetableRepository.getTimeTable(file,uid.toString())

                if (response.isSuccessful) {
                    Log.d(TAG, "Timetable fetched successfully, saving...")
                    timetable = response.body()?.also { timetableRepository.saveTimeTable(it) }
                    _timetableState.update { TimetableState.Success }
                } else {
                    handleError(response.errorBody()?.string())
                }
            } catch (e: Exception) {
                handleError(e.localizedMessage)
            }
        }
    }

    fun resetState() {
        Log.d(TAG, "Resetting state to Idle")
        _timetableState.update { TimetableState.Idle }
    }

    fun getTimeTableDayWise(): List<SubjectSchedule>? {
        val schedule = when(selectedDay.value.lowercase()){
            "monday" -> timetable?.days?.Monday
            "tuesday" -> timetable?.days?.Tuesday
            "wednesday" -> timetable?.days?.Wednesday
            "thursday" -> timetable?.days?.Thursday
            "friday" -> timetable?.days?.Friday
            else -> emptyList()
        }
        return schedule
    }

    fun updateSelectedDay(day: String) {
        Log.d(TAG, "Selected day updated: $day")
        _selectedDay.update { day }
    }



    private fun handleError(message: String?) {
        val errorMsg = message ?: "Something went wrong"
        Log.e(TAG, "Error: $errorMsg")
        _timetableState.update { TimetableState.Error(errorMsg) }
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
