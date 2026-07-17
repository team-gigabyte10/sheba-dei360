package com.nayem.sheba_dei.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var currentPasswordVisible by remember { mutableStateOf(false) }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    SetStatusBarColor()
    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "পাসওয়ার্ড পরিবর্তন করুন" else "Change Password",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Lock Icon Header
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE2E8F0)), // Slate 200
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF64748B)) // Slate 500
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBengali) "নতুন পাসওয়ার্ড তৈরি করুন" else "Create New Password",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B) // Slate 800
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isBengali) "আপনার নতুন পাসওয়ার্ড আগের ব্যবহৃত পাসওয়ার্ডগুলো থেকে ভিন্ন হতে হবে।" else "Your new password must be different from previous used passwords.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Current Password
            PasswordOutlinedTextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                label = if (isBengali) "বর্তমান পাসওয়ার্ড" else "Current Password",
                passwordVisible = currentPasswordVisible,
                onVisibilityChange = { currentPasswordVisible = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New Password
            PasswordOutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = if (isBengali) "নতুন পাসওয়ার্ড" else "New Password",
                passwordVisible = newPasswordVisible,
                onVisibilityChange = { newPasswordVisible = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            PasswordOutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = if (isBengali) "নতুন পাসওয়ার্ড নিশ্চিত করুন" else "Confirm New Password",
                passwordVisible = confirmPasswordVisible,
                onVisibilityChange = { confirmPasswordVisible = it }
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Update Button
            Button(
                onClick = {
                    if (newPassword == confirmPassword && newPassword.isNotEmpty()) {
                        Toast.makeText(context, if (isBengali) "পাসওয়ার্ড সফলভাবে আপডেট হয়েছে" else "Password updated successfully", Toast.LENGTH_SHORT).show()
                        onBack()
                    } else {
                        Toast.makeText(context, if (isBengali) "পাসওয়ার্ড মিলছে না" else "Passwords do not match", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2563EB) // Blue 600
                )
            ) {
                Text(
                    text = if (isBengali) "পাসওয়ার্ড আপডেট করুন" else "Update Password",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    passwordVisible: Boolean,
    onVisibilityChange: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = Color(0xFF64748B))
        },
        trailingIcon = {
            val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { onVisibilityChange(!passwordVisible) }) {
                Icon(imageVector = image, contentDescription = description, tint = Color(0xFF64748B))
            }
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFF2563EB),
            unfocusedBorderColor = Color.LightGray,
            focusedLabelColor = Color(0xFF2563EB),
            cursorColor = Color(0xFF2563EB)
        )
    )
}
