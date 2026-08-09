package com.barisal.cityservice.feature.settings

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
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
import com.barisal.cityservice.core.language.AppLanguage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.theme.AppThemeMode
import com.barisal.cityservice.core.theme.LocalAppTheme
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

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

    var notificationsEnabled by remember { mutableStateOf(true) }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "সেটিংস" else "Settings",
                onBackClick = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
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

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Theme Control Section
            item {
                SectionTitle(if (isBengali) "থিম মোড" else "Theme Mode")
            }
            item {
                ThemeSelectorCard(
                    currentMode = themeState.themeMode,
                    isBengali = isBengali,
                    onModeSelected = { mode ->
                        themeState.updateThemeMode(mode)
                    }
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
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color.White)
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
        color = MaterialTheme.colorScheme.primary,
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
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
fun SettingsSwitchItem(icon: ImageVector, title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun ThemeSelectorCard(
    currentMode: AppThemeMode,
    isBengali: Boolean,
    onModeSelected: (AppThemeMode) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isBengali) "অ্যাপের থিম নির্বাচন করুন" else "Select App Theme",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ThemeOptionChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LightMode,
                    title = if (isBengali) "লাইট" else "Light",
                    isSelected = currentMode == AppThemeMode.LIGHT,
                    onClick = { onModeSelected(AppThemeMode.LIGHT) }
                )

                ThemeOptionChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.DarkMode,
                    title = if (isBengali) "ডার্ক" else "Dark",
                    isSelected = currentMode == AppThemeMode.DARK,
                    onClick = { onModeSelected(AppThemeMode.DARK) }
                )

                ThemeOptionChip(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.PhoneAndroid,
                    title = if (isBengali) "সিস্টেম" else "System",
                    isSelected = currentMode == AppThemeMode.SYSTEM,
                    onClick = { onModeSelected(AppThemeMode.SYSTEM) }
                )
            }
        }
    }
}

@Composable
fun ThemeOptionChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        label = "chipBgColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "chipContentColor"
    )

    val borderColor by animateColorAsState(
        targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
        label = "chipBorderColor"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(width = 1.5.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = contentColor,
                modifier = Modifier.size(24.dp)
            )
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(12.dp)
                        .offset(x = 6.dp, y = (-4).dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )
    }
}
