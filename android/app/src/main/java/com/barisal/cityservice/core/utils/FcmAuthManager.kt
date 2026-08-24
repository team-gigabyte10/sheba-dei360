package com.barisal.cityservice.core.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
     * Stores/updates user info with device ID and FCM token, then sends an OTP via notification instantly.
     */
    suspend fun registerUserWithFcm(
        context: Context,
        name: String,
        email: String,
        phone: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        try {
            val otpCode = generateOtpCode()
            val cleanPhone = phone.trim()
            val cleanEmail = email.trim()

            // 1. Immediately cache in memory for instant local verification
            if (cleanPhone.isNotBlank()) activeOtpCache[cleanPhone] = otpCode
            if (cleanEmail.isNotBlank()) activeOtpCache[cleanEmail] = otpCode

            // 2. Instantly post local system Notification + Toast banner + Clipboard copy
            sendFcmNotification(context, otpCode)

            // 3. Instantly notify UI callback
            onResult(true, otpCode, null)

            // 4. Save to Firestore asynchronously in background coroutine scope
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceId = getDeviceId(context)
                    val fcmToken = try {
                        FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {
                        "FCM_TOKEN_UNAVAILABLE"
                    }

                    val userData = hashMapOf(
                        "name" to name.trim(),
                        "email" to cleanEmail,
                        "phone" to cleanPhone,
                        "deviceId" to deviceId,
                        "fcmToken" to fcmToken,
                        "lastOtp" to otpCode,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    val docKey = cleanPhone.ifBlank { cleanEmail }
                    if (docKey.isNotBlank()) {
                        firestore.collection("users").document(docKey).set(userData).await()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            onResult(false, "", e.message ?: "Failed to process registration")
        }
    }

    /**
     * Sends an OTP notification for login request after checking if the user exists in Firestore users collection.
     */
    suspend fun sendLoginFcmOtp(
        context: Context,
        target: String,
        onResult: (Boolean, String, String?) -> Unit
    ) {
        try {
            val cleanTarget = target.trim()
            if (cleanTarget.isBlank()) {
                onResult(false, "", "Please enter email or phone number.")
                return
            }

            // 1. Check if user document or matching record exists in Firestore users collection
            val userDocRef = firestore.collection("users").document(cleanTarget)
            val docSnap = try {
                userDocRef.get().await()
            } catch (e: Exception) {
                null
            }

            var userExists = docSnap?.exists() == true

            if (!userExists) {
                val emailQuery = try {
                    firestore.collection("users").whereEqualTo("email", cleanTarget).get().await()
                } catch (e: Exception) { null }

                val phoneQuery = try {
                    firestore.collection("users").whereEqualTo("phone", cleanTarget).get().await()
                } catch (e: Exception) { null }

                val altQuery = try {
                    firestore.collection("users").whereEqualTo("emailOrPhone", cleanTarget).get().await()
                } catch (e: Exception) { null }

                userExists = (emailQuery?.isEmpty == false) ||
                             (phoneQuery?.isEmpty == false) ||
                             (altQuery?.isEmpty == false)
            }

            // 2. If user does NOT exist in Firestore, return INVALID_USER error and DO NOT send OTP
            if (!userExists) {
                onResult(false, "", "INVALID_USER")
                return
            }

            // 3. User exists: Generate OTP code, cache locally, and post notification instantly
            val otpCode = generateOtpCode()
            activeOtpCache[cleanTarget] = otpCode

            sendFcmNotification(context, otpCode)
            onResult(true, otpCode, null)

            // 4. Save/update user's latest FCM token & OTP in Firestore asynchronously
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val deviceId = getDeviceId(context)
                    val fcmToken = try {
                        FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {
                        "FCM_TOKEN_UNAVAILABLE"
                    }

                    val updateData = hashMapOf(
                        "deviceId" to deviceId,
                        "fcmToken" to fcmToken,
                        "lastOtp" to otpCode,
                        "updatedAt" to System.currentTimeMillis()
                    )

                    if (docSnap?.exists() == true) {
                        userDocRef.update(updateData as Map<String, Any>).await()
                    } else {
                        userDocRef.set(updateData as Map<String, Any>, com.google.firebase.firestore.SetOptions.merge()).await()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
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
