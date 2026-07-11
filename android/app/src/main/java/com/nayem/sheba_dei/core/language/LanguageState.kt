package com.nayem.sheba_dei.core.language

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class AppLanguage {
    ENGLISH, BENGALI
}

class LanguageState(initialLanguage: AppLanguage = AppLanguage.BENGALI) {
    var currentLanguage by mutableStateOf(initialLanguage)
    val isBengali: Boolean
        get() = currentLanguage == AppLanguage.BENGALI
}

val LocalAppLanguage = compositionLocalOf<LanguageState> {
    error("No AppLanguage provided")
}
