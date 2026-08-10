package com.barisal.cityservice.feature.tutor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.barisal.cityservice.R
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.repository.TutorRepository

data class TutorProfile(
    val id: String = "",
    val name: String,
    val date: String,
    val bio: String,
    val classRange: String,
    val daysPerWeek: String,
    val subject: String,
    val salary: String,
    val gender: String,
    val address: String,
    val thana: String,
    val phone: String = "01712345678",
    val postType: String = "tutor",
    val profileImageUrl: String = "",
    val selectedSubjects: List<String> = emptyList()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val tutorRepository = remember { TutorRepository() }
    val firestorePostsState by tutorRepository.getApprovedTutorPosts().collectAsState(initial = emptyList())

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("পড়াতে চাই", "শিক্ষক চাই")
    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    var showTutorMap by remember { mutableStateOf(false) }
    var selectedTutorForMap by remember { mutableStateOf<TutorProfile?>(null) }
    var showCreatePostDialog by remember { mutableStateOf(false) }

    var selectedSubCategory by remember { mutableStateOf("সব") }
    val tutorSubCategories = listOf(
        "সব",
        "১ম–৫ম",
        "৬ষ্ঠ–৮ম",
        "৯ম–১০ম",
        "এইচএসসি",
        "পদার্থবিজ্ঞান",
        "রসায়ন",
        "উচ্চতর গণিত",
        "সাধারণ গণিত",
        "জীববিজ্ঞান",
        "আইসিটি",
        "ইংরেজি",
        "হিসাববিজ্ঞান",
        "আরবি/কুরআন",
        "নৃত্য শিক্ষক",
        "সঙ্গীত শিক্ষক"
    )

    val dummyTutors = remember {
        mutableStateListOf(
            TutorProfile(
                name = "সুজন শেখ",
                date = "30 Dec 2024",
                bio = "আমি রাজেন্দ্র কলেজ এ বোটানি ডিপার্টমেন্ট ৩য় বর্ষের ছাত্র।",
                classRange = "১ম–৫ম",
                daysPerWeek = "৪ দিন/সপ্তাহে",
                subject = "সকল বিষয় (প্রাথমিক)",
                salary = "৳৩,৫০০ টাকা",
                gender = "ছেলে ও মেয়ে",
                address = "উপজেলা ভুমি অফিস, ঝিলটুলি",
                thana = "ফরিদপুর সদর",
                phone = "01712345678",
                postType = "tutor"
            ),
            TutorProfile(
                name = "শেখ ফেরদৌস",
                date = "13 Oct 2025",
                bio = "বাণিজ্য বিভাগের অভিজ্ঞ শিক্ষক।",
                classRange = "৬ষ্ঠ–১০ম",
                daysPerWeek = "৪ দিন/সপ্তাহে",
                subject = "বাণিজ্য বিভাগ ও সাধারণ গণিত",
                salary = "৳৪,৫০০ টাকা",
                gender = "ছেলে",
                address = "মেডিকেল কলেজ সংলগ্ন",
                thana = "ফরিদপুর সদর",
                phone = "01898765432",
                postType = "tutor"
            ),
            TutorProfile(
                name = "সজীব রায় মৃত্যুঞ্জয়",
                date = "17 Dec 2024",
                bio = "পদার্থবিজ্ঞানে স্নাতকোত্তর (ঢাকা বিশ্ববিদ্যালয়)।",
                classRange = "এইচএসসি",
                daysPerWeek = "৩ দিন/সপ্তাহে",
                subject = "পদার্থবিজ্ঞান, উচ্চতর গণিত",
                salary = "৬,০০০ টাকা",
                gender = "ছেলে ও মেয়ে",
                address = "মিরপুর ১০, ঢাকা",
                thana = "পল্লবী",
                phone = "01911223344",
                postType = "tutor"
            ),
            TutorProfile(
                name = "হাফেজ ক্বারী মাওলানা আব্দুল্লাহ",
                date = "20 Jan 2025",
                bio = "হাফেজে কুরআন ও অভিজ্ঞ আরবি শিক্ষক। সহিহ তাজবীদ সহ কুরআন শিক্ষা দেওয়া হয়।",
                classRange = "আরবি/কুরআন শিক্ষা",
                daysPerWeek = "৫ দিন/সপ্তাহে",
                subject = "তাজবীদ সহ কুরআন শিক্ষা ও আরবি ভাষা",
                salary = "৳৪,০০০ টাকা",
                gender = "ছেলে ও মেয়ে",
                address = "উত্তরা সেক্টর ৭, ঢাকা",
                thana = "উত্তরা",
                phone = "01755667788",
                postType = "tutor"
            )
        )
    }

    val firestoreTutors = remember(firestorePostsState) {
        firestorePostsState.map { dto ->
            TutorProfile(
                id = dto.id,
                name = dto.name,
                date = if (dto.date.isNotBlank()) dto.date else "সাম্প্রতিক",
                bio = dto.bio,
                classRange = dto.classRange,
                daysPerWeek = dto.daysPerWeek,
                subject = dto.subject,
                salary = dto.salary,
                gender = dto.gender,
                address = dto.address,
                thana = dto.thana,
                phone = dto.phone,
                postType = dto.postType,
                profileImageUrl = dto.profileImageUrl,
                selectedSubjects = dto.selectedSubjects
            )
        }
    }

    val allTutors = remember(dummyTutors.size, firestoreTutors) {
        firestoreTutors + dummyTutors
    }

    val activePostType = if (selectedTabIndex == 0) "tutor" else "student"

    val filteredTutors = remember(searchQuery, selectedSubCategory, selectedZilla, activePostType, allTutors) {
        allTutors.filter { tutor ->
            val matchesTab = tutor.postType.equals(activePostType, ignoreCase = true)
            val matchesQuery = searchQuery.isEmpty() ||
                    tutor.name.contains(searchQuery, ignoreCase = true) ||
                    tutor.subject.contains(searchQuery, ignoreCase = true) ||
                    tutor.address.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" ||
                    tutor.classRange.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(tutor.classRange, ignoreCase = true) ||
                    tutor.subject.contains(selectedSubCategory, ignoreCase = true) ||
                    tutor.selectedSubjects.any { it.contains(selectedSubCategory, ignoreCase = true) }
            val matchesZilla = selectedZilla == null || tutor.address.contains(selectedZilla!!, ignoreCase = true) || tutor.thana.contains(selectedZilla!!, ignoreCase = true)
            matchesTab && matchesQuery && matchesCategory && matchesZilla
        }
    }

    if (showTutorMap) {
        BackHandler {
            showTutorMap = false
        }
        TutorMapScreen(
            initialTutor = selectedTutorForMap,
            selectedZilla = selectedZilla,
            onBack = { showTutorMap = false }
        )
    } else if (showCreatePostDialog) {
        CreateTutorPostScreen(
            onBack = { showCreatePostDialog = false },
            onPostCreated = { newPost ->
                dummyTutors.add(0, newPost)
                showCreatePostDialog = false
            }
        )
    } else {
        Scaffold(
            topBar = {
                Column {
                    com.barisal.cityservice.ui.components.GlobalAppBar(
                        title = "টিউটর",
                        onBackClick = onNavigateBack,
                        actions = {
                            IconButton(onClick = { showZillaFilterDialog = true }) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                            }
                        }
                    )
                    
                    // Tabs
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.White,
                        contentColor = Color(0xFF0F766E),
                        indicator = { tabPositions ->
                            TabRowDefaults.Indicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                                color = Color(0xFF0F766E),
                                height = 3.dp
                            )
                        }
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = {
                                    Text(
                                        text = title,
                                        fontSize = 15.sp,
                                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                                        color = if (selectedTabIndex == index) Color(0xFF0F766E) else Color.Gray
                                    )
                                }
                            )
                        }
                    }
                    
                    // Search Bar and Count Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp)),
                            placeholder = {
                                Text(
                                    text = "খুঁজুন...",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = Color(0xFF0F766E)
                                )
                            },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                disabledContainerColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color(0xFF0F766E)
                            ),
                            singleLine = true
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        // Counter Badge
                        Box(
                            modifier = Modifier
                                .height(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFCCFBF1)) // Light Teal
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${filteredTutors.size}",
                                color = Color.DarkGray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // Sub-category Filter Chips
                    androidx.compose.foundation.lazy.LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.background(Color(0xFFF8FAFC))
                    ) {
                        items(tutorSubCategories) { cat ->
                            val isSelected = cat == selectedSubCategory
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color(0xFF0F766E) else Color.White)
                                    .border(1.dp, if (isSelected) Color.Transparent else Color.LightGray, RoundedCornerShape(20.dp))
                                    .clickable { selectedSubCategory = cat }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSelected) Color.White else Color.DarkGray,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showCreatePostDialog = true },
                    containerColor = Color(0xFF0F766E),
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Post")
                }
            }
        ) { innerPadding ->
            if (showZillaFilterDialog) {
                com.barisal.cityservice.ui.components.CustomDialog(
                    onDismissRequest = { showZillaFilterDialog = false },
                    title = "জেলা নির্বাচন করুন",
                    confirmButtonText = "বন্ধ করুন",
                    onConfirm = { showZillaFilterDialog = false }
                ) {
                    androidx.compose.foundation.lazy.LazyColumn(
                        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                    ) {
                        item {
                            TextButton(onClick = { 
                                selectedZilla = null
                                showZillaFilterDialog = false 
                            }) {
                                Text("রিসেট", color = Color.Red, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                            }
                        }
                        items(com.barisal.cityservice.core.utils.bangladeshZillas) { zilla ->
                            TextButton(
                                onClick = { 
                                    selectedZilla = zilla
                                    showZillaFilterDialog = false 
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = zilla,
                                    color = if (selectedZilla == zilla) Color(0xFF1E3A8A) else Color.Black,
                                    fontWeight = if (selectedZilla == zilla) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
            
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8FAFC))
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 80.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredTutors) { tutor ->
                    TutorCard(
                        tutor = tutor,
                        onContactClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${tutor.phone}"))
                            context.startActivity(intent)
                        },
                        onMapClick = {
                            selectedTutorForMap = tutor
                            showTutorMap = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TutorCard(
    tutor: TutorProfile,
    onContactClick: () -> Unit = {},
    onMapClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Profile Image
                val profileModel = remember(tutor.profileImageUrl) { tutor.profileImageUrl.toCoilModel() }
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileModel != null) {
                        AsyncImage(
                            model = profileModel,
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "Profile",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = tutor.name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = tutor.date,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Bio
            Text(
                text = tutor.bio,
                fontSize = 15.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Details Grid
            Row(modifier = Modifier.fillMaxWidth()) {
                // Left Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TutorDetailRow(icon = Icons.Default.MenuBook, label = "ক্লাস", value = tutor.classRange)
                    TutorDetailRow(icon = Icons.Default.MenuBook, label = "বিষয়", value = tutor.subject)
                    TutorDetailRow(icon = Icons.Default.Wc, label = "লিঙ্গ", value = tutor.gender)
                    TutorDetailRow(icon = Icons.Default.AltRoute, label = "থানা", value = tutor.thana)
                }
                // Right Column
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TutorDetailRow(icon = Icons.Default.CalendarMonth, label = "দিন", value = tutor.daysPerWeek)
                    TutorDetailRow(icon = Icons.Default.AttachMoney, label = "বেতন", value = tutor.salary)
                    TutorDetailRow(icon = Icons.Default.LocationOn, label = "ঠিকানা", value = tutor.address)
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onContactClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("যোগাযোগ করুন", fontSize = 15.sp)
                }
                
                Button(
                    onClick = onMapClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("গুগল ম্যাপ", fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun TutorDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(0xFFE2E8F0)), // Light Grayish Blue
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 18.sp
            )
        }
    }
}
