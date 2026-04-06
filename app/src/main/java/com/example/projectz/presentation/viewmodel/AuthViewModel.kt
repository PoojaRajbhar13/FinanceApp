package com.example.projectz.presentation.viewmodel

import android.util.Log
import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.core.util.Result
import com.example.projectz.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Result<FirebaseUser>>(Result.Idle)
    val loginState: StateFlow<Result<FirebaseUser>> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Result<FirebaseUser>>(Result.Idle)
    val registerState: StateFlow<Result<FirebaseUser>> = _registerState.asStateFlow()

    fun loginWithEmail(email: String, password: String) {
        if (!isValidEmail(email)) {
            _loginState.value = Result.Error("Please enter a valid email address.")
            return
        }
        if (password.isEmpty()) {
            _loginState.value = Result.Error("Password cannot be empty.")
            return
        }

        viewModelScope.launch {
            _loginState.value = Result.Loading
            val result = authRepository.loginWithEmail(email, password)
            if (result is Result.Success) {
                _loginState.value = result
            } else if (result is Result.Error) {
                _loginState.value = Result.Error(mapFirebaseError(result.message))
            }
        }
    }

    fun registerWithEmail(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (firstName.isEmpty() || lastName.isEmpty()) {
            _registerState.value = Result.Error("First and last name cannot be empty.")
            return
        }
        if (!isValidEmail(email)) {
            _registerState.value = Result.Error("Please enter a valid email address.")
            return
        }
        if (password.length < 6) {
            _registerState.value = Result.Error("Password should be at least 6 characters.")
            return
        }
        if (password != confirmPassword) {
            _registerState.value = Result.Error("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _registerState.value = Result.Loading
            val result = authRepository.registerWithEmail(firstName, lastName, email, password)
            if (result is Result.Success) {
                _registerState.value = result
            } else if (result is Result.Error) {
                _registerState.value = Result.Error(mapFirebaseError(result.message))
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        Log.d("LoginFlow", "loginWithGoogle called with token!")
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = Result.Loading
            try {
                val result = authRepository.loginWithGoogle(idToken)
                if (result is Result.Success) {
                    Log.d("LoginFlow", "Firebase login SUCCESS!")
                    _loginState.value = result
                } else if (result is Result.Error) {
                    Log.e("LoginFlow", "Firebase login ERROR: ${result.message}")
                    _loginState.value = Result.Error(mapFirebaseError(result.message))
                }
            } catch (e: Exception) {
                Log.e("LoginFlow", "Exception caught in ViewModel: ${e.message}", e)
                _loginState.value = Result.Error("An unexpected error occurred.")
            }
        }
    }

    fun sendPasswordResetEmail(email: String, onResult: (Result<Unit>) -> Unit) {
        if (!isValidEmail(email)) {
            onResult(Result.Error("Please enter a valid email address."))
            return
        }
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            if (result is Result.Success) {
                onResult(Result.Success(Unit))
            } else if (result is Result.Error) {
                onResult(Result.Error(mapFirebaseError(result.message)))
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun resetState() {
        _loginState.value = Result.Idle
        _registerState.value = Result.Idle
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun mapFirebaseError(message: String): String {
        val lowerMessage = message.lowercase()
        return when {
            lowerMessage.contains("invalid_user") || lowerMessage.contains("user not found") -> "No account found with this email."
            lowerMessage.contains("wrong_password") || lowerMessage.contains("invalid credentials") || lowerMessage.contains(
                "incorrect password"
            ) -> "Incorrect password. Please try again."

            lowerMessage.contains("email_already_in_use") || lowerMessage.contains("already registered") -> "This email is already registered."
            lowerMessage.contains("weak_password") -> "Password should be at least 6 characters."
            lowerMessage.contains("network") || lowerMessage.contains("unable to resolve host") -> "No internet connection. Please try again."
            else -> message
        }
    }
}