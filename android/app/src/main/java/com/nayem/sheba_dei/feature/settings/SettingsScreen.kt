package com.nayem.sheba_dei.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.AppLanguage
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.core.theme.LocalAppTheme
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToUpdateProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    val themeState = LocalAppTheme.current
    val isDarkMode = themeState.isDarkMode

    var notificationsEnabled by remember { mutableStateOf(true) }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "সেটিংস" else "Settings",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Account Section
            item {
                SectionTitle(if (isBengali) "অ্যাকাউন্ট" else "Account")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Person,
                    title = if (isBengali) "প্রোফাইল সম্পাদনা করুন" else "Edit Profile",
                    onClick = onNavigateToUpdateProfile
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Lock,
                    title = if (isBengali) "পাসওয়ার্ড পরিবর্তন করুন" else "Change Password",
                    onClick = onNavigateToChangePassword
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // App Settings Section
            item {
                SectionTitle(if (isBengali) "অ্যাপ সেটিংস" else "App Settings")
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = if (isBengali) "নোটিফিকেশন" else "Notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.Language,
                    title = if (isBengali) "বাংলা ভাষা" else "Bengali Language",
                    checked = isBengali,
                    onCheckedChange = { isBangla ->
                        languageState.currentLanguage = if (isBangla) AppLanguage.BENGALI else AppLanguage.ENGLISH
                    }
                )
            }
            item {
                SettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = if (isBengali) "ডার্ক মোড" else "Dark Mode",
                    checked = isDarkMode,
                    onCheckedChange = { themeState.isDarkMode = it }
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Information Section
            item {
                SectionTitle(if (isBengali) "তথ্য" else "Information")
            }
            item {
                SettingsItem(
                    icon = Icons.Default.Info,
                    title = if (isBengali) "আমাদের সম্পর্কে" else "About Us",
                    onClick = { /* TODO */ }
                )
            }
            item {
                SettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = if (isBengali) "গোপনীয়তা নীতি" else "Privacy Policy",
                    onClick = { /* TODO */ }
                )
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }

            // Logout Button
            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)), // Red
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBengali) "লগ আউট" else "Log Out", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFF1E3A8A), // Navy Blue
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
    )
}

@Composable
fun SettingsItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = Color.DarkGray, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = Color(0xFF1E3A8A)
            )
        )
    }
}
