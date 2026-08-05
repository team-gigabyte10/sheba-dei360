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
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

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

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    SetStatusBarColor()

    val primaryTeal = Color(0xFF0F766E)
    val textDark = Color(0xFF1E293B)
    val textMuted = Color(0xFF64748B)

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
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(primaryTeal.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "App Logo",
                                modifier = Modifier.size(48.dp)
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
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(if (isBengali) "পুরো নাম *" else "Full Name *") },
                            placeholder = { Text(if (isBengali) "আপনার নাম লিখুন" else "Enter your full name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryTeal,
                                unfocusedBorderColor = Color(0xFFE2E8F0)
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(if (isBengali) "ইমেইল এড্রেস *" else "Email Address *") },
                            placeholder = { Text("user@example.com") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = primaryTeal) },
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

                        // Phone Field
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBengali) "ফোন নম্বর (ঐচ্ছিক)" else "Phone Number (Optional)") },
                            placeholder = { Text("+88017XXXXXXXX") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryTeal) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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

                        Spacer(modifier = Modifier.height(22.dp))

                        // Register Button
                        Button(
                            onClick = {
                                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "অনুগ্রহ করে নাম, ইমেইল ও পাসওয়ার্ড লিখুন" else "Please enter your name, email, and password",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                if (password.length < 6) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "পাসওয়ার্ড কমপক্ষে ৬ অক্ষরের হতে হবে" else "Password must be at least 6 characters",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isLoading = true
                                val firebaseAuth = FirebaseAuth.getInstance()

                                firebaseAuth.createUserWithEmailAndPassword(email.trim(), password)
                                    .addOnCompleteListener { task ->
                                        isLoading = false
                                        if (task.isSuccessful) {
                                            val user = firebaseAuth.currentUser
                                            val profileUpdates = UserProfileChangeRequest.Builder()
                                                .setDisplayName(name.trim())
                                                .build()
                                            user?.updateProfile(profileUpdates)
                                            com.barisal.cityservice.core.utils.UserPreferences.setOtpVerified(context, true)
                                            Toast.makeText(
                                                context,
                                                if (isBengali) "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে!" else "Account created successfully!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onNavigateToHome()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                if (isBengali) "নিবন্ধন ব্যর্থ: ${task.exception?.message}" else "Registration failed: ${task.exception?.message}",
                                                Toast.LENGTH_LONG
                                            ).show()
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
                                Icon(Icons.Default.PersonAdd, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "অ্যাকাউন্ট তৈরি করুন" else "Create Account",
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
