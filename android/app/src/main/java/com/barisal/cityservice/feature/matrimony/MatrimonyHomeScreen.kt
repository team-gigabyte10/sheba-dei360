package com.barisal.cityservice.feature.matrimony

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrimonyHomeScreen(
    onNavigateBack: () -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToProfile: (String) -> Unit,
    onNavigateToCreateProfile: () -> Unit = {}
) {
    val context = LocalContext.current
    var profilesList by remember { mutableStateOf(mockProfiles) }
    var showNotificationsDialog by remember { mutableStateOf(false) }
    var showFavoritesDialog by remember { mutableStateOf(false) }
    var showMyBiodataDialog by remember { mutableStateOf(false) }
    
    var selectedGenderFilter by remember { mutableStateOf<String?>(null) }
    var favoriteProfileIds by remember { mutableStateOf(setOf("2")) }
    var selectedNavIndex by remember { mutableStateOf(0) }

    val filteredProfiles = remember(profilesList, selectedGenderFilter) {
        if (selectedGenderFilter == null) profilesList
        else profilesList.filter { it.gender.equals(selectedGenderFilter, ignoreCase = true) }
    }

    if (showNotificationsDialog) {
        MatrimonyNotificationsDialog(onDismiss = { showNotificationsDialog = false })
    }

    if (showFavoritesDialog) {
        FavoritesListDialog(
            favoriteProfiles = profilesList.filter { favoriteProfileIds.contains(it.id) },
            onDismiss = { showFavoritesDialog = false },
            onProfileClick = { profileId ->
                showFavoritesDialog = false
                onNavigateToProfile(profileId)
            }
        )
    }

    if (showMyBiodataDialog) {
        MyBiodataDialog(
            myProfile = profilesList.firstOrNull(),
            onDismiss = { showMyBiodataDialog = false },
            onCreateNew = {
                showMyBiodataDialog = false
                onNavigateToCreateProfile()
            }
        )
    }

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
                    IconButton(onClick = { showNotificationsDialog = true }) {
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
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateProfile,
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Post") },
                text = { Text("বায়োডাটা পোস্ট", fontWeight = FontWeight.Bold) },
                containerColor = Color(0xFFBE123C), // Matrimony Rose Maroon theme
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            )
        },
        bottomBar = {
            MatrimonyBottomNav(
                selectedNavIndex = selectedNavIndex,
                onTabSelected = { index ->
                    selectedNavIndex = index
                    when (index) {
                        1 -> onNavigateToSearch()
                        2 -> showFavoritesDialog = true
                        3 -> showNotificationsDialog = true
                        4 -> showMyBiodataDialog = true
                    }
                }
            )
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
                        .background(Color(0xFFBE123C)) // Rich Rose Maroon theme color
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
                                Text("খুঁজুন", color = Color(0xFFBE123C), fontWeight = FontWeight.Bold)
                            }
                        }
                        
                        // Using an icon as a placeholder for the illustration
                        Box(
                            modifier = Modifier
                                .size(90.dp)
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
                    val isGroomActive = selectedGenderFilter == "Male"
                    val isBrideActive = selectedGenderFilter == "Female"

                    MatrimonyCategoryItem(
                        icon = Icons.Default.Male, 
                        title = if (isGroomActive) "সকল পাত্রী" else "পাত্র খুঁজুন", 
                        iconColor = if (isGroomActive) Color.White else Color(0xFF2563EB),
                        backgroundColor = if (isGroomActive) Color(0xFF2563EB) else Color.White
                    ) {
                        selectedGenderFilter = if (selectedGenderFilter == "Male") null else "Male"
                        Toast.makeText(context, if (selectedGenderFilter == "Male") "শুধুমাত্র পাত্র ফিল্টার করা হয়েছে" else "সকল প্রোফাইল দেখাচ্ছে", Toast.LENGTH_SHORT).show()
                    }

                    MatrimonyCategoryItem(
                        icon = Icons.Default.Female, 
                        title = if (isBrideActive) "সকল পাত্র" else "পাত্রী খুঁজুন", 
                        iconColor = if (isBrideActive) Color.White else Color(0xFFBE123C),
                        backgroundColor = if (isBrideActive) Color(0xFFBE123C) else Color.White
                    ) {
                        selectedGenderFilter = if (selectedGenderFilter == "Female") null else "Female"
                        Toast.makeText(context, if (selectedGenderFilter == "Female") "শুধুমাত্র পাত্রী ফিল্টার করা হয়েছে" else "সকল প্রোফাইল দেখাচ্ছে", Toast.LENGTH_SHORT).show()
                    }

                    MatrimonyCategoryItem(
                        icon = Icons.Default.Favorite, 
                        title = "পছন্দের তালিকা", 
                        iconColor = Color(0xFFE11D48)
                    ) {
                        showFavoritesDialog = true
                    }

                    MatrimonyCategoryItem(
                        icon = Icons.Default.History, 
                        title = "আমার বায়োডাটা", 
                        iconColor = Color(0xFF7C3AED)
                    ) {
                        showMyBiodataDialog = true
                    }
                }
            }

            // Filter status banner if active
            if (selectedGenderFilter != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF1F2))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedGenderFilter == "Male") "ফিল্টার: পাত্র (Groom)" else "ফিল্টার: পাত্রী (Bride)",
                            color = Color(0xFFBE123C),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "রিসেট",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.clickable { selectedGenderFilter = null }
                        )
                    }
                }
            }

            // Our Suggestions
            item {
                SectionHeader("আমাদের অনুমান", "সব দেখুন", onActionClick = onNavigateToSearch)
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProfiles) { profile ->
                        val isFav = favoriteProfileIds.contains(profile.id)
                        ProfileCard(
                            profile = profile,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                favoriteProfileIds = if (isFav) favoriteProfileIds - profile.id else favoriteProfileIds + profile.id
                                Toast.makeText(context, if (!isFav) "পছন্দের তালিকায় যুক্ত হয়েছে" else "পছন্দের তালিকা থেকে সরানো হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onClick = { onNavigateToProfile(profile.id) }
                        )
                    }
                }
            }

            // Recently Joined
            item {
                SectionHeader("সাম্প্রতিকভাবে যুক্ত", "সব দেখুন", onActionClick = onNavigateToSearch)
            }
            
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredProfiles.reversed()) { profile ->
                        val isFav = favoriteProfileIds.contains(profile.id)
                        ProfileCard(
                            profile = profile,
                            isFavorite = isFav,
                            onFavoriteToggle = {
                                favoriteProfileIds = if (isFav) favoriteProfileIds - profile.id else favoriteProfileIds + profile.id
                                Toast.makeText(context, if (!isFav) "পছন্দের তালিকায় যুক্ত হয়েছে" else "পছন্দের তালিকা থেকে সরানো হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            onClick = { onNavigateToProfile(profile.id) }
                        )
                    }
                }
            }
            
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
fun SectionHeader(title: String, actionText: String, onActionClick: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        Text(
            text = actionText, 
            fontSize = 14.sp, 
            color = Color(0xFFBE123C), 
            fontWeight = FontWeight.Medium, 
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}

@Composable
fun MatrimonyCategoryItem(
    icon: ImageVector, 
    title: String, 
    iconColor: Color, 
    backgroundColor: Color = Color.White,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(backgroundColor)
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
fun ProfileCard(
    profile: MatrimonyProfile,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onClick: () -> Unit
) {
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
                    .background(Color(0xFFF1F5F9))
            ) {
                if (profile.imageUri != null) {
                    AsyncImage(
                        model = profile.imageUri,
                        contentDescription = profile.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.Person, 
                        contentDescription = null, 
                        tint = Color.Gray,
                        modifier = Modifier.align(Alignment.Center).size(60.dp)
                    )
                }
                
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFFBE123C))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text("ম্যাচ ${profile.matchPercentage}%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onFavoriteToggle,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color(0xFFE11D48) else Color.DarkGray
                    )
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(profile.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B), maxLines = 1)
                Spacer(modifier = Modifier.height(4.dp))
                Text("${profile.age} বছর, ${profile.profession}", fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text(profile.location, fontSize = 12.sp, color = Color(0xFF64748B), maxLines = 1)
            }
        }
    }
}

@Composable
fun MatrimonyBottomNav(
    selectedNavIndex: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("হোম") },
            selected = selectedNavIndex == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFBE123C),
                selectedTextColor = Color(0xFFBE123C),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("খুঁজুন") },
            selected = selectedNavIndex == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFBE123C),
                selectedTextColor = Color(0xFFBE123C),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("পছন্দ") },
            selected = selectedNavIndex == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFBE123C),
                selectedTextColor = Color(0xFFBE123C),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = "Notifications") },
            label = { Text("বিজ্ঞপ্তি") },
            selected = selectedNavIndex == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFBE123C),
                selectedTextColor = Color(0xFFBE123C),
                indicatorColor = Color.Transparent,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("প্রোফাইল") },
            selected = selectedNavIndex == 4,
            onClick = { onTabSelected(4) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color(0xFFBE123C),
                selectedTextColor = Color(0xFFBE123C),
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
    val gender: String = "Male",
    val age: Int,
    val height: String,
    val weight: String = "৬৫ কেজি",
    val complexion: String = "উজ্জ্বল ফর্সা",
    val bloodGroup: String = "B+",
    val maritalStatus: String = "অবিবাহিত",
    val religion: String = "ইসলাম",
    val sect: String = "সুন্নি",
    val profession: String,
    val monthlyIncome: String = "৫০,০০০+ টাকা",
    val education: String,
    val institute: String = "ঢাকা বিশ্ববিদ্যালয়",
    val location: String,
    val permanentAddress: String = "পাবনা সদর, পাবনা",
    val fatherOccupation: String = "অবসরপ্রাপ্ত সরকারি কর্মকর্তা",
    val motherOccupation: String = "গৃহিনী",
    val siblings: String = "১ ভাই, ১ বোন",
    val partnerExpectationAge: String = "২০ - ২৫ বছর",
    val partnerExpectationHeight: String = "৫'২\" - ৫'৬\"",
    val partnerExpectationEducation: String = "স্নাতক / স্নাতকোত্তর",
    val partnerExpectationProfession: String = "যেকোনো সম্মাজনক পেশা / গৃহিনী",
    val partnerExpectationLocation: String = "ঢাকা / পাবনা / রাজসাহি বিভাগ",
    val matchPercentage: Int,
    val about: String = "ধার্মিক, সৎ, মার্জিত ও দায়িত্বশীল স্বভাবের। নিয়মিত সালাত আদায় করি এবং পরিবারকেন্দ্রিক মানসিকতা পোষণ করি।",
    val imageUri: String? = null
)

