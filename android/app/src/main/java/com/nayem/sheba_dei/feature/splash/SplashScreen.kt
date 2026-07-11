package com.nayem.sheba_dei.feature.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import com.nayem.sheba_dei.core.language.LocalAppLanguage

@Composable
fun SplashScreen(onNavigateToHome: () -> Unit) {
    val isBengali = LocalAppLanguage.current.isBengali

    LaunchedEffect(key1 = true) {
        delay(2000L) // Simulate some loading/delay
        onNavigateToHome()
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
