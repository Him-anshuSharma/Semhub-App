package com.himanshu.semhub.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.himanshu.semhub.data.repository.OnboardingRepository
import com.himanshu.semhub.ui.navigation.Routes
import com.himanshu.semhub.utils.uriToFile  // You'll import this yourself
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val onboardingRepository: OnboardingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val TAG = "OnboardingViewModel"

    // UI state for onboarding
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Initial)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // Message to show in snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // User subject input
    private val _userSubject = MutableStateFlow("")
    val userSubject: StateFlow<String> = _userSubject.asStateFlow()

    // Selected images
    private val _selectedImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedImages: StateFlow<List<Uri>> = _selectedImages.asStateFlow()

    // Selected audio files
    private val _selectedAudios = MutableStateFlow<List<Uri>>(emptyList())
    val selectedAudios: StateFlow<List<Uri>> = _selectedAudios.asStateFlow()

    /**
     * Update user subject input
     */
    fun updateUserSubject(subject: String) {
        _userSubject.value = subject
    }

    /**
     * Add an image to the selected images list
     */
    fun addImage(uri: Uri) {
        _selectedImages.value += uri
    }

    /**
     * Remove an image from the selected images list
     */
    fun removeImage(uri: Uri) {
        _selectedImages.value = _selectedImages.value.filter { it != uri }
    }

    /**
     * Add an audio file to the selected audios list
     */
    fun addAudio(uri: Uri) {
        _selectedAudios.value += uri
    }

    /**
     * Remove an audio file from the selected audios list
     */
    fun removeAudio(uri: Uri) {
        _selectedAudios.value = _selectedAudios.value.filter { it != uri }
    }

    /**
     * Prepare and submit the onboarding data
     */
    fun prepareAndSubmitOnboarding(navController: NavController) {
        if (_selectedImages.value.isEmpty()) {
            _snackbarMessage.value = "Please select at least one image"
            return
        }

        if (_userSubject.value.isBlank()) {
            _snackbarMessage.value = "Please enter your subject"
            return
        }

        viewModelScope.launch {
            _uiState.value = OnboardingUiState.Loading

            try {
                // Convert URIs to MultipartBody.Parts using the external uriToFile function
                val imageParts = _selectedImages.value.mapNotNull { uri ->
                    val file = uriToFile(context, uri)
                    if (file != null) {
                        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("images", file.name, requestBody)
                    } else {
                        _snackbarMessage.value = "Failed to process image"
                        null
                    }
                }

                // Process audio files if any
                val audioParts = _selectedAudios.value.mapNotNull { uri ->
                    val contentResolver = context.contentResolver
                    val mimeType = contentResolver.getType(uri) ?: "audio/*"

                    val inputStream = contentResolver.openInputStream(uri) ?: return@mapNotNull null
                    val file = File(context.cacheDir, "audio_${System.currentTimeMillis()}.mp3")

                    file.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }

                    val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("audios", file.name, requestBody)
                }

                if (imageParts.isEmpty()) {
                    _uiState.value = OnboardingUiState.Error("Failed to process images")
                    return@launch
                }

                // Start onboarding process
                startOnboarding(imageParts, audioParts.ifEmpty { null }, navController)
            } catch (e: Exception) {
                Log.e(TAG, "Error preparing files: ${e.message}", e)
                _uiState.value = OnboardingUiState.Error("Failed to prepare files: ${e.message}")
                _snackbarMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Start the onboarding process with images and optional audio files
     */
    private fun startOnboarding(
        images: List<MultipartBody.Part>,
        audios: List<MultipartBody.Part>? = null,
        navController: NavController
    ) {
        viewModelScope.launch {
            onboardingRepository.onboardUser(images, audios)
                .collect { result ->
                    result.fold(
                        onSuccess = { response ->
                            Log.d(TAG, "Onboarding successful: ${response.message}")

                            // Show success message in snackbar
                            _snackbarMessage.value = response.message

                            // Update UI state
                            _uiState.value = OnboardingUiState.Success

                            // Navigate to dashboard
//                            navController.navigate(Routes.DASHBOARD) {
//                                // Clear the back stack so user can't go back to onboarding
//                                popUpTo(Routes.ONBOARDING) { inclusive = true }
//                            }
                        },
                        onFailure = { error ->
                            Log.e(TAG, "Onboarding failed", error)

                            // Show error message in snackbar
                            _snackbarMessage.value = "Onboarding failed: ${error.message ?: "Unknown error"}"

                            // Update UI state
                            _uiState.value = OnboardingUiState.Error(error.message ?: "Unknown error")
                        }
                    )
                }
        }
    }

    /**
     * Clear the snackbar message after it's been shown
     */
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}

/**
 * UI states for the onboarding screen
 */
sealed class OnboardingUiState {
    data object Initial : OnboardingUiState()
    data object Loading : OnboardingUiState()
    data object Success : OnboardingUiState()
    data class Error(val message: String) : OnboardingUiState()
}
