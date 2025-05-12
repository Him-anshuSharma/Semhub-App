package com.himanshu.semhub.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.Task
import com.himanshu.semhub.data.model.Onboarding
import com.himanshu.semhub.data.repository.OnboardingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _onboardingState = MutableStateFlow<OnboardingState>(OnboardingState.Initial)
    val onboardingState: StateFlow<OnboardingState> = _onboardingState

    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages

    private val _selectedAudios = MutableStateFlow<List<Uri>>(emptyList())
    val selectedAudios: StateFlow<List<Uri>> = _selectedAudios

    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    private val _userSubject = MutableStateFlow<String>("")
    val userSubject: StateFlow<String> = _userSubject

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token

    init {
        fetchToken()
    }

    private fun fetchToken() {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    // Get token with force refresh = false to use cached token when available
                    user.getIdToken(false).await()?.let { tokenResult ->
                        _token.value = tokenResult.token
                    }
                } else {
                    _onboardingState.value = OnboardingState.Error("User not authenticated")
                }
            } catch (e: Exception) {
                _onboardingState.value = OnboardingState.Error("Failed to get authentication token: ${e.message}")
            }
        }
    }

    fun updateUserName(name: String) {
        _userName.value = name
    }

    fun updateUserSubject(subject: String) {
        _userSubject.value = subject
    }

    fun addImage(uri: Uri) {
        val currentList = _selectedImages.value
        _selectedImages.value = currentList + uri
    }

    fun removeImage(uri: Uri) {
        val currentList = _selectedImages.value
        _selectedImages.value = currentList.filter { it != uri }
    }

    fun addAudio(uri: Uri) {
        val currentList = _selectedAudios.value
        _selectedAudios.value = currentList + uri
    }

    fun removeAudio(uri: Uri) {
        val currentList = _selectedAudios.value
        _selectedAudios.value = currentList.filter { it != uri }
    }

    fun clearSelections() {
        _selectedImages.value = emptyList()
        _selectedAudios.value = emptyList()
    }

    fun submitOnboarding() {
        val images = _selectedImages.value
        if (images.isEmpty()) {
            _onboardingState.value = OnboardingState.Error("Please select at least one image")
            return
        }

        val subject = _userSubject.value
        if (subject.isBlank()) {
            _onboardingState.value = OnboardingState.Error("Please enter your subject")
            return
        }

        val currentToken = _token.value
        if (currentToken == null) {
            refreshToken()
            _onboardingState.value = OnboardingState.Error("Authentication token not available. Please try again.")
            return
        }

        _onboardingState.value = OnboardingState.Loading

        viewModelScope.launch {
            val audios = _selectedAudios.value.takeIf { it.isNotEmpty() }
            onboardingRepository.onboardUser(currentToken, images, audios)
                .onSuccess { response ->
                    _onboardingState.value = OnboardingState.Success(response)
                }
                .onFailure { error ->
                    _onboardingState.value = OnboardingState.Error(error.message ?: "Unknown error occurred")
                }
        }
    }

    fun refreshToken() {
        viewModelScope.launch {
            try {
                val user = firebaseAuth.currentUser
                if (user != null) {
                    // Force refresh the token
                    user.getIdToken(true).await()?.let { tokenResult ->
                        _token.value = tokenResult.token
                    }
                } else {
                    _onboardingState.value = OnboardingState.Error("User not authenticated")
                }
            } catch (e: Exception) {
                _onboardingState.value = OnboardingState.Error("Failed to refresh authentication token: ${e.message}")
            }
        }
    }

    sealed class OnboardingState {
        object Initial : OnboardingState()
        object Loading : OnboardingState()
        data class Success(val data: Onboarding) : OnboardingState()
        data class Error(val message: String) : OnboardingState()
    }

    // Extension function to make Firebase Tasks awaitable in coroutines
    private suspend fun <T> Task<T>.await(): T? {
        return suspendCancellableCoroutine { continuation ->
            addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    continuation.resume(task.result) { }
                } else {
                    continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
                }
            }

            continuation.invokeOnCancellation {
                if (isComplete) return@invokeOnCancellation
                // If needed, you can cancel the task here
            }
        }
    }
}
