package com.barisal.cityservice.feature.auth

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import com.barisal.cityservice.ui.components.AppTextField
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.ExitAppDialog
import com.barisal.cityservice.ui.components.SetStatusBarColor
import androidx.compose.ui.graphics.Brush
import com.barisal.cityservice.ui.components.clearFocusOnTap
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    redirectRoute: String? = null,
    onNavigateToHome: () -> Unit,
    onNavigateToRedirectTarget: (String) -> Unit = {},
    onNavigateToRegister: () -> Unit,
    onNavigateToOtp: (target: String, verificationId: String, isEmailMode: Boolean) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val isBengali = LocalAppLanguage.current.isBengali
    val authRepository = remember { AuthRepository() }
    val coroutineScope = rememberCoroutineScope()

    var emailOrPhone by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var generatedOtpDialogData by remember { mutableStateOf<Triple<String, String, Boolean>?>(null) }
    var showInvalidUserDialog by remember { mutableStateOf(false) }

    BackHandler {
        onNavigateToHome()
    }

    if (showInvalidUserDialog) {
        InvalidUserDialog(
            target = emailOrPhone.ifBlank { "Unregistered User" },
            isBengali = isBengali,
            onDismiss = { showInvalidUserDialog = false },
            onRegisterClick = {
                showInvalidUserDialog = false
                onNavigateToRegister()
            }
        )
    }

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
                UserPreferences.setUserIdentity(context, target)
                UserPreferences.setOtpVerified(context, true)
                Toast.makeText(
                    context,
                    if (isBengali) "সফলভাবে লগইন হয়েছে!" else "Successfully logged in!",
                    Toast.LENGTH_SHORT
                ).show()
                if (!redirectRoute.isNullOrBlank()) {
                    onNavigateToRedirectTarget(redirectRoute)
                } else {
                    onNavigateToHome()
                }
            }
        )
    }

    SetStatusBarColor()

    val primaryTeal = Color(0xFF0F766E)
    val secondaryTeal = Color(0xFF00897B)
    val textMuted = Color(0xFF64748B)

    val focusManager = LocalFocusManager.current

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
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.96f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Logo with Gradient Ring
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .border(3.dp, Brush.linearGradient(listOf(Color(0xFF1E3A8A), Color(0xFF0F766E))), CircleShape)
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier
                                    .size(92.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isBengali) "বরিশাল সিটি সার্ভিস" else "Barisal City Service",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E3A8A)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = if (isBengali) "আপনার অ্যাকাউন্ট দিয়ে লগ-ইন করুন" else "Log in to access your account",
                            fontSize = 13.sp,
                            color = textMuted,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Email or Phone Field
                        AppTextField(
                            value = emailOrPhone,
                            onValueChange = { emailOrPhone = it },
                            label = { Text(if (isBengali) "ইমেইল বা ফোন নম্বর *" else "Email or Phone Number *") },
                            placeholder = { Text(if (isBengali) "ইমেইল বা ফোন নম্বর লিখুন" else "Enter email or phone") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus() }
                            ),
                            modifier = Modifier.fillMaxWidth(),
                            focusedBorderColor = primaryTeal
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        // Send OTP Button
                        Button(
                            onClick = {
                                val input = emailOrPhone.trim()
                                if (input.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "অনুগ্রহ করে ইমেইল বা ফোন নম্বর দিন" else "Please enter email or phone number",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isLoading = true
                                focusManager.clearFocus()

                                coroutineScope.launch {
                                    com.barisal.cityservice.core.utils.FcmAuthManager.sendLoginFcmOtp(
                                        context = context,
                                        target = input
                                    ) { success, otpCode, errorMsg ->
                                        isLoading = false
                                        if (success) {
                                            val isEmail = input.contains("@")
                                            generatedOtpDialogData = Triple(input, otpCode, isEmail)
                                        } else if (errorMsg == "INVALID_USER") {
                                            showInvalidUserDialog = true
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
                                .height(52.dp)
                                .clip(RoundedCornerShape(26.dp)),
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(Color(0xFF1E3A8A), Color(0xFF0F766E))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                                } else {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Send, contentDescription = null, tint = Color.White)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = if (isBengali) "ওটিপি কোড পাঠান" else "Get OTP Code",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
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
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E3A8A),
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtpDisplayDialog(
    target: String,
    otpCode: String,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onProceed: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Colorful Icon Badge with Gradient Effect
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF0F766E), Color(0xFF00BFA5))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.VpnKey,
                        contentDescription = "OTP Security Icon",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isBengali) "বরিশাল সিটি সার্ভিস ওটিপি কোড" else "Barisal City Service Verification Code",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBengali) "$target এর জন্য আপনার ৬-সংখ্যার লগইন ওটিপি কোড:" else "Your 6-digit login OTP code for $target:",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 6-Digit Stylized OTP Display Box
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    otpCode.forEach { char ->
                        Card(
                            modifier = Modifier.size(40.dp, 50.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                            border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF0F766E))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = char.toString(),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF0F766E)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Proceed Button
                Button(
                    onClick = onProceed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Text(
                        text = if (isBengali) "কোড দিয়ে ভেরিফাই করুন" else "Proceed to Verify",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun InvalidUserDialog(
    target: String,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onRegisterClick: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFDC2626), Color(0xFFEF4444))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonOff,
                        contentDescription = "Invalid User Icon",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isBengali) "ব্যবহারকারী পাওয়া যায়নি!" else "Invalid User / Account Not Found",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF1E293B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isBengali)
                        "'$target' নামে কোনো অ্যাকাউন্ট আমাদের ডাটাবেজে পাওয়া যায়নি। ওটিপি পাওয়ার পূর্বে অনুগ্রহ করে একটি নতুন অ্যাকাউন্ট রেজিস্ট্রেশন করুন।"
                    else
                        "No registered account found for '$target'. Please create a new account to receive an OTP and log in.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onRegisterClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "নতুন অ্যাকাউন্ট তৈরি করুন" else "Create New Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "আবার চেষ্টা করুন" else "Try Again",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }
    }
}
