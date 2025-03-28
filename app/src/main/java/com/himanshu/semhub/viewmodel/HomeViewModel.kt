package com.himanshu.semhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.Timetable
import com.himanshu.semhub.data.repository.TimetableRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val timetableRepository: TimetableRepository
) : ViewModel() {

    private val _timetableState = MutableStateFlow<Timetable?>(null)
    val timetableState: StateFlow<Timetable?> = _timetableState.asStateFlow()

    fun getTimeTable(file: File) {
        viewModelScope.launch {
            try {
                val response = timetableRepository.getTimeTable(file)
                if (response.isSuccessful) {
                    _timetableState.value = response.body()
                } else {
                    _timetableState.value = null
                }
            } catch (e: Exception) {
                _timetableState.value = null
            }
        }
    }
}
