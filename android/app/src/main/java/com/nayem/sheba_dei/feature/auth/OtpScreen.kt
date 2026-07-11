package com.nayem.sheba_dei.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nayem.sheba_dei.core.language.LocalAppLanguage

@Composable
fun OtpScreen(
    onVerifySuccess: () -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    val isBengali = LocalAppLanguage.current.isBengali

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (isBengali) "ওটিপি যাচাই করুন" else "Verify OTP",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = if (isBengali) "আপনার ফোন/ইমেলে পাঠানো ৪-সংখ্যার কোডটি লিখুন।" else "Enter the 4-digit code sent to your phone/email.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text(if (isBengali) "ওটিপি কোড" else "OTP Code") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onVerifySuccess() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (isBengali) "যাচাই করুন" else "Verify")
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { onBack() }) {
            Text(if (isBengali) "লগ-ইনে ফিরে যান" else "Back to Login")
        }
    }
}
