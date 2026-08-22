package com.barisal.cityservice.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.PhonelinkRing
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.repository.AuthRepository
import com.barisal.cityservice.data.repository.PhoneOtpResult
import com.barisal.cityservice.ui.components.AppTextField
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen(
    target: String = "", // Phone number or Email
    verificationId: String = "",
    isEmailMode: Boolean = false,
    onVerifySuccess: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository() }

    val initialOtp = remember(verificationId) {
        if (verificationId.length == 6 && verificationId.all { it.isDigit() }) verificationId else ""
    }
    var otpCode by remember { mutableStateOf(initialOtp) }
    var currentVerificationId by remember { mutableStateOf(verificationId) }
    var isVerifying by remember { mutableStateOf(false) }

    LaunchedEffect(verificationId) {
        if (verificationId.length == 6 && verificationId.all { it.isDigit() }) {
            otpCode = verificationId
        }
    }

    // 60-second countdown timer
    var timerSeconds by remember { mutableStateOf(60) }
    var canResend by remember { mutableStateOf(false) }

    LaunchedEffect(timerSeconds) {
        if (timerSeconds > 0) {
            delay(1000L)
            timerSeconds -= 1
        } else {
            canResend = true
        }
    }

    SetStatusBarColor()
    val primaryTeal = Color(0xFF00897B)

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "ওটিপি যাচাইকরণ" else "OTP Verification",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(primaryTeal.copy(alpha = 0.1f), shape = RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isEmailMode) Icons.Default.MarkEmailRead else Icons.Default.PhonelinkRing,
                    contentDescription = null,
                    tint = primaryTeal,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (isBengali) "যাচাইকরণ কোড লিখুন" else "Enter Verification Code",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isEmailMode) {
                    if (isBengali) "আপনার ইমেইল ($target)-এ ভেরিফিকেশন পাঠানো হয়েছে। নিচে ওটিপি লিখুন বা ইমেইল লিঙ্ক চেক করুন।" else "Verification sent to $target. Enter OTP code or verify link."
                } else {
                    if (isBengali) "আপনার নম্বর ($target)-এ ৬-সংখ্যার ওটিপি কোড পাঠানো হয়েছে।" else "A 6-digit OTP code was sent to $target."
                },
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            AppTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text(if (isBengali) "৬-সংখ্যার ওটিপি কোড" else "6-Digit OTP Code") },
                placeholder = { Text("123456") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                focusedBorderColor = primaryTeal
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Verify Button
            Button(
                onClick = {
                    if (otpCode.length < 6) {
                        Toast.makeText(
                            context,
                            if (isBengali) "অনুগ্রহ করে ৬-সংখ্যার সঠিক ওটিপি কোড লিখুন" else "Please enter a valid 6-digit OTP code",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isVerifying = true

                    coroutineScope.launch {
                        val isFcmValid = authRepository.verifyFcmOtp(target, otpCode) || (currentVerificationId.isNotBlank() && currentVerificationId == otpCode)
                        if (isFcmValid) {
                            isVerifying = false
                            com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, true)
                            Toast.makeText(context, if (isBengali) "সফলভাবে ভেরিফাই হয়েছে!" else "Verified successfully!", Toast.LENGTH_SHORT).show()
                            onVerifySuccess()
                        } else if (isEmailMode) {
                            val res = authRepository.checkEmailVerified()
                            isVerifying = false
                            if (res.getOrDefault(false)) {
                                com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, true)
                                Toast.makeText(context, if (isBengali) "ইমেইল সফলভাবে ভেরিফাই হয়েছে!" else "Email verified successfully!", Toast.LENGTH_SHORT).show()
                                onVerifySuccess()
                            } else {
                                Toast.makeText(context, if (isBengali) "ভুল ওটিপি কোড! আবার চেষ্টা করুন।" else "Invalid OTP code! Please try again.", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            val res = authRepository.verifyPhoneOtp(currentVerificationId, otpCode)
                            isVerifying = false
                            if (res.isSuccess) {
                                com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, true)
                                Toast.makeText(context, if (isBengali) "ফোন নম্বর সফলভাবে ভেরিফাই হয়েছে!" else "Phone number verified successfully!", Toast.LENGTH_SHORT).show()
                                onVerifySuccess()
                            } else {
                                Toast.makeText(context, if (isBengali) "ভুল ওটিপি কোড! আবার চেষ্টা করুন।" else "Invalid OTP code! Please try again.", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !isVerifying,
                colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
            ) {
                if (isVerifying) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isBengali) "ওটিপি যাচাই করুন" else "Verify OTP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Resend Timer & Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(Icons.Default.LockClock, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                if (timerSeconds > 0) {
                    Text(
                        text = if (isBengali) "পুনরায় পাঠান ($timerSeconds সেক)" else "Resend in ${timerSeconds}s",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                } else {
                    TextButton(
                        onClick = {
                            if (canResend) {
                                canResend = false
                                timerSeconds = 60
                                coroutineScope.launch {
                                    if (isEmailMode) {
                                        val res = authRepository.sendEmailVerification()
                                        if (res.isSuccess) {
                                            Toast.makeText(context, if (isBengali) "নতুন ভেরিফিকেশন ইমেইল পাঠানো হয়েছে!" else "New verification email sent!", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        val activity = context as? Activity
                                        if (activity != null) {
                                            authRepository.sendPhoneOtp(activity, target) { result ->
                                                when (result) {
                                                    is PhoneOtpResult.CodeSent -> {
                                                        currentVerificationId = result.verificationId
                                                        Toast.makeText(context, if (isBengali) "নতুন ওটিপি কোড পাঠানো হয়েছে!" else "New OTP code sent!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    is PhoneOtpResult.Error -> {
                                                        Toast.makeText(context, "ত্রুটি: ${result.exception.message}", Toast.LENGTH_LONG).show()
                                                    }
                                                    else -> {}
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    ) {
                        Text(
                            text = if (isBengali) "কোড পুনরায় পাঠান" else "Resend Code",
                            fontWeight = FontWeight.Bold,
                            color = primaryTeal
                        )
                    }
                }
            }
        }
    }
}
