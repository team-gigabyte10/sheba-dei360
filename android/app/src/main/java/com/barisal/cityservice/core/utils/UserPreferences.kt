package com.barisal.cityservice.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

object UserPreferences {

    private const val PREF_NAME = "city_service_user_prefs"
    private const val KEY_OTP_VERIFIED = "is_otp_verified"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Stores whether the current user has completed one-time OTP verification.
     */
    fun setOtpVerified(context: Context, isVerified: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_OTP_VERIFIED, isVerified).apply()
    }

    /**
     * Returns true if the user has completed one-time OTP verification.
     */
    fun isOtpVerified(context: Context): Boolean {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return false
        val isVerifiedInPrefs = getPrefs(context).getBoolean(KEY_OTP_VERIFIED, false)
        val isEmailVerifiedInFirebase = currentUser.isEmailVerified
        val isPhoneUserInFirebase = !currentUser.phoneNumber.isNullOrBlank()

        return isVerifiedInPrefs || isEmailVerifiedInFirebase || isPhoneUserInFirebase
    }
}
