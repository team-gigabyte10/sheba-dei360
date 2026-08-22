package com.barisal.cityservice.feature.splash

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.barisal.cityservice.R
import com.barisal.cityservice.core.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        delay(2000L) // Simulate splash delay

        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)

        if (!isOnboardingCompleted) {
            onNavigateToOnboarding()
        } else {
            // Always launch HomeScreen directly for all users (guests and returning users)!
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Splash Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
