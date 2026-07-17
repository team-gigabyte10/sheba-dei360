package com.nayem.sheba_dei.feature.matrimony

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrimonyHomeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("পাত্র-পাত্রী", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO */ }) {
                        BadgedBox(
                            badge = { Badge { Text("৩") } }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black,
                    actionIconContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            MatrimonyBottomNav()
        },
        containerColor = Color(0xFFF8FAFC) // Light gray background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }
            
            // Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFE91E63)) // Pink theme color
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "সহজেই খুঁজুন\nআপনার জীবনের\nসঙ্গী",
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 28.sp
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onNavigateToSearch,
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                shape = RoundedCornerShape(20.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text("খুঁজতে শুরু করুন", color = Color(0xFFE91E63), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFFE91E63), modifier = Modifier.size(16.dp))
                            }
                        }
                        
                        // Using an icon as a placeholder for the illustration
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.People, contentDescription = null, tint = Color.White, modifier = Modifier.size(60.dp))
                        }
                    }
                }
            }

            // Categories
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MatrimonyCategoryItem(Icons.Default.Male, "পাত্র খুঁজুন", Color(0xFF3B82F6), onNavigateToSearch)
                    MatrimonyCategoryItem(Icons.Default.Female, "পাত্রী খুঁজুন", Color(0xFFE91E63), onNavigateToSearch)
                    MatrimonyCategoryItem(Icons.Default.Favorite, "পছন্দের তালিকা", Color(0xFFEF4444)) { }
                    MatrimonyCategoryItem(Icons.Default.History, "আমার বায়োডাটা", Color(0xFF8B5CF6)) { }
                }
            }

            // Our Suggestions
            item {
                SectionHeader("আমাদের অনুমান", "সব দেখুন")
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mockProfiles) { profile ->
                        ProfileCard(profile) { onNavigateToProfile(profile.id) }
                    }
                }
            }

            // Recently Joined
            item {
                SectionHeader("সাম্প্রতিকভাবে যুক্ত", "সব দেখুন")
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(mockProfiles.reversed()) { profile ->
                        ProfileCard(profile) { onNavigateToProfile(profile.id) }
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Text(text = actionText, fontSize = 14.sp, color = Color(0xFFE91E63), fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
    }
}

@Composable
fun MatrimonyCategoryItem(icon: ImageVector, title: String, iconColor: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = title, tint = iconColor, modifier = Modifier.size(30.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(title, fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@Composable
fun ProfileCard(profile: MatrimonyProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray)
            ) {
                // Placeholder for profile image
                Icon(
                    Icons.Default.Person, 
                    contentDescription = null, 
                    tint = Color.Gray,
                    modifier = Modifier.align(Alignment.Center).size(60.dp)
                )
                
                // Match percentage badge
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFE91E63))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text("ম্যাচ ${profile.matchPercentage}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(profile.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${profile.age} বছর, ${profile.profession}", fontSize = 12.sp, color = Color(0xFF64748B))
                Spacer(modifier = Modifier.height(2.dp))
                Text(profile.location, fontSize = 12.sp, color = Color(0xFF64748B))
            }
        }
    }
}

@Composable
fun MatrimonyBottomNav() {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("হোম") },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE91E63),
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.People, contentDescription = "Matrimony") },
            label = { Text("পাত্র-পাত্রী") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE91E63),
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
            label = { Text("চ্যাট") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE91E63),
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
            label = { Text("বিজ্ঞপ্তি") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE91E63),
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("প্রোফাইল") },
            selected = false,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFE91E63),
                selectedTextColor = Color(0xFFE91E63),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
    }
}

// Mock Data
data class MatrimonyProfile(
    val id: String,
    val name: String,
    val age: Int,
    val height: String,
    val profession: String,
    val education: String,
    val location: String,
    val matchPercentage: Int
)

val mockProfiles = listOf(
    MatrimonyProfile("1", "রিফাত হাসান", 28, "৫'৮\"", "সফটওয়্যার ইঞ্জিনিয়ার", "BSc in CSE", "ঢাকা, বাংলাদেশ", 85),
    MatrimonyProfile("2", "আফসানা আক্তার", 24, "৫'৩\"", "ডাক্তার", "MBBS", "সিলেট, বাংলাদেশ", 92),
    MatrimonyProfile("3", "তানভীর রহমান", 30, "৫'৯\"", "ব্যাংকার", "MBA", "চট্টগ্রাম, বাংলাদেশ", 78)
)
