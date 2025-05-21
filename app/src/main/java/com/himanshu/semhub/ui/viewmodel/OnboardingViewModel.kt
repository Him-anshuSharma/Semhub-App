package com.himanshu.semhub.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.himanshu.semhub.data.repository.OnboardingRepository
import com.himanshu.semhub.utils.uriToFile
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
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
    private val NETWORK_TIMEOUT = 1200000L // 60 seconds timeout

    init {
        viewModelScope.launch {
            Log.d(TAG, onboardingRepository.getToken().toString())
        }
    }

    // UI state for onboarding
    private val _uiState = MutableStateFlow<OnboardingUiState>(OnboardingUiState.Initial)
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    // Progress state for more granular progress indication
    private val _progressState = MutableStateFlow<ProgressState>(ProgressState.Idle)
    val progressState: StateFlow<ProgressState> = _progressState.asStateFlow()

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
            _progressState.value = ProgressState.PreparingFiles

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
                    _progressState.value = ProgressState.Error("Failed to process images")
                    return@launch
                }

                // Start onboarding process
                startOnboarding(imageParts, audioParts.ifEmpty { null }, navController)
            } catch (e: Exception) {
                Log.e(TAG, "Error preparing files: ${e.message}", e)
                _uiState.value = OnboardingUiState.Error("Failed to prepare files: ${e.message}")
                _progressState.value = ProgressState.Error("Failed to prepare files: ${e.message}")
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
            try {
                _progressState.value = ProgressState.Onboarding

                // Step 1: Perform onboarding with timeout
                withTimeout(NETWORK_TIMEOUT) {
                    onboardingRepository.onboardUser(images, audios).collect { result ->
                        result.fold(
                            onSuccess = { response ->
                                Log.d(TAG, "Onboarding successful: ${response.message}")
                                _snackbarMessage.value = response.message
                                _progressState.value = ProgressState.FetchingTasks

                                // Step 2: Fetch and save tasks
                                fetchTasks { tasksSuccess ->
                                    if (tasksSuccess) {
                                        _progressState.value = ProgressState.FetchingGoals

                                        // Step 3: Fetch and save goals only if tasks were successful
                                        fetchGoals { goalsSuccess ->
                                            if (goalsSuccess) {
                                                Log.d(TAG, "All data fetched and saved successfully")
                                                _progressState.value = ProgressState.Completed

                                                // Only update UI state to Success after all operations complete
                                                _uiState.value = OnboardingUiState.Success

                                                // Navigation code would go here
                                            } else {
                                                _progressState.value = ProgressState.Error("Failed to fetch goals")
                                                _uiState.value = OnboardingUiState.Error("Failed to fetch goals")
                                            }
                                        }
                                    } else {
                                        _progressState.value = ProgressState.Error("Failed to fetch tasks")
                                        _uiState.value = OnboardingUiState.Error("Failed to fetch tasks")
                                    }
                                }
                            },
                            onFailure = { error ->
                                Log.e(TAG, "Onboarding failed", error)
                                _snackbarMessage.value = "Onboarding failed: ${error.message ?: "Unknown error"}"
                                _uiState.value = OnboardingUiState.Error(error.message ?: "Unknown error")
                                _progressState.value = ProgressState.Error(error.message ?: "Unknown error")
                            }
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Onboarding timed out", e)
                _uiState.value = OnboardingUiState.Error("Operation timed out. Please try again.")
                _progressState.value = ProgressState.Error("Operation timed out. Please try again.")
                _snackbarMessage.value = "Operation timed out. Please try again."
            } catch (e: CancellationException) {
                // Coroutine was cancelled normally, don't treat as error
                Log.d(TAG, "Onboarding operation was cancelled")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Error in onboarding process", e)
                _uiState.value = OnboardingUiState.Error("Onboarding process failed: ${e.message}")
                _progressState.value = ProgressState.Error("Onboarding process failed: ${e.message}")
                _snackbarMessage.value = "Error: ${e.message}"
            }
        }
    }

    /**
     * Fetch tasks from API and save to local database
     */
    private fun fetchTasks(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Fetching and saving tasks to local database")

                withTimeout(NETWORK_TIMEOUT) {
                    onboardingRepository.getTasks().collect { tasksResult ->
                        tasksResult.fold(
                            onSuccess = { tasks ->
                                Log.d(TAG, "Successfully fetched ${tasks.size} tasks")
                                onComplete(true)
                            },
                            onFailure = { error ->
                                Log.e(TAG, "Failed to fetch tasks", error)
                                _snackbarMessage.value = "Failed to fetch tasks: ${error.message}"
                                onComplete(false)
                            }
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Task fetching timed out", e)
                _snackbarMessage.value = "Task fetching timed out. Please try again."
                onComplete(false)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching tasks", e)
                _snackbarMessage.value = "Error fetching tasks: ${e.message}"
                onComplete(false)
            }
        }
    }

    /**
     * Fetch goals from API and save to local database
     */
    private fun fetchGoals(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d(TAG, "Fetching and saving goals to local database")

                withTimeout(NETWORK_TIMEOUT) {
                    onboardingRepository.getGoals().collect { goalsResult ->
                        goalsResult.fold(
                            onSuccess = { goals ->
                                Log.d(TAG, "Successfully fetched ${goals.size} goals")
                                onComplete(true)
                            },
                            onFailure = { error ->
                                Log.e(TAG, "Failed to fetch goals", error)
                                _snackbarMessage.value = "Failed to fetch goals: ${error.message}"
                                onComplete(false)
                            }
                        )
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e(TAG, "Goal fetching timed out", e)
                _snackbarMessage.value = "Goal fetching timed out. Please try again."
                onComplete(false)
            } catch (e: Exception) {
                Log.e(TAG, "Error fetching goals", e)
                _snackbarMessage.value = "Error fetching goals: ${e.message}"
                onComplete(false)
            }
        }
    }

    /**
     * Retry the entire onboarding process
     */
    fun retryOnboarding(navController: NavController) {
        prepareAndSubmitOnboarding(navController)
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

/**
 * More granular progress states for detailed progress indication
 */
sealed class ProgressState {
    data object Idle : ProgressState()
    data object PreparingFiles : ProgressState()
    data object Onboarding : ProgressState()
    data object FetchingTasks : ProgressState()
    data object FetchingGoals : ProgressState()
    data object Completed : ProgressState()
    data class Error(val message: String) : ProgressState()
}
