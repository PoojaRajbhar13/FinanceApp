package com.example.projectz.data.repositoryimpl

import com.example.projectz.core.util.Result
import com.example.projectz.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User missing after login")
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Login failed")
        }
    }

    override suspend fun registerWithEmail(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User missing after registration")
            
            // Update the display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName("$firstName $lastName")
                .build()
            user.updateProfile(profileUpdates).await()
            
            // Re-fetch user to get the updated profile
            val updatedUser = auth.currentUser ?: user
            Result.Success(updatedUser)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Registration failed")
        }
    }

    override suspend fun loginWithGoogle(idToken: String): Result<FirebaseUser> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user ?: throw Exception("User missing after Google login")
            Result.Success(user)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Google login failed")
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Password reset failed")
        }
    }

    override suspend fun logout() {
        try {
            auth.signOut()
        } catch (e: Exception) {
            // Ignore errors on logout
        }
    }
}