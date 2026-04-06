package com.example.projectz.core.di

import android.content.Context
import androidx.room.Room
import com.example.projectz.data.local.dao.GoalDao
import com.example.projectz.data.local.dao.TransactionDao
import com.example.projectz.data.local.dao.UserPreferenceDao
import com.example.projectz.data.local.database.FinanceDatabase
import com.example.projectz.data.repositoryimpl.AuthRepositoryImpl
import com.example.projectz.data.repositoryimpl.OfflineFirstFinanceRepository
import com.example.projectz.data.repositoryimpl.UserPreferenceRepositoryImpl
import com.example.projectz.domain.repository.AuthRepository
import com.example.projectz.domain.repository.FinanceRepository
import com.example.projectz.domain.repository.UserPreferenceRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FinanceDatabase {
        return Room.databaseBuilder(
            context,
            FinanceDatabase::class.java,
            "finance.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideTransactionDao(db: FinanceDatabase): TransactionDao = db.transactionDao

    @Provides
    fun provideGoalDao(db: FinanceDatabase): GoalDao = db.goalDao

    @Provides
    @Singleton
    fun provideFinanceRepository(
        transactionDao: TransactionDao,
        goalDao: GoalDao
    ): FinanceRepository {
        return OfflineFirstFinanceRepository(transactionDao, goalDao)
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideGoogleSignInClient(@ApplicationContext context: Context): GoogleSignInClient {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            // TODO: Add your Web Client ID here
            .requestIdToken("YOUR_WEB_CLIENT_ID") 
            .build()
        return GoogleSignIn.getClient(context, options)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(auth: FirebaseAuth): AuthRepository{
        return AuthRepositoryImpl(auth)
    }

    @Provides
    fun provideUserPreferenceDao(db: FinanceDatabase): UserPreferenceDao = db.userPreferenceDao

    @Provides
    @Singleton
    fun provideUserPreferenceRepository(
        dao: UserPreferenceDao
    ): UserPreferenceRepository {
        return UserPreferenceRepositoryImpl(dao)
    }
}
