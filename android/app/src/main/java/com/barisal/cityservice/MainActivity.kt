package com.barisal.cityservice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.barisal.cityservice.core.navigation.AppNavigation
import com.barisal.cityservice.ui.theme.Sheba_deiTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.barisal.cityservice.core.language.LanguageState
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.theme.ThemeState
import com.barisal.cityservice.core.theme.LocalAppTheme
import androidx.compose.foundation.isSystemInDarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemDarkTheme = isSystemInDarkTheme()
            val languageState = remember { LanguageState() }
            val themeState = remember { ThemeState(initialIsDark = systemDarkTheme) }
            
            CompositionLocalProvider(
                LocalAppLanguage provides languageState,
                LocalAppTheme provides themeState
            ) {
                Sheba_deiTheme(darkTheme = themeState.isDarkMode) {
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