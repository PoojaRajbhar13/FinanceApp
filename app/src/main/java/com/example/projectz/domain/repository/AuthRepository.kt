package com.example.projectz.domain.repository

import com.example.projectz.core.util.Result
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isLoggedIn: Boolean

    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser>

    suspend fun registerWithEmail(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<FirebaseUser>

    suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun logout()
}

