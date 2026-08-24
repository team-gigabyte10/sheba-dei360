package com.barisal.cityservice.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth

object UserPreferences {

    private const val PREF_NAME = "city_service_user_prefs"
    private const val KEY_OTP_VERIFIED = "is_otp_verified"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_IDENTITY = "user_identity"

    private val ADMIN_EMAILS = setOf(
        "admin@servenear.com",
        "admin@sheba.com",
        "teamgigabyte10@gmail.com",
        "eeerakib24@gmail.com"
    )

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
     * Stores the user email or phone number upon login.
     */
    fun setUserIdentity(context: Context, identity: String) {
        val cleanIdentity = identity.trim()
        getPrefs(context).edit().putString(KEY_USER_IDENTITY, cleanIdentity).apply()
        
        // Auto-assign admin role if identity belongs to admin list
        val lowerIdentity = cleanIdentity.lowercase()
        if (lowerIdentity.contains("admin") || ADMIN_EMAILS.contains(lowerIdentity) || lowerIdentity.contains("01798771927")) {
            setUserRole(context, "admin")
        } else {
            setUserRole(context, "customer")
        }
    }

    /**
     * Retrieves stored user email or phone identity.
     */
    fun getUserIdentity(context: Context): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val firebaseIdentity = currentUser?.email ?: currentUser?.phoneNumber ?: ""
        if (firebaseIdentity.isNotBlank()) return firebaseIdentity
        return getPrefs(context).getString(KEY_USER_IDENTITY, "") ?: ""
    }

    /**
     * Returns true if the user is currently logged in via Firebase or OTP session.
     */
    fun isLoggedIn(context: Context): Boolean {
        val hasFirebaseUser = FirebaseAuth.getInstance().currentUser != null
        val isVerifiedInPrefs = getPrefs(context).getBoolean(KEY_OTP_VERIFIED, false)
        val hasIdentity = getUserIdentity(context).isNotBlank()
        return hasFirebaseUser || (isVerifiedInPrefs && hasIdentity)
    }

    /**
     * Stores the user role (e.g. "admin", "customer").
     */
    fun setUserRole(context: Context, role: String) {
        getPrefs(context).edit().putString(KEY_USER_ROLE, role).apply()
    }

    /**
     * Retrieves the stored user role.
     */
    fun getUserRole(context: Context): String {
        return getPrefs(context).getString(KEY_USER_ROLE, "customer") ?: "customer"
    }

    /**
     * Returns true if the logged-in user is an admin.
     */
    fun isAdmin(context: Context): Boolean {
        if (!isLoggedIn(context)) return false
        val identity = getUserIdentity(context).lowercase()
        val role = getUserRole(context)
        return role == "admin" ||
                identity.contains("admin") ||
                ADMIN_EMAILS.contains(identity) ||
                identity.contains("01798771927")
    }

    /**
     * Clears all authentication session preferences on logout.
     */
    fun clearSession(context: Context) {
        getPrefs(context).edit()
            .putBoolean(KEY_OTP_VERIFIED, false)
            .remove(KEY_USER_IDENTITY)
            .remove(KEY_USER_ROLE)
            .apply()
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
