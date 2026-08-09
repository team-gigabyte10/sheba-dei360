package com.barisal.cityservice.core.theme

import android.content.Context
import android.content.SharedPreferences

object ThemePreferences {

    private const val PREF_NAME = "city_service_theme_prefs"
    private const val KEY_THEME_MODE = "app_theme_mode"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Retrieves the stored AppThemeMode, defaulting to AppThemeMode.SYSTEM.
     */
    fun getThemeMode(context: Context): AppThemeMode {
        val savedName = getPrefs(context).getString(KEY_THEME_MODE, AppThemeMode.SYSTEM.name)
        return try {
            AppThemeMode.valueOf(savedName ?: AppThemeMode.SYSTEM.name)
        } catch (e: Exception) {
            AppThemeMode.SYSTEM
        }
    }

    /**
     * Saves the selected AppThemeMode into SharedPreferences.
     */
    fun setThemeMode(context: Context, mode: AppThemeMode) {
        getPrefs(context).edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}
