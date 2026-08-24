package com.barisal.cityservice.feature.blood

import android.content.Intent
import android.net.Uri
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.data.model.BloodDonorDto
import com.barisal.cityservice.data.model.BloodRequestDto
import com.barisal.cityservice.data.repository.BloodRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class BloodDonorItem(
    val id: String = "",
    val name: String,
    val bloodGroup: String,
    val lastDonation: String,
    val thana: String,
    val address: String,
    val details: String,
    val contactInfo: String = "",
    val zilla: String = ""
)

data class BloodRequestItem(
    val id: String = "",
    val patientName: String,
    val bloodGroup: String,
    val bagsNeeded: String,
    val hospitalName: String,
    val requiredDate: String,
    val thana: String,
    val zilla: String,
    val contactInfo: String,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodDonationScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostDonor: () -> Unit = {},
    onNavigateToPostRequest: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableStateOf(0) }
    var showScamWarningDialog by remember { mutableStateOf(true) }

    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }

    val tealColor = Color(0xFF0F766E)
    val redColor = Color(0xFFDC2626)
    val bloodRepo = remember { BloodRepository() }

    val firestoreDonorsState by bloodRepo.getBloodDonors().collectAsState(initial = emptyList())
    val firestoreRequestsState by bloodRepo.getBloodRequests().collectAsState(initial = emptyList())

    val mockDonorsList = remember {
        listOf(
            BloodDonorItem("1", "Md Parvez Mir", "O-", "12/11/2024", "ফরিদপুর সদর", "Faridpur", "", "01711223344", "ফরিদপুর"),
            BloodDonorItem("2", "সীমান্ত সন্ন্যাসী", "B+", "05/05/2026", "", "ফরিদপুর, ধুলদী রেল গেইট", "only Emergency", "01712334455", "ফরিদপুর"),
            BloodDonorItem("3", "MD Sahad Hossen", "AB+", "20/03/2026", "সালথা", "Faridpur", "Emergency Only", "01713445566", "ফরিদপুর"),
            BloodDonorItem("4", "Abu Naim", "A+", "05/01/2026", "ফরিদপুর সদর", "Faridpur", "Only For Emergency", "01714556677", "ফরিদপুর")
        )
    }

    val mockRequestsList = remember {
        listOf(
            BloodRequestItem("r1", "মো: পারভেজ আলম", "O-", "১ ব্যাগ", "শের-ই-বাংলা মেডিকেল কলেজ হাসপাতাল", "আজ বিকাল ৪টা", "বরিশাল সদর", "বরিশাল", "01711223344", "জরুরি ডেলিভারি অপারেশনের জন্য প্রয়োজন"),
            BloodRequestItem("r2", "রহিম মিয়া", "B+", "২ ব্যাগ", "ফরিদপুর জেনারেল হাসপাতাল", "জরুরি আজ রাতে", "ফরিদপুর সদর", "ফরিদপুর", "01712334455", "সার্জারির জন্য প্রয়োজন")
        )
    }

    val allDonors = remember(firestoreDonorsState, mockDonorsList) {
        if (firestoreDonorsState.isNotEmpty()) {
            firestoreDonorsState.map { dto ->
                BloodDonorItem(
                    id = dto.id,
                    name = dto.name,
                    bloodGroup = dto.bloodGroup,
                    lastDonation = dto.lastDonation,
                    thana = dto.thana,
                    address = dto.address,
                    details = dto.details,
                    contactInfo = dto.contactInfo,
                    zilla = dto.zilla
                )
            }
        } else {
            mockDonorsList
        }
    }

    val allRequests = remember(firestoreRequestsState, mockRequestsList) {
        if (firestoreRequestsState.isNotEmpty()) {
            firestoreRequestsState.map { dto ->
                BloodRequestItem(
                    id = dto.id,
                    patientName = dto.patientName,
                    bloodGroup = dto.bloodGroup,
                    bagsNeeded = dto.bagsNeeded,
                    hospitalName = dto.hospitalName,
                    requiredDate = dto.requiredDate,
                    thana = dto.thana,
                    zilla = dto.zilla,
                    contactInfo = dto.contactInfo,
                    details = dto.details
                )
            }
        } else {
            mockRequestsList
        }
    }

    val filteredDonors = remember(allDonors, searchQuery, selectedZilla) {
        allDonors.filter { donor ->
            val matchesSearch = searchQuery.isBlank() ||
                    donor.name.contains(searchQuery, ignoreCase = true) ||
                    donor.bloodGroup.contains(searchQuery, ignoreCase = true) ||
                    donor.thana.contains(searchQuery, ignoreCase = true) ||
                    donor.address.contains(searchQuery, ignoreCase = true) ||
                    donor.details.contains(searchQuery, ignoreCase = true)

            val matchesZilla = selectedZilla == null || donor.zilla.equals(selectedZilla, ignoreCase = true) || donor.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesZilla
        }
    }

    val filteredRequests = remember(allRequests, searchQuery, selectedZilla) {
        allRequests.filter { req ->
            val matchesSearch = searchQuery.isBlank() ||
                    req.patientName.contains(searchQuery, ignoreCase = true) ||
                    req.bloodGroup.contains(searchQuery, ignoreCase = true) ||
                    req.hospitalName.contains(searchQuery, ignoreCase = true) ||
                    req.thana.contains(searchQuery, ignoreCase = true) ||
                    req.details.contains(searchQuery, ignoreCase = true)

            val matchesZilla = selectedZilla == null || req.zilla.equals(selectedZilla, ignoreCase = true) || req.hospitalName.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesZilla
        }
    }

    if (showScamWarningDialog) {
        AlertDialog(
            onDismissRequest = { showScamWarningDialog = false },
            icon = {
                Icon(
                    Icons.Default.Warning,
                    contentDescription = "Warning",
                    tint = redColor,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    text = if (isBengali) "প্রতারক হতে সতর্কতা!" else "Warning Against Fraud!",
                    color = redColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    text = if (isBengali) "অনলাইনে যেকোনো অপরিচিত ব্যক্তি বা প্রতিষ্ঠানকে অর্থ পাঠাবেন না। প্রতারণার শিকার হলে কর্তৃপক্ষ দায়ী নয়।" else "Do not send money online to unknown individuals or organizations. Authorities are not responsible for fraud.",
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
                    Text(if (isBengali) "ঠিক আছে" else "OK", color = Color.White, fontWeight = FontWeight.Bold)
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
                GlobalAppBar(
                    title = if (isBengali) "রক্তদাতা ও ব্লাড ব্যাংক" else "Blood Donors & Blood Request",
                    onBackClick = onNavigateBack,
                    actions = {
                        IconButton(onClick = { showZillaFilterDialog = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.White)
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
                        text = { Text(if (isBengali) "ডোনার" else "Donors", color = if (selectedTab == 0) tealColor else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text(if (isBengali) "রক্তের প্রয়োজন" else "Urgent Blood Need", color = if (selectedTab == 1) tealColor else Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (selectedTab == 0) onNavigateToPostDonor() else onNavigateToPostRequest()
                },
                containerColor = if (selectedTab == 0) tealColor else redColor,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Post") },
                text = {
                    Text(
                        text = if (selectedTab == 0) {
                            if (isBengali) "ডোনার হিসেবে পোস্ট করুন" else "Post Donor Profile"
                        } else {
                            if (isBengali) "রক্তের পোস্ট দিন" else "Post Blood Request"
                        },
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = if (isBengali) "জেলা নির্বাচন করুন" else "Select District / Zilla",
                confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
                onConfirm = { showZillaFilterDialog = false }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    item {
                        TextButton(onClick = { 
                            selectedZilla = null
                            showZillaFilterDialog = false 
                        }) {
                            Text(if (isBengali) "রিসেট" else "Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(items = bangladeshZillas) { zilla ->
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
                                textAlign = TextAlign.Start
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
                    placeholder = { Text(if (isBengali) "খুঁজুন (গ্রুপ, এলাকা, নাম)..." else "Search (Group, Location, Name)...", fontSize = 15.sp) },
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
                    Text(
                        text = if (selectedTab == 0) "${filteredDonors.size}" else "${filteredRequests.size}",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                }
            }

            // Warning Text Marquee
            Text(
                text = if (isBengali) "সাবধান! অনলাইনে যেকোনো অপরিচিত ব্যক্তি বা প্রতিষ্ঠানকে অর্থ পাঠাবেন না। নিজ দায়িত্বে বিষয়সমূহ যাচাই করে রক্তদান বা গ্রহণ করুন।" else "Warning: Do not send money online to unknown persons. Verify details independently before donating or receiving blood.",
                color = redColor,
                fontSize = 13.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .basicMarquee(),
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (selectedTab == 0) {
                // Donors List
                if (filteredDonors.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isBengali) "কোন রক্তদাতা পাওয়া যায়নি" else "No donors found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredDonors) { donor ->
                            BloodDonorCard(donor, isBengali = isBengali, context = context)
                        }
                    }
                }
            } else {
                // Blood Requests List
                if (filteredRequests.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (isBengali) "কোন রক্তের পোস্ট পাওয়া যায়নি" else "No blood requests found", color = Color.Gray)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredRequests) { req ->
                            BloodRequestCard(req, isBengali = isBengali, context = context)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BloodDonorCard(donor: BloodDonorItem, isBengali: Boolean, context: android.content.Context) {
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
                    .width(52.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.WaterDrop,
                        contentDescription = "Blood",
                        tint = Color(0xFFDC2626),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = donor.bloodGroup,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = donor.name,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (donor.lastDonation.isNotBlank()) {
                    Text(text = "${if (isBengali) "সর্বশেষ রক্তদানঃ" else "Last Donation:"} ${donor.lastDonation}", fontSize = 13.sp, color = Color(0xFF475569))
                }
                if (donor.thana.isNotBlank()) {
                    Text(text = "${if (isBengali) "থানাঃ" else "Thana:"} ${donor.thana}", fontSize = 13.sp, color = Color(0xFF475569))
                }
                if (donor.address.isNotBlank()) {
                    Text(text = "${if (isBengali) "ঠিকানাঃ" else "Address:"} ${donor.address}", fontSize = 13.sp, color = Color(0xFF475569))
                }
                if (donor.details.isNotBlank()) {
                    Text(text = "${if (isBengali) "বিস্তারিতঃ" else "Details:"} ${donor.details}", fontSize = 13.sp, color = Color(0xFF475569))
                }
            }
            
            // Action Buttons (Call & Message)
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Call Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                        .clickable {
                            if (donor.contactInfo.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${donor.contactInfo}")
                                }
                                context.startActivity(intent)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0F766E), modifier = Modifier.size(20.dp))
                }
                
                // Message Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0))
                        .clickable {
                            if (donor.contactInfo.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${donor.contactInfo}")
                                }
                                context.startActivity(intent)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Message, contentDescription = "Message", tint = Color(0xFF0F766E), modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun BloodRequestCard(req: BloodRequestItem, isBengali: Boolean, context: android.content.Context) {
    val redColor = Color(0xFFDC2626)
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Blood Group & Bags Icon
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(54.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(redColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = req.bloodGroup,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = req.bagsNeeded,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = redColor
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = req.patientName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (req.hospitalName.isNotBlank()) {
                    Text(text = "📍 ${req.hospitalName}", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.Black)
                }
                if (req.requiredDate.isNotBlank()) {
                    Text(text = "⏰ ${if (isBengali) "প্রয়োজনের সময়ঃ" else "Required Time:"} ${req.requiredDate}", fontSize = 13.sp, color = redColor, fontWeight = FontWeight.Medium)
                }
                if (req.thana.isNotBlank() || req.zilla.isNotBlank()) {
                    Text(text = "🏠 ${if (isBengali) "এলাকাঃ" else "Area:"} ${req.thana} ${req.zilla}", fontSize = 13.sp, color = Color(0xFF475569))
                }
                if (req.details.isNotBlank()) {
                    Text(text = "ℹ️ ${req.details}", fontSize = 13.sp, color = Color(0xFF475569))
                }
            }
            
            // Action Buttons
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 4.dp)
            ) {
                // Call Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2))
                        .clickable {
                            if (req.contactInfo.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${req.contactInfo}")
                                }
                                context.startActivity(intent)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = redColor, modifier = Modifier.size(20.dp))
                }
                
                // Message Button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFEE2E2))
                        .clickable {
                            if (req.contactInfo.isNotBlank()) {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("smsto:${req.contactInfo}")
                                }
                                context.startActivity(intent)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Message, contentDescription = "Message", tint = redColor, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}
