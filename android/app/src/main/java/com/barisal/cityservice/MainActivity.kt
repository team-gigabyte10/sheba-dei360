package com.barisal.cityservice

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
}