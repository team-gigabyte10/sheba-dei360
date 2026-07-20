package com.nayem.sheba_dei.feature.tutor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.R

data class TutorProfile(
    val name: String,
    val date: String,
    val bio: String,
    val classRange: String,
    val daysPerWeek: String,
    val subject: String,
    val salary: String,
    val gender: String,
    val address: String,
    val thana: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("পড়াতে চাই", "শিক্ষক চাই")
    var searchQuery by remember { mutableStateOf("") }
    
    var showZillaFilterDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedZilla by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    val dummyTutors = listOf(
        TutorProfile(
            name = "Sujon Sheikh",
            date = "30 Dec 2024",
            bio = "আমি সুজন শেখ, রাজেন্দ্র কলেজ এ বোটানি ডিপার্টমেন্ট ৩য় বর্ষে ছাত্র।",
            classRange = "1-8",
            daysPerWeek = "4 / সপ্তাহে",
            subject = "all",
            salary = "আলোচনা সাপেক্ষ।",
            gender = "ছেলে ও মেয়ে",
            address = "উপজেলা ভুমি অফিস, ঝিলটুলি",
            thana = "ফরিদপুর সদর"
        ),
        TutorProfile(
            name = "Sheikh Ferdous",
            date = "13 Oct 2025",
            bio = "Ferdous Sheikh",
            classRange = "নবম- দশম",
            daysPerWeek = "4 / সপ্তাহে",
            subject = "বাণিজ্য বিভাগের সকল সাবজেক্ট",
            salary = "আলোচনা সাপেক্ষে",
            gender = "ছেলে",
            address = "মেডিকেল কলেজ সংলগ্ন।",
            thana = "ফরিদপুর সদর"
        ),
        TutorProfile(
            name = "সজীব রায় মৃত্যুঞ্জয়",
            date = "17 Dec 2024",
            bio = "আমি ঢাকা বিশ্ববিদ্যালয় থেকে পদার্থবিজ্ঞানে স্নাতকোত্তর সম্পন্ন করেছি।",
            classRange = "একাদশ-দ্বাদশ",
            daysPerWeek = "3 / সপ্তাহে",
            subject = "পদার্থবিজ্ঞান, উচ্চতর গণিত",
            salary = "৬০০০ টাকা",
            gender = "ছেলে ও মেয়ে",
            address = "মিরপুর ১০, ঢাকা",
            thana = "পল্লবী"
        )
    )

    Scaffold(
        topBar = {
            Column {
                com.nayem.sheba_dei.ui.components.GlobalAppBar(
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
                            .padding(horizontal = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "675",
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Add new tutor profile */ },
                containerColor = Color(0xFF0F766E),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            com.nayem.sheba_dei.ui.components.CustomDialog(
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
                    items(com.nayem.sheba_dei.core.utils.bangladeshZillas) { zilla ->
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
            items(dummyTutors) { tutor ->
                TutorCard(tutor = tutor)
            }
        }
    }
}

@Composable
fun TutorCard(tutor: TutorProfile) {
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
                // Profile Image Placeholder
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo), // Using logo as placeholder
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize()
                    )
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
                        fontSize = 12.sp,
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
                    onClick = { /* Contact */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("যোগাযোগ করুন", fontSize = 14.sp)
                }
                
                Button(
                    onClick = { /* Map */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("গুগল ম্যাপ", fontSize = 14.sp)
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
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 13.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )
        }
    }
}
