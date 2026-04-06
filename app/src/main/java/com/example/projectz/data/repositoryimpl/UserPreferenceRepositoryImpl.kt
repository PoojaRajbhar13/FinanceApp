package com.example.projectz.data.repositoryimpl

import com.example.projectz.core.util.Result
import com.example.projectz.data.local.dao.UserPreferenceDao
import com.example.projectz.data.local.entity.UserPreferenceEntity
import com.example.projectz.domain.repository.UserPreferenceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserPreferenceRepositoryImpl @Inject constructor(
    private val dao: UserPreferenceDao
) : UserPreferenceRepository {

    override fun getUserPreference(): Flow<UserPreferenceEntity?> {
        return dao.getUserPreference()
    }

    override suspend fun saveUserPreference(pref: UserPreferenceEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.saveUserPreference(pref)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to save user preferences")
            }
        }
    }

    override suspend fun updateLoginState(isLoggedIn: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.updateLoginState(isLoggedIn)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to update login state")
            }
        }
    }

    override suspend fun updateOnboardingState(isDone: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.updateOnboardingState(isDone)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to update onboarding state")
            }
        }
    }

    override suspend fun updateTheme(theme: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.updateTheme(theme)
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to update theme")
            }
        }
    }

    override suspend fun clearUserPreference(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                dao.clearUserPreference()
                Result.Success(Unit)
            } catch (e: Exception) {
                Result.Error(e.message ?: "Failed to clear user preferences")
            }
        }
    }
}