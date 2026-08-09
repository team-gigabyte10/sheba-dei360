package com.barisal.cityservice.core.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppThemeMode {
    LIGHT, DARK, SYSTEM
}

class ThemeState(
    initialMode: AppThemeMode = AppThemeMode.SYSTEM,
    initialIsSystemDark: Boolean = false,
    private val onThemeModeChanged: ((AppThemeMode) -> Unit)? = null
) {
    var themeMode by mutableStateOf(initialMode)
        private set

    var isSystemDark by mutableStateOf(initialIsSystemDark)

    var isDarkMode: Boolean
        get() = when (themeMode) {
            AppThemeMode.LIGHT -> false
            AppThemeMode.DARK -> true
            AppThemeMode.SYSTEM -> isSystemDark
        }
        set(value) {
            val newMode = if (value) AppThemeMode.DARK else AppThemeMode.LIGHT
            updateThemeMode(newMode)
        }

    fun updateThemeMode(newMode: AppThemeMode) {
        themeMode = newMode
        onThemeModeChanged?.invoke(newMode)
    }
}

val LocalAppTheme = compositionLocalOf<ThemeState> {
    error("No ThemeState provided")
}
