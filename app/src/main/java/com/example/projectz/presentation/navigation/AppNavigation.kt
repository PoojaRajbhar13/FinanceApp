package com.example.projectz.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projectz.presentation.viewmodel.AuthViewModel
import com.example.projectz.presentation.screens.auth.LoginScreen
import com.example.projectz.presentation.screens.auth.RegisterScreen
import com.example.projectz.presentation.screens.splashscreen.SplashScreen
import com.example.projectz.presentation.viewmodel.UserPreferenceViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val userPreferenceViewModel: UserPreferenceViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = Route.SplashScreen
    ) {
        composable<Route.SplashScreen> {
            SplashScreen(
                userPreferenceViewModel = userPreferenceViewModel,
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.SplashScreen) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Route.Login) {
                        popUpTo(Route.SplashScreen) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Route.Onboarding) {
                        popUpTo(Route.SplashScreen) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Onboarding> {
            // TODO: Implement OnboardingScreen
            // For now, navigating to Login
            navController.navigate(Route.Login) {
                popUpTo(Route.Onboarding) { inclusive = true }
            }
        }

        composable<Route.Login> {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Route.Register)
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Login) { inclusive = true }
                    }
                }
            )
        }

        composable<Route.Register> {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Route.Login)
                },
                onNavigateToHome = {
                    navController.navigate(Route.Home) {
                        popUpTo(Route.Register) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Route.Home> {
            // Home screen implementation
        }
    }
}