val mockProfiles = listOf(
    MatrimonyProfile(
        id = "1",
        name = "রিফাত হাসান",
        gender = "Male",
        age = 28,
        height = "৫'৮\"",
        weight = "৬৮ কেজি",
        complexion = "উজ্জ্বল শ্যামলা",
        bloodGroup = "B+",
        maritalStatus = "অবিবাহিত",
        religion = "ইসলাম",
        sect = "সুন্নি",
        profession = "সফটওয়্যার ইঞ্জিনিয়ার",
        monthlyIncome = "৮৫,০০০ টাকা",
        education = "BSc in Computer Science",
        institute = "BUET",
        location = "মিরপুর, ঢাকা",
        permanentAddress = "পাবনা সদর, পাবনা",
        fatherOccupation = "অবসরপ্রাপ্ত ব্যাংক কর্মকর্তা",
        motherOccupation = "গৃহিনী",
        siblings = "২ ভাই, ১ বোন",
        partnerExpectationAge = "২০ - ২৫ বছর",
        partnerExpectationHeight = "৫'২\" - ৫'৬\"",
        partnerExpectationEducation = "স্নাতক বা সমমান",
        partnerExpectationProfession = "যেকোনো সম্মানজনক চাকরি বা গৃহিনী",
        partnerExpectationLocation = "ঢাকা / পাবনা / রাজশাহী বিভাগ",
        matchPercentage = 85,
        about = "পেশাগতভাবে সফটওয়্যার ইঞ্জিনিয়ার। মার্জিত স্বভাবের, নিয়মিত সালাত আদায় করি ও পরিবারকে সর্বাধিক গুরুত্ব দিই।"
    ),
    MatrimonyProfile(
        id = "2",
        name = "আফসানা আক্তার",
        gender = "Female",
        age = 24,
        height = "৫'৩\"",
        weight = "৫২ কেজি",
        complexion = "ফর্সা",
        bloodGroup = "O+",
        maritalStatus = "অবিবাহিত",
        religion = "ইসলাম",
        sect = "সুন্নি",
        profession = "ডাক্তার (মেডিকেল অফিসার)",
        monthlyIncome = "৬০,০০০ টাকা",
        education = "MBBS",
        institute = "ঢাকা মেডিকেল কলেজ",
        location = "জিন্দাবাজার, সিলেট",
        permanentAddress = "সিলেট সদর, সিলেট",
        fatherOccupation = "ব্যবসায়ী",
        motherOccupation = "শিক্ষিকা",
        siblings = "১ ভাই, ১ বোন",
        partnerExpectationAge = "২৬ - ৩০ বছর",
        partnerExpectationHeight = "৫'৭\" - ৬'০\"",
        partnerExpectationEducation = "MBBS / ইঞ্জিনিয়ার / সমমান",
        partnerExpectationProfession = "ডাক্তার, ইঞ্জিনিয়ার বা ১ম শ্রেণীর কর্মকর্তা",
        partnerExpectationLocation = "ঢাকা / সিলেট",
        matchPercentage = 92,
        about = "আমি পেশায় একজন চিকিৎসক। ধর্মপরায়ণ ও পরিবারের প্রতি অনুগত একজন সৎ জীবনসঙ্গী প্রত্যাশা করছি।"
    ),
    MatrimonyProfile(
        id = "3",
        name = "তানভীর রহমান",
        gender = "Male",
        age = 30,
        height = "৫'৯\"",
        weight = "৭২ কেজি",
        complexion = "উজ্জ্বল ফর্সা",
        bloodGroup = "A+",
        maritalStatus = "অবিবাহিত",
        religion = "ইসলাম",
        sect = "সুন্নি",
        profession = "সিনিয়র ব্যাংকার",
        monthlyIncome = "৯৫,০০০ টাকা",
        education = "MBA in Finance",
        institute = "আইবিএ, ঢাকা বিশ্ববিদ্যালয়",
        location = "জিইসি, চট্টগ্রাম",
        permanentAddress = "হালিশহর, চট্টগ্রাম",
        fatherOccupation = "সাবেক প্রফেসর",
        motherOccupation = "গৃহিনী",
        siblings = "১ ভাই",
        partnerExpectationAge = "২২ - ২৭ বছর",
        partnerExpectationHeight = "৫'৩\" - ৫'৭\"",
        partnerExpectationEducation = "স্নাতকোত্তর",
        partnerExpectationProfession = "যেকোনো মার্জিত চাকরি বা গৃহিনী",
        partnerExpectationLocation = "চট্টগ্রাম / ঢাকা",
        matchPercentage = 78,
        about = "উচ্চশিক্ষিত ও পরিবার সচেতন। নম্র ও মার্জিত জীবনসঙ্গী খুঁজছি।"
    )
)



