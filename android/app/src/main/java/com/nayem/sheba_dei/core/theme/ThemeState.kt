package com.nayem.sheba_dei.core.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.compositionLocalOf

class ThemeState(initialIsDark: Boolean = false) {
    var isDarkMode by mutableStateOf(initialIsDark)
}

val LocalAppTheme = compositionLocalOf<ThemeState> { 
    error("No ThemeState provided") 
}
