package com.barisal.cityservice

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.barisal.cityservice.core.language.LanguageState
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.navigation.AppNavigation
import com.barisal.cityservice.core.theme.LocalAppTheme
import com.barisal.cityservice.core.theme.ThemePreferences
import com.barisal.cityservice.core.theme.ThemeState
import com.barisal.cityservice.ui.theme.Sheba_deiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        setContent {
            val context = LocalContext.current
            val systemDarkTheme = isSystemInDarkTheme()
            val initialMode = remember { ThemePreferences.getThemeMode(context) }
            val languageState = remember { LanguageState() }
            val themeState = remember {
                ThemeState(
                    initialMode = initialMode,
                    initialIsSystemDark = systemDarkTheme,
                    onThemeModeChanged = { mode ->
                        ThemePreferences.setThemeMode(context, mode)
                    }
                )
            }
            themeState.isSystemDark = systemDarkTheme

            CompositionLocalProvider(
                LocalAppLanguage provides languageState,
                LocalAppTheme provides themeState
            ) {
                Sheba_deiTheme {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation()
                    }
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }
}