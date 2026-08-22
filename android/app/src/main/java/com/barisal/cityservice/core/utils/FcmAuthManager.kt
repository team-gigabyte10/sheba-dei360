package com.barisal.cityservice.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import java.util.Random

object FcmAuthManager {

    private const val CHANNEL_ID = "fcm_otp_channel"
    private const val CHANNEL_NAME = "OTP Verification Notifications"
    private val firestore by lazy { FirebaseFirestore.getInstance() }

    // Active in-memory OTP cache for instant verification fallback
    private val activeOtpCache = mutableMapOf<String, String>()

    fun getDeviceId(context: Context): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "UNKNOWN_DEVICE"
        } catch (e: Exception) {
            "UNKNOWN_DEVICE"
        }
    }

    fun generateOtpCode(): String {
        return (100000 + Random().nextInt(900000)).toString()
    }

    /**
     * Stores/updates user info with device ID and FCM token, then sends an OTP via notification.
     */
    suspend fun registerUserWithFcm(
        context: Context,
        name: String,
        email: String,
        phone: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        try {
            val deviceId = getDeviceId(context)
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                "FCM_TOKEN_UNAVAILABLE"
            }

            val otpCode = generateOtpCode()
            val cleanPhone = phone.trim()
            val cleanEmail = email.trim()

            val userData = hashMapOf(
                "name" to name.trim(),
                "email" to cleanEmail,
                "phone" to cleanPhone,
                "deviceId" to deviceId,
                "fcmToken" to fcmToken,
                "lastOtp" to otpCode,
                "updatedAt" to System.currentTimeMillis()
            )

            // Save in Firestore under users collection (keyed by phone)
            val docKey = cleanPhone.ifBlank { cleanEmail }
            firestore.collection("users").document(docKey).set(userData).await()

            // Store in cache
            activeOtpCache[cleanPhone] = otpCode
            activeOtpCache[cleanEmail] = otpCode

            // Send notification with OTP code
            sendFcmNotification(context, otpCode)
            onResult(true, otpCode, null)
        } catch (e: Exception) {
            onResult(false, "", e.message ?: "Failed to process registration")
        }
    }

    /**
     * Sends an OTP notification for login request and updates user's last OTP code.
     */
    suspend fun sendLoginFcmOtp(
        context: Context,
        target: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        try {
            val cleanTarget = target.trim()
            val deviceId = getDeviceId(context)
            val fcmToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: Exception) {
                "FCM_TOKEN_UNAVAILABLE"
            }

            val otpCode = generateOtpCode()

            val updateData = hashMapOf(
                "deviceId" to deviceId,
                "fcmToken" to fcmToken,
                "lastOtp" to otpCode,
                "updatedAt" to System.currentTimeMillis()
            )

            // Update user's latest FCM token & OTP in Firestore
            val userDocRef = firestore.collection("users").document(cleanTarget)
            val docSnap = userDocRef.get().await()

            if (docSnap.exists()) {
                userDocRef.update(updateData as Map<String, Any>).await()
            } else {
                // If doc doesn't exist yet, create basic record
                val newData = hashMapOf(
                    "emailOrPhone" to cleanTarget,
                    "deviceId" to deviceId,
                    "fcmToken" to fcmToken,
                    "lastOtp" to otpCode,
                    "updatedAt" to System.currentTimeMillis()
                )
                userDocRef.set(newData).await()
            }

            activeOtpCache[cleanTarget] = otpCode
            sendFcmNotification(context, otpCode)
            onResult(true, otpCode, null)
        } catch (e: Exception) {
            onResult(false, "", e.message ?: "Failed to send OTP code")
        }
    }

    /**
     * Verifies the 6-digit OTP code against memory cache or Firestore record.
     */
    suspend fun verifyFcmOtp(target: String, inputCode: String): Boolean {
        val cleanTarget = target.trim()
        val cachedCode = activeOtpCache[cleanTarget]
        if (cachedCode != null && cachedCode == inputCode.trim()) {
            return true
        }

        return try {
            val doc = firestore.collection("users").document(cleanTarget).get().await()
            val savedOtp = doc.getString("lastOtp")
            savedOtp != null && savedOtp == inputCode.trim()
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Posts a local/FCM system notification and on-screen Toast presenting the 6-digit OTP code.
     */
    fun sendFcmNotification(context: Context, otpCode: String) {
        try {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Channels for FCM Security OTP notifications"
                    enableVibration(true)
                    setShowBadge(true)
                }
                notificationManager.createNotificationChannel(channel)
            }

            val iconRes = try {
                com.barisal.cityservice.R.drawable.logo
            } catch (e: Exception) {
                android.R.drawable.ic_dialog_info
            }

            val notification = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(iconRes)
                .setContentTitle("Barisal City Service Verification Code")
                .setContentText("Your OTP code for login is: $otpCode")
                .setStyle(NotificationCompat.BigTextStyle().bigText("Your 6-digit OTP verification code for login is: $otpCode"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true)
                .build()

            notificationManager.notify(1001, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Copy OTP to clipboard for seamless pasting
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("OTP Code", otpCode)
            clipboard.setPrimaryClip(clip)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Always display an immediate Toast banner as on-screen confirmation
        android.os.Handler(android.os.Looper.getMainLooper()).post {
            android.widget.Toast.makeText(
                context,
                "Barisal City Service Login OTP Code: $otpCode (Copied to Clipboard)",
                android.widget.Toast.LENGTH_LONG
            ).show()
        }
    }
}
