package com.barisal.cityservice.data.repository

import android.app.Activity
import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

sealed class PhoneOtpResult {
    data class CodeSent(val verificationId: String, val token: PhoneAuthProvider.ForceResendingToken) : PhoneOtpResult()
    data class Completed(val user: FirebaseUser?) : PhoneOtpResult()
    data class Error(val exception: Exception) : PhoneOtpResult()
}

class AuthRepository {

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    init {
        auth.useAppLanguage()
    }

    /**
     * Sends a 6-digit SMS OTP code to the provided phone number using Firebase PhoneAuth.
     */
    fun sendPhoneOtp(
        activity: Activity,
        phoneNumber: String,
        resendToken: PhoneAuthProvider.ForceResendingToken? = null,
        onResult: (PhoneOtpResult) -> Unit
    ) {
        var formattedPhone = phoneNumber.trim()
        if (formattedPhone.startsWith("0")) {
            formattedPhone = "+88$formattedPhone"
        } else if (!formattedPhone.startsWith("+")) {
            formattedPhone = "+88$formattedPhone"
        }

        val builder = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(formattedPhone)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    val currentUser = auth.currentUser
                    if (currentUser != null) {
                        currentUser.linkWithCredential(credential)
                            .addOnCompleteListener { linkTask ->
                                onResult(PhoneOtpResult.Completed(auth.currentUser))
                            }
                    } else {
                        auth.signInWithCredential(credential)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    onResult(PhoneOtpResult.Completed(auth.currentUser))
                                } else {
                                    onResult(PhoneOtpResult.Error(task.exception ?: Exception("Verification failed")))
                                }
                            }
                    }
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Log.e("AuthRepository", "Phone verification failed: ${e.message}", e)
                    val msg = e.message ?: ""
                    val userFriendlyError = when {
                        msg.contains("BILLING_NOT_ENABLED") -> {
                            Exception("Firebase SMS requirement: Upgrade to Blaze Plan or add Test Numbers in Firebase Console (Authentication > Sign-in method > Phone > Phone numbers for testing).")
                        }
                        msg.contains("App verification") || e is FirebaseAuthMissingActivityForRecaptchaException -> {
                            Exception("App verification failed. Ensure SHA-1 & SHA-256 fingerprints are registered in Firebase Console Project Settings.")
                        }
                        msg.contains("QuotaExceeded") -> {
                            Exception("SMS quota exceeded for this number. Please try again later or use test phone numbers.")
                        }
                        else -> e
                    }
                    onResult(PhoneOtpResult.Error(userFriendlyError))
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    onResult(PhoneOtpResult.CodeSent(verificationId, token))
                }
            })

        if (resendToken != null) {
            builder.setForceResendingToken(resendToken)
        }

        PhoneAuthProvider.verifyPhoneNumber(builder.build())
    }

    /**
     * Verifies the 6-digit SMS OTP code and signs in the user.
     */
    suspend fun verifyPhoneOtp(verificationId: String, code: String): Result<FirebaseUser?> = suspendCancellableCoroutine { continuation ->
        try {
            val credential = PhoneAuthProvider.getCredential(verificationId, code)
            val currentUser = auth.currentUser
            if (currentUser != null) {
                currentUser.linkWithCredential(credential)
                    .addOnCompleteListener { linkTask ->
                        if (linkTask.isSuccessful) {
                            if (continuation.isActive) continuation.resume(Result.success(auth.currentUser))
                        } else {
                            auth.signInWithCredential(credential)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        if (continuation.isActive) continuation.resume(Result.success(auth.currentUser))
                                    } else {
                                        if (continuation.isActive) continuation.resume(Result.failure(task.exception ?: Exception("Invalid OTP code")))
                                    }
                                }
                        }
                    }
            } else {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            if (continuation.isActive) continuation.resume(Result.success(auth.currentUser))
                        } else {
                            if (continuation.isActive) continuation.resume(Result.failure(task.exception ?: Exception("Invalid OTP code")))
                        }
                    }
            }
        } catch (e: Exception) {
            if (continuation.isActive) continuation.resume(Result.failure(e))
        }
    }

    /**
     * Sends an email verification link/code to the currently logged in user.
     */
    suspend fun sendEmailVerification(): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val currentUser = auth.currentUser
        if (currentUser == null) {
            if (continuation.isActive) continuation.resume(Result.failure(Exception("No user logged in")))
            return@suspendCancellableCoroutine
        }

        currentUser.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (continuation.isActive) continuation.resume(Result.success(true))
                } else {
                    if (continuation.isActive) continuation.resume(Result.failure(task.exception ?: Exception("Failed to send email verification")))
                }
            }
    }

    /**
     * Reloads current user state and checks if email is verified.
     */
    suspend fun checkEmailVerified(): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val currentUser = auth.currentUser
        if (currentUser == null) {
            if (continuation.isActive) continuation.resume(Result.success(false))
            return@suspendCancellableCoroutine
        }

        currentUser.reload()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val isVerified = auth.currentUser?.isEmailVerified == true
                    if (continuation.isActive) continuation.resume(Result.success(isVerified))
                } else {
                    if (continuation.isActive) continuation.resume(Result.failure(task.exception ?: Exception("Failed to reload user")))
                }
            }
    }

    /**
     * Sends FCM OTP code for passwordless sign-in request.
     */
    suspend fun sendFcmOtp(context: android.content.Context, target: String): Result<String> {
        return suspendCancellableCoroutine { continuation ->
            kotlinx.coroutines.CoroutineScope(Dispatchers.IO).launch {
                com.barisal.cityservice.core.utils.FcmAuthManager.sendLoginFcmOtp(context, target) { success, code, error ->
                    if (success) {
                        if (continuation.isActive) continuation.resume(Result.success(code))
                    } else {
                        if (continuation.isActive) continuation.resume(Result.failure(Exception(error ?: "Failed to send OTP")))
                    }
                }
            }
        }
    }

    /**
     * Verifies FCM OTP code for passwordless sign-in request.
     */
    suspend fun verifyFcmOtp(target: String, code: String): Boolean {
        return com.barisal.cityservice.core.utils.FcmAuthManager.verifyFcmOtp(target, code)
    }
}
