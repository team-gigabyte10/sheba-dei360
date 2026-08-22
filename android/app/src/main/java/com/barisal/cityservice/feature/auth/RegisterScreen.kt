package com.barisal.cityservice.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.R
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.repository.AuthRepository
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.barisal.cityservice.ui.components.AppTextField
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.barisal.cityservice.ui.components.clearFocusOnTap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.barisal.cityservice.core.utils.UserPreferences
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToOtp: (target: String, verificationId: String, isEmailMode: Boolean) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    var generatedOtpDialogData by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }

    if (generatedOtpDialogData != null) {
        val (target, otpCode, isEmail) = generatedOtpDialogData!!
        OtpDisplayDialog(
            target = target,
            otpCode = otpCode,
            isBengali = isBengali,
            onDismiss = {
                generatedOtpDialogData = null
                onNavigateToOtp(target, otpCode, isEmail)
            },
            onProceed = {
                generatedOtpDialogData = null
                UserPreferences.setOtpVerified(context, true)
                Toast.makeText(
                    context,
                    if (isBengali) "সফলভাবে রেজিস্ট্রেশন সম্পন্ন হয়েছে!" else "Successfully registered!",
                    Toast.LENGTH_SHORT
                ).show()
                onNavigateToHome()
            }
        )
    }

    SetStatusBarColor()

    val primaryTeal = Color(0xFF0F766E)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

    val focusManager = LocalFocusManager.current
    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clearFocusOnTap(),
        contentAlignment = Alignment.Center
    ) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 24.dp)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Card Container
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.95f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(primaryTeal.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(90.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isBengali) "নতুন অ্যাকাউন্ট তৈরি করুন" else "Create New Account",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryTeal
                        )

                        Text(
                            text = if (isBengali) "সকল সেবা পেতে নিবন্ধন সম্পন্ন করুন" else "Register to access all local services",
                            fontSize = 12.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Full Name Field
                        AppTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isBengali) "পুরো নাম *" else "Full Name *") },
                            placeholder = { Text(if (isBengali) "আপনার নাম লিখুন" else "Enter your full name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(onNext = { emailFocusRequester.requestFocus() }),
                            modifier = Modifier.fillMaxWidth(),
                            focusedBorderColor = primaryTeal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Email Field
                        AppTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(if (isBengali) "ইমেইল এড্রেস *" else "Email Address *") },
                            placeholder = { Text("user@example.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            focusRequester = emailFocusRequester,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(onNext = { phoneFocusRequester.requestFocus() }),
                            modifier = Modifier.fillMaxWidth(),
                            focusedBorderColor = primaryTeal
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Phone Field (Mandatory)
                        AppTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBengali) "ফোন নম্বর *" else "Phone Number *") },
                            placeholder = { Text("+88017XXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            focusRequester = phoneFocusRequester,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Phone,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                            modifier = Modifier.fillMaxWidth(),
                            focusedBorderColor = primaryTeal
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Register Button
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || phone.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "অনুগ্রহ করে নাম, ইমেইল ও ফোন নম্বর দিন" else "Please enter your name, email, and phone number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isSubmitting = true
                                focusManager.clearFocus()

                                coroutineScope.launch {
                                    com.barisal.cityservice.core.utils.FcmAuthManager.registerUserWithFcm(
                                        context = context,
                                        name = name,
                                        email = email,
                                        phone = phone
                                    ) { success, otpCode, errorMsg ->
                                        isSubmitting = false
                                        if (success) {
                                            generatedOtpDialogData = Triple(phone.trim(), otpCode, false)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isBengali) "ত্রুটি: $errorMsg" else "Error: $errorMsg",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isSubmitting,
                            colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "ওটিপি পেয়ে নিবন্ধন করুন" else "Get OTP & Register",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE2E8F0))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Login Navigation Link
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBengali) "ইতিমধ্যে একটি অ্যাকাউন্ট আছে? " else "Already have an account? ",
                                fontSize = 13.sp,
                                color = textMuted
                            )
                            Text(
                                text = if (isBengali) "লগ-ইন করুন" else "Log In",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTeal,
                                modifier = Modifier.clickable { onNavigateToLogin() }
                            )
                        }
                    }
                }
            }
        }
    }
}
