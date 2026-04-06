package com.example.projectz.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Route {
    @Serializable
    data object SplashScreen : Route()

    @Serializable
    data object Onboarding : Route()
    
    @Serializable
    data object Home : Route()
    
    @Serializable
    data object Login : Route()
    
    @Serializable
    data object Register : Route()
}
