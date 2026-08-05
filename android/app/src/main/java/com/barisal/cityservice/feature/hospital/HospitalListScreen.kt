package com.barisal.cityservice.feature.hospital

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.repository.HealthServiceRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class HospitalInfo(
    val id: String = "",
    val name: String,
    val date: String,
    val type: String,
    val address: String,
    val latLng: String,
    val contactInfo: String,
    val details: String,
    val zilla: String = "",
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalListScreen(
    onBack: () -> Unit,
    onNavigateToPostHealthService: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFF00897B)
    val healthRepo = remember { HealthServiceRepository() }

    val firestoreHospitalsState by healthRepo.getHealthServicesByCategory("hospital")
        .collectAsState(initial = emptyList())

    val dummyHospitals = remember(isBengali) {
        listOf(
            HospitalInfo(
                id = "dummy-1",
                name = if (isBengali) "ফরিদপুর মেডিকেল কলেজ হাসপাতাল" else "Faridpur Medical College Hospital",
                date = "06 Jun 2026",
                type = if (isBengali) "সরকারি হাসপাতাল" else "Government Hospital",
                address = if (isBengali) "ফরিদপুর সদর, ফরিদপুর" else "Faridpur Sadar, Faridpur",
                latLng = "23.6061,89.8406",
                contactInfo = "01711-223344",
                details = if (isBengali) "ফরিদপুর মেডিকেল কলেজ হাসপাতাল একটি সরকারি হাসপাতাল। এখানে সকল ধরনের চিকিৎসা সুবিধা রয়েছে।" else "Faridpur Medical College Hospital is a government hospital. All kinds of medical facilities are available here.",
                zilla = if (isBengali) "ফরিদপুর" else "Faridpur"
            ),
            HospitalInfo(
                id = "dummy-2",
                name = if (isBengali) "ডায়াবেটিক হাসপাতাল" else "Diabetic Hospital",
                date = "03 Jun 2026",
                type = if (isBengali) "বেসরকারি হাসপাতাল" else "Private Hospital",
                address = if (isBengali) "ঝিলটুলী, ফরিদপুর" else "Jhiltuli, Faridpur",
                latLng = "23.6012,89.8322",
                contactInfo = "01712-334455",
                details = if (isBengali) "ডায়াবেটিস ও অন্যান্য রোগের উন্নত চিকিৎসা প্রদান করা হয়।" else "Advanced treatment for diabetes and other diseases is provided.",
                zilla = if (isBengali) "ফরিদপুর" else "Faridpur"
            )
        )
    }

    val allHospitals = remember(firestoreHospitalsState, dummyHospitals) {
        if (firestoreHospitalsState.isNotEmpty()) {
            firestoreHospitalsState.map { dto ->
                val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(dto.createdAt))
                HospitalInfo(
                    id = dto.id,
                    name = dto.name,
                    date = dateStr,
                    type = dto.type,
                    address = dto.address,
                    latLng = dto.latLng,
                    contactInfo = dto.contactInfo,
                    details = dto.details,
                    zilla = dto.zilla,
                    imageUrl = dto.imageUrl
                )
            }
        } else {
            dummyHospitals
        }
    }

    val filteredHospitals = remember(allHospitals, searchQuery, selectedZilla) {
        allHospitals.filter { hospital ->
            val matchesSearch = searchQuery.isBlank() ||
                    hospital.name.contains(searchQuery, ignoreCase = true) ||
                    hospital.type.contains(searchQuery, ignoreCase = true) ||
                    hospital.address.contains(searchQuery, ignoreCase = true) ||
                    hospital.details.contains(searchQuery, ignoreCase = true)

            val matchesZilla = selectedZilla == null || hospital.zilla.equals(selectedZilla, ignoreCase = true) || hospital.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesZilla
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "হাসপাতাল" else "Hospital",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter by Zilla", tint = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToPostHealthService("hospital") },
                containerColor = primaryColor,
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Post Hospital") },
                text = { Text(if (isBengali) "হাসপাতাল পোস্ট করুন" else "Post Hospital", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = if (isBengali) "জেলা নির্বাচন করুন" else "Select Zilla",
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
                    items(items = bangladeshZillas) { zilla: String ->
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
                .background(Color(0xFFF3F4F6))
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
                    placeholder = { Text(if (isBengali) "হাসপাতাল খুঁজুন..." else "Search hospital...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = primaryColor
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Count Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB2DFDB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${filteredHospitals.size}",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredHospitals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "কোন হাসপাতাল পাওয়া যায়নি" else "No hospitals found",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredHospitals) { hospital ->
                        HospitalCard(hospital = hospital, isBengali = isBengali, primaryColor = primaryColor)
                    }
                }
            }
        }
    }
}

@Composable
fun HospitalCard(hospital: HospitalInfo, isBengali: Boolean, primaryColor: Color) {
    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }

    val coilModel = remember(hospital.imageUrl) { hospital.imageUrl.toCoilModel() }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Left Side: Image
            Column(
                modifier = Modifier.width(100.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Hospital Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocalHospital, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Right Side: Content
            Column(modifier = Modifier.weight(1f)) {
                // Name and Date
                Text(
                    text = hospital.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = hospital.date,
                    color = Color.Gray,
                    fontSize = 12.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Type
                if (hospital.type.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(if (isBengali) "হাসপাতালের ধরণ" else "Hospital Type", fontSize = 10.sp, color = Color.Gray)
                            Text(hospital.type, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
                
                // Address
                if (hospital.address.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(if (isBengali) "ঠিকানা" else "Address", fontSize = 10.sp, color = Color.Gray)
                            Text(hospital.address, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (hospital.latLng.isNotBlank()) {
                                val gmmIntentUri = Uri.parse("geo:${hospital.latLng}?q=${hospital.latLng}(${Uri.encode(hospital.name)})")
                                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${hospital.latLng}"))
                                    context.startActivity(browserIntent)
                                }
                            } else {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(hospital.name)}"))
                                context.startActivity(browserIntent)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Google Map", fontSize = 11.sp, color = Color.White)
                    }
                    
                    Button(
                        onClick = { showDetailsDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(32.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(if (isBengali) "বিস্তারিত দেখুন" else "View Details", fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }

    if (showDetailsDialog) {
        CustomDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = if (isBengali) "হাসপাতালের বিস্তারিত তথ্য" else "Hospital Details",
            icon = Icons.Default.Info,
            confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
            usePlatformDefaultWidth = false,
            onConfirm = { showDetailsDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Hospital Photo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                Text(
                    text = hospital.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                if (hospital.type.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(hospital.type, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }

                if (hospital.address.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(hospital.address, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }

                if (hospital.details.isNotBlank()) {
                    Text(hospital.details, color = Color.DarkGray, fontSize = 14.sp)
                }
                
                if (hospital.contactInfo.isNotBlank()) {
                    Divider(color = Color.LightGray, thickness = 1.dp)

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${hospital.contactInfo}")
                                }
                                context.startActivity(intent)
                            }
                            .padding(12.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = primaryColor)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isBengali) "যোগাযোগঃ ${hospital.contactInfo}" else "Contact: ${hospital.contactInfo}",
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}
