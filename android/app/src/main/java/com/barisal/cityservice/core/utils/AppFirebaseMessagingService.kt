package com.barisal.cityservice.core.utils

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class AppFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCM_SERVICE", "From: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Barisal City Service Verification Code"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: remoteMessage.data["otpCode"]?.let { "Your OTP code is: $it" }

        if (!body.isNullOrBlank()) {
            val otpCode = remoteMessage.data["otpCode"] ?: body.filter { it.isDigit() }.take(6)
            FcmAuthManager.sendFcmNotification(applicationContext, if (otpCode.length == 6) otpCode else body)
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM_SERVICE", "Refreshed FCM Token: $token")
    }
}
