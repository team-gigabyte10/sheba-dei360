package com.nayem.sheba_dei.feature.blood

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import com.nayem.sheba_dei.ui.components.SetStatusBarColor
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationScreen(
    onNavigateBack: () -> Unit
) {
    SetStatusBarColor()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showScamWarningDialog by remember { mutableStateOf(true) }

    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }

    val tealColor = Color(0xFF0F766E)

    if (showScamWarningDialog) {
        AlertDialog(
            onDismissRequest = { showScamWarningDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = Color(0xFFDC2626),
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = "প্রতারক হতে সতর্কতা!",
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = "অনলাইনে যেকোনো অপরিচিত ব্যক্তি বা প্রতিষ্ঠানকে অর্থ পাঠাবেন না। প্রতারণার শিকার হলে কর্তৃপক্ষকে দায়ী নয়।",
                    fontSize = 14.sp,
                    color = Color.Black
                )
            },
            confirmButton = {
                Button(
                    onClick = { showScamWarningDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = tealColor),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ঠিক আছে", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                com.nayem.sheba_dei.ui.components.GlobalAppBar(
                    title = "রক্তদাতা",
                    onBackClick = onNavigateBack,
                    actions = {
                        IconButton(onClick = { showZillaFilterDialog = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = tealColor,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = tealColor
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("ডোনার", color = if (selectedTab == 0) tealColor else Color.Gray, fontSize = 16.sp) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("রক্তের প্রয়োজন", color = if (selectedTab == 1) tealColor else Color.Gray, fontSize = 16.sp) }
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC) // Light gray background
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
                            Text("রিসেট", color = Color.Red, fontWeight = FontWeight.Bold)
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
                                fontWeight = if (selectedZilla == zilla) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("খুঁজুন...", fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = tealColor) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedBorderColor = tealColor
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Total Count Box
                Box(
                    modifier = Modifier
                        .height(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "1283", fontWeight = FontWeight.Medium, color = Color(0xFF475569))
                }
            }

            // Warning Text Marquee
            Text(
                text = "সাবধান! অনলাইনে যেকোনো অপরিচিত ব্যক্তি বা প্রতিষ্ঠানকে অর্থ পাঠাবেন না। নিজ দায়িত্বে যাচাই করে রক্তদান বা গ্রহণ করুন। প্রতারণার শিকার হলে কর্তৃপক্ষকে দায়ী নয়।",
                color = Color(0xFFDC2626), // Red warning text
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .basicMarquee(),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Donors List
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mockBloodDonors) { donor ->
                    BloodDonorCard(donor)
                }
            }
        }
    }
}

@Composable
fun BloodDonorCard(donor: BloodDonor) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Blood Group Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)), // Light red background for icon
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.WaterDrop, // Blood drop icon
                        contentDescription = "Blood",
                        tint = Color(0xFFDC2626), // Red drop
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = donor.bloodGroup,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donor.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "সর্বশেষ রক্তদানঃ ${donor.lastDonation}", fontSize = 13.sp, color = Color(0xFF475569))
                Text(text = "থানাঃ ${donor.thana}", fontSize = 13.sp, color = Color(0xFF475569))
                Text(text = "ঠিকানাঃ ${donor.address}", fontSize = 13.sp, color = Color(0xFF475569))
                Text(text = "বিস্তারিতঃ ${donor.details}", fontSize = 13.sp, color = Color(0xFF475569))
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Call Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                        .clickable { /* Call Action */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                
                // Message Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                        .clickable { /* Message Action */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Message, contentDescription = "Message", tint = Color.Black, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

// Mock Data Model
data class BloodDonor(
    val name: String,
    val bloodGroup: String,
    val lastDonation: String,
    val thana: String,
    val address: String,
    val details: String
)

val mockBloodDonors = listOf(
    BloodDonor(
        name = "Md Parvez Mir",
        bloodGroup = "O-",
        lastDonation = "12/11/2024",
        thana = "ফরিদপুর সদর",
        address = "Faridpur",
        details = ""
    ),
    BloodDonor(
        name = "সীমান্ত সন্ন্যাসী",
        bloodGroup = "B+",
        lastDonation = "05/05/2026",
        thana = "",
        address = "ফরিদপুর, ধুলদী রেল গেইট",
        details = "only Emergency"
    ),
    BloodDonor(
        name = "MD Sahad Hossen",
        bloodGroup = "AB+",
        lastDonation = "20/03/2026",
        thana = "সালথা",
        address = "Faridpur",
        details = "Dhaka divition er baire asi, so No call me"
    ),
    BloodDonor(
        name = "Abu Naim",
        bloodGroup = "A+",
        lastDonation = "05/01/2026",
        thana = "ফরিদপুর সদর",
        address = "",
        details = "Only For Emergency"
    )
)
