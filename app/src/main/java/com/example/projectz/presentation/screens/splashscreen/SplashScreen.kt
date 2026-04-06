package com.example.projectz.presentation.screens.splashscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.projectz.R
import com.example.projectz.presentation.viewmodel.UserPreferenceViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    userPreferenceViewModel: UserPreferenceViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToOnboarding: () -> Unit
) {
    val state by userPreferenceViewModel.userPreferenceState.collectAsState()
    var startAnimation by remember { mutableStateOf(false) }
    val alphaAni = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 2000),
        label = "alpha"
    )

    LaunchedEffect(key1 = state.isLoading) {
        startAnimation = true
        delay(3000)

        if (!state.isLoading) {
            when {
                state.isFirstTimeLogin -> onNavigateToOnboarding()
                state.isLoggedIn -> onNavigateToHome()
                else -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alphaAni.value),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.splashlogo),
                contentDescription = "App Logo",
                modifier = Modifier.size(200.dp)
            )
        }
    }
}
