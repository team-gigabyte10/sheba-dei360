package com.barisal.cityservice.feature.splash

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit
) {
    val isBengali = LocalAppLanguage.current.isBengali
    val context = LocalContext.current

    LaunchedEffect(key1 = true) {
        delay(2000L) // Simulate splash delay

        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isOnboardingCompleted = sharedPreferences.getBoolean("onboarding_completed", false)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val isOtpVerified = UserPreferences.isOtpVerified(context)

        if (!isOnboardingCompleted) {
            onNavigateToOnboarding()
        } else {
            // Always launch HomeScreen directly for all users (guests and returning users)!
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isBengali) "বরিশাল সিটি সার্ভিস" else "Barisal City Service",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}
