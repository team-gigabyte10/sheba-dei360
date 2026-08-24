package com.barisal.cityservice.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.R
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToUpdateProfile: () -> Unit = {},
    onNavigateToChangePassword: () -> Unit = {}
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor()
    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "প্রোফাইল" else "My Profile"
            )
        },
        bottomBar = {
            val isAdmin = currentUser != null && com.barisal.cityservice.core.utils.UserPreferences.isAdmin(context)
            if (currentUser != null && !isAdmin) {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(if (isBengali) "হোম" else "Home") },
                        selected = false,
                        onClick = { onNavigateToHome() },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F766E),
                            selectedTextColor = Color(0xFF0F766E),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Bookings") },
                        label = { Text(if (isBengali) "বুকিংস" else "Bookings") },
                        selected = false,
                        onClick = { onNavigateToBookings() },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F766E),
                            selectedTextColor = Color(0xFF0F766E),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(if (isBengali) "প্রোফাইল" else "Profile") },
                        selected = true,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF0F766E),
                            selectedTextColor = Color(0xFF0F766E),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(4.dp)) }

            // 1. Profile Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F766E)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color(0xFFFFC107), CircleShape)
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo),
                                contentDescription = "Profile Avatar",
                                modifier = Modifier
                                    .size(84.dp)
                                    .clip(CircleShape)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val isAdminUser = currentUser != null && com.barisal.cityservice.core.utils.UserPreferences.isAdmin(context)

                        val displayName = if (currentUser != null) {
                            currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: (if (isBengali) "ব্যবহারকারী" else "User")
                        } else {
                            if (isBengali) "অতিথি ব্যবহারকারী" else "Guest User"
                        }

                        val contactText = if (currentUser != null) {
                            currentUser.email ?: currentUser.phoneNumber ?: ""
                        } else {
                            if (isBengali) "লগইন করা নেই" else "Not logged in"
                        }

                        Text(
                            text = displayName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = contactText,
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isAdminUser) Color(0xFFDC2626) else Color(0xFFFFC107))
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdminUser) Icons.Default.AdminPanelSettings else Icons.Default.Star,
                                contentDescription = if (isAdminUser) "Admin" else "Member",
                                tint = if (isAdminUser) Color.White else Color(0xFF78350F),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAdminUser) (if (isBengali) "অ্যাডমিন" else "Admin") else (if (isBengali) "গোল্ড মেম্বার" else "Gold Member"),
                                color = if (isAdminUser) Color.White else Color(0xFF78350F),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // 2. Wallet Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF166534)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Wallet", tint = Color.White, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "আমার ওয়ালেট ব্যালেন্স" else "My Wallet Balance",
                                    fontSize = 13.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (isBengali) "৳ ১,৫০০.০০" else "৳1,500.00",
                                    fontSize = 18.sp,
                                    color = Color(0xFF166534),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Button(
                            onClick = { },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF166534)),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(if (isBengali) "রিচার্জ" else "Top Up", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 3. Menu Options List
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        ProfileMenuItem(
                            icon = Icons.Default.Edit,
                            text = if (isBengali) "প্রোফাইল আপডেট করুন" else "Edit Profile",
                            onClick = onNavigateToUpdateProfile
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuItem(
                            icon = Icons.Default.Lock,
                            text = if (isBengali) "পাসওয়ার্ড পরিবর্তন" else "Change Password",
                            onClick = onNavigateToChangePassword
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuItem(
                            icon = Icons.Default.History,
                            text = if (isBengali) "বুকিং ইতিহাস" else "Booking History",
                            onClick = onNavigateToBookings
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuItem(
                            icon = Icons.Default.Settings,
                            text = if (isBengali) "সেটিংস" else "Settings",
                            onClick = onNavigateToSettings
                        )
                        HorizontalDivider(color = Color(0xFFF1F5F9))
                        ProfileMenuItem(
                            icon = Icons.AutoMirrored.Filled.HelpOutline,
                            text = if (isBengali) "সাহায্য ও সাপোর্ট" else "Help & Support",
                            onClick = { }
                        )
                    }
                }
            }

            // 4. Logout Button
            item {
                Button(
                    onClick = onLogout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFDC2626)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isBengali) "লগ আউট করুন" else "Log Out",
                        color = Color(0xFFDC2626),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun ProfileMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = text, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(14.dp)
        )
    }
}
