package com.example.projectz.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projectz.data.local.entity.UserPreferenceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {

    @Query("SELECT * FROM user_preference WHERE id = 1")
    fun getUserPreference(): Flow<UserPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreference(pref: UserPreferenceEntity)

    @Query("UPDATE user_preference SET isLoggedIn = :value WHERE id = 1")
    suspend fun updateLoginState(value: Boolean)

    @Query("UPDATE user_preference SET isOnboardingDone = :value WHERE id = 1")
    suspend fun updateOnboardingState(value: Boolean)

    @Query("UPDATE user_preference SET themePreference = :theme WHERE id = 1")
    suspend fun updateTheme(theme: String)

    @Query("DELETE FROM user_preference")
    suspend fun clearUserPreference()
}
