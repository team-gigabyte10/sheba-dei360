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
import com.barisal.cityservice.core.utils.UserPreferences
import com.barisal.cityservice.data.repository.AuthRepository
import com.barisal.cityservice.data.repository.PhoneOtpResult
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.ExitAppDialog
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    redirectRoute: String? = null,
    onNavigateToHome: () -> Unit,
    onNavigateToRedirectTarget: (String) -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onNavigateToOtp: (target: String, verificationId: String, isEmailMode: Boolean) -> Unit = { _, _, _ -> },
    onNavigateToVendorDashboard: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isBengali = LocalAppLanguage.current.isBengali
    val authRepository = remember { AuthRepository() }

    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    // Forgot Password Dialog
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var isSendingReset by remember { mutableStateOf(false) }

    var showExitDialog by remember { mutableStateOf(false) }
    var backPressedTime by remember { mutableStateOf(0L) }

    BackHandler {
        val currentTime = System.currentTimeMillis()
        if (currentTime - backPressedTime < 2000) {
            showExitDialog = true
        } else {
            backPressedTime = currentTime
            Toast.makeText(
                context,
                if (isBengali) "প্রস্থান অপশন দেখতে আবার ব্যাক চাপুন" else "Press back again to exit",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    if (showExitDialog) {
        ExitAppDialog(
            onDismissRequest = { showExitDialog = false },
            onExitConfirm = { activity?.finish() }
        )
    }

    SetStatusBarColor()

    val primaryTeal = Color(0xFF0F766E)
    val secondaryTeal = Color(0xFF00897B)
    val textMuted = Color(0xFF64748B)

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        CustomDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = if (isBengali) "পাসওয়ার্ড রিসেট করুন" else "Reset Password",
            confirmButtonText = if (isBengali) "লিঙ্ক পাঠান" else "Send Reset Link",
            onConfirm = {
                if (resetEmail.isBlank()) {
                    Toast.makeText(context, if (isBengali) "অনুগ্রহ করে আপনার ইমেইল দিন" else "Please enter your email", Toast.LENGTH_SHORT).show()
                    return@CustomDialog
                }
                isSendingReset = true
                FirebaseAuth.getInstance().sendPasswordResetEmail(resetEmail.trim())
                    .addOnCompleteListener { task ->
                        isSendingReset = false
                        showForgotPasswordDialog = false
                        if (task.isSuccessful) {
                            Toast.makeText(
                                context,
                                if (isBengali) "আপনার ইমেইলে পাসওয়ার্ড রিসেট লিঙ্ক পাঠানো হয়েছে।" else "Password reset link sent to your email.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                context,
                                "ত্রুটি: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = if (isBengali) "আপনার নিবন্ধিত ইমেইল এড্রেস লিখুন। আমরা একটি রিসেট লিঙ্ক পাঠাবো।" else "Enter your registered email address to receive a password reset link.",
                    fontSize = 13.sp,
                    color = textMuted
                )
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = { resetEmail = it },
                    label = { Text(if (isBengali) "ইমেইল এড্রেস" else "Email Address") },
                    placeholder = { Text("user@example.com") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryTeal,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
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
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(primaryTeal.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = if (isBengali) "সেবা দেই ৩৬০" else "Sheba Dei 360",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryTeal
                        )

                        Text(
                            text = if (isBengali) "আপনার অ্যাকাউন্ট দিয়ে লগ-ইন করুন" else "Log in to access your account",
                            fontSize = 12.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Email or Phone Field
                        OutlinedTextField(
                            value = emailOrPhone,
                            onValueChange = { emailOrPhone = it },
                            label = { Text(if (isBengali) "ইমেইল বা ফোন নম্বর *" else "Email or Phone Number *") },
                            placeholder = { Text(if (isBengali) "ইমেইল বা ফোন নম্বর লিখুন" else "Enter email or phone") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryTeal,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(if (isBengali) "পাসওয়ার্ড *" else "Password *") },
                            placeholder = { Text("••••••••") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = primaryTeal) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle Password Visibility",
                                        tint = Color.Gray
                                    )
                                }
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryTeal,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Forgot Password Link
                        Text(
                            text = if (isBengali) "পাসওয়ার্ড ভুলে গেছেন?" else "Forgot Password?",
                            color = primaryTeal,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .align(Alignment.End)
                                .clickable {
                                    resetEmail = if (emailOrPhone.contains("@")) emailOrPhone.trim() else ""
                                    showForgotPasswordDialog = true
                                }
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Standard Login Button
                        Button(
                            onClick = {
                                val input = emailOrPhone.trim()
                                if (input.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "অনুগ্রহ করে ইমেইল/ফোন নম্বর এবং পাসওয়ার্ড দিন" else "Please enter email/phone and password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isLoading = true
                                var loginEmail = input
                                if (!loginEmail.contains("@")) {
                                    loginEmail = "$input@cityservice.com"
                                }

                                FirebaseAuth.getInstance().signInWithEmailAndPassword(loginEmail, password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            UserPreferences.setOtpVerified(context, true)
                                            Toast.makeText(context, if (isBengali) "সফলভাবে লগ-ইন হয়েছে!" else "Login successful!", Toast.LENGTH_SHORT).show()
                                            if (!redirectRoute.isNullOrBlank()) {
                                                onNavigateToRedirectTarget(redirectRoute)
                                            } else {
                                                onNavigateToHome()
                                            }
                                        } else {
                                            // Fallback create account if user logging in with phone for first time
                                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(loginEmail, password)
                                                .addOnCompleteListener { createTask ->
                                                    if (createTask.isSuccessful) {
                                                        UserPreferences.setOtpVerified(context, true)
                                                        Toast.makeText(context, if (isBengali) "লগ-ইন সফল হয়েছে!" else "Login successful!", Toast.LENGTH_SHORT).show()
                                                        if (!redirectRoute.isNullOrBlank()) {
                                                            onNavigateToRedirectTarget(redirectRoute)
                                                        } else {
                                                            onNavigateToHome()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            if (isBengali) "লগ-ইন ব্যর্থ হয়েছে: ${task.exception?.message}" else "Login failed: ${task.exception?.message}",
                                                            Toast.LENGTH_LONG
                                                        ).show()
                                                    }
                                                }
                                        }
                                    }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Login, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "লগ-ইন করুন" else "Sign In",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Divider(color = Color(0xFFE2E8F0))

                        Spacer(modifier = Modifier.height(16.dp))

                        // Register Navigation Link
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBengali) "নতুন ব্যবহারকারী? " else "Don't have an account? ",
                                fontSize = 13.sp,
                                color = textMuted
                            )
                            Text(
                                text = if (isBengali) "রেজিস্ট্রেশন করুন" else "Create Account",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryTeal,
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Vendor Dashboard Navigation Link
                        Text(
                            text = if (isBengali) "ভেন্ডর প্যানেলে যান" else "Login as Vendor",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = secondaryTeal,
                            modifier = Modifier.clickable { onNavigateToVendorDashboard() }
                        )
                    }
                }
            }
        }
    }
}
