package com.example.projectz.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projectz.domain.repository.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class UserPreferenceState(
    val isLoading: Boolean = true,
    val isLoggedIn: Boolean = false,
    val isFirstTimeLogin: Boolean = true
)

@HiltViewModel
class UserPreferenceViewModel @Inject constructor(
    private val repository: UserPreferenceRepository
) : ViewModel() {

    val userPreferenceState: StateFlow<UserPreferenceState> = repository.getUserPreference()
        .map { pref ->
            if (pref == null) {
                UserPreferenceState(isLoading = false)
            } else {
                UserPreferenceState(
                    isLoading = false,
                    isLoggedIn = pref.isLoggedIn,
                    isFirstTimeLogin = !pref.isOnboardingDone
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferenceState(isLoading = true)
        )
}
