package com.himanshu.semhub.ui.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.himanshu.semhub.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private var user: FirebaseUser? = authRepository.getCurrentUser()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun check(){
        if(authRepository.getCurrentUser() !=null) _loginState.value = LoginState.Success
    }

    fun login() {

        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val authCred = authRepository.login()
                if (authCred != null) {
                    authRepository.signInWithFirebase(authCred)
                        .onSuccess {
                            _loginState.value = LoginState.Success
                            Log.d("AuthViewModel",user?.getIdToken(true).toString())
                        }
                        .onFailure {
                            _loginState.value =
                                LoginState.Error(it.localizedMessage ?: "Unknown error")
                        }
                } else {
                    _loginState.value = LoginState.Error("Failed to retrieve credentials")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.localizedMessage ?: "Login error")
            }
        }
    }

    fun getProfile(): FirebaseUser? {
        return user
    }

}

sealed class LoginState {
    data object Idle : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