@Composable
fun MatrimonyNotificationsDialog(onDismiss: () -> Unit) {
    val sampleNotifications = listOf(
        "আপনার বায়োডাটা একজন অভিভাবক পছন্দ করেছেন।" to "১০ মিনিট আগে",
        "নতুন ২টি উপযোগী পাত্রের বায়োডাটা যুক্ত হয়েছে।" to "১ ঘণ্টা আগে",
        "আপনার বায়োডাটা ৫০+ বার দেখা হয়েছে।" to "গতকাল"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "বিজ্ঞপ্তি (Notifications)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFBE123C)
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sampleNotifications.forEach { (text, time) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFF1F2))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null,
                            tint = Color(0xFFBE123C),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(text, fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                            Text(time, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C))
            ) {
                Text("ঠিক আছে", color = Color.White)
            }
        }
    )
}

@Composable
fun FavoritesListDialog(
    favoriteProfiles: List<MatrimonyProfile>,
    onDismiss: () -> Unit,
    onProfileClick: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "পছন্দের তালিকা (${favoriteProfiles.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFBE123C)
            )
        },
        text = {
            if (favoriteProfiles.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "আপনার পছন্দের তালিকায় এখনো কোনো বায়োডাটা যুক্ত হয়নি।",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    favoriteProfiles.forEach { profile ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProfileClick(profile.id) },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color(0xFFBE123C),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(profile.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("${profile.age} বছর, ${profile.profession}", fontSize = 12.sp, color = Color.Gray)
                                }
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = null,
                                    tint = Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C))
            ) {
                Text("বন্ধ করুন", color = Color.White)
            }
        }
    )
}

@Composable
fun MyBiodataDialog(
    myProfile: MatrimonyProfile?,
    onDismiss: () -> Unit,
    onCreateNew: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "আমার বায়োডাটা (My Biodata)",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFFBE123C)
            )
        },
        text = {
            if (myProfile == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "আপনি এখনো কোনো বায়োডাটা তৈরি বা পোস্ট করেননি।",
                        color = Color.Gray,
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onCreateNew,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C))
                    ) {
                        Text("নতুন বায়োডাটা তৈরি করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "নাম: ${myProfile.name}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(text = "পেশা: ${myProfile.profession}", fontSize = 13.sp)
                    Text(text = "শিক্ষাগত যোগ্যতা: ${myProfile.education}", fontSize = 13.sp)
                    Text(text = "ঠিকানা: ${myProfile.location}", fontSize = 13.sp)
                    Text(text = "উচ্চতা: ${myProfile.height} | বয়স: ${myProfile.age} বছর", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "সংক্ষিপ্ত বিবরণ:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = myProfile.about, fontSize = 12.sp, color = Color.DarkGray)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFBE123C))
            ) {
                Text("ঠিক আছে", color = Color.White)
            }
        }
    )
}
