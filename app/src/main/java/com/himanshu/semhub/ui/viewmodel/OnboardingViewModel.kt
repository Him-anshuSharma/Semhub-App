package com.himanshu.semhub.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.himanshu.semhub.data.model.Onboarding
import com.himanshu.semhub.data.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository
) : ViewModel() {

    private val _onboardingState = MutableLiveData<OnboardingState>()
    val onboardingState: LiveData<OnboardingState> = _onboardingState

    private val _selectedImages = MutableLiveData<List<Uri>>(emptyList())
    val selectedImages: LiveData<List<Uri>> = _selectedImages

    private val _selectedAudios = MutableLiveData<List<Uri>>(emptyList())
    val selectedAudios: LiveData<List<Uri>> = _selectedAudios

    fun addImage(uri: Uri) {
        val currentList = _selectedImages.value ?: emptyList()
        _selectedImages.value = currentList + uri
    }

    fun removeImage(uri: Uri) {
        val currentList = _selectedImages.value ?: emptyList()
        _selectedImages.value = currentList.filter { it != uri }
    }

    fun addAudio(uri: Uri) {
        val currentList = _selectedAudios.value ?: emptyList()
        _selectedAudios.value = currentList + uri
    }

    fun removeAudio(uri: Uri) {
        val currentList = _selectedAudios.value ?: emptyList()
        _selectedAudios.value = currentList.filter { it != uri }
    }

    fun clearSelections() {
        _selectedImages.value = emptyList()
        _selectedAudios.value = emptyList()
    }

    fun submitOnboarding(token: String) {
        val images = _selectedImages.value ?: emptyList()
        if (images.isEmpty()) {
            _onboardingState.value = OnboardingState.Error("Please select at least one image")
            return
        }

        _onboardingState.value = OnboardingState.Loading

        viewModelScope.launch {
            val audios = _selectedAudios.value?.takeIf { it.isNotEmpty() }
            onboardingRepository.onboardUser(token, images, audios)
                .onSuccess { response ->
                    _onboardingState.value = OnboardingState.Success(response)
                }
                .onFailure { error ->
                    _onboardingState.value = OnboardingState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    sealed class OnboardingState {
        object Initial : OnboardingState()
        object Loading : OnboardingState()
        data class Success(val data: Onboarding) : OnboardingState()
        data class Error(val message: String) : OnboardingState()
    }
}
