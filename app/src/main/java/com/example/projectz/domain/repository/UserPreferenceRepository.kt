package com.example.projectz.domain.repository

import com.example.projectz.core.util.Result
import com.example.projectz.data.local.entity.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

interface UserPreferenceRepository {
    fun getUserPreference(): Flow<UserPreferenceEntity?>
    suspend fun saveUserPreference(pref: UserPreferenceEntity): Result<Unit>
    suspend fun updateLoginState(isLoggedIn: Boolean): Result<Unit>
    suspend fun updateOnboardingState(isDone: Boolean): Result<Unit>
    suspend fun updateTheme(theme: String): Result<Unit>
    suspend fun clearUserPreference(): Result<Unit>
}
