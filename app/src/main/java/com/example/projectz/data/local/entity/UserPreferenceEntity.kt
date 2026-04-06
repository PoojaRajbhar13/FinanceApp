package com.example.projectz.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preference")
data class UserPreferenceEntity(
    @PrimaryKey
    val id: Int = 1,
    val isLoggedIn: Boolean = false,
    val isOnboardingDone: Boolean = false,
    val userName: String = "",
    val userEmail: String = "",
    val themePreference: String = "system"
)
