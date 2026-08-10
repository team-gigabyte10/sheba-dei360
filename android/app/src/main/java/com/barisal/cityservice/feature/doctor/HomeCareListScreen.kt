package com.barisal.cityservice.feature.doctor

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.HealthServiceDto
import com.barisal.cityservice.data.repository.HealthServiceRepository
import com.barisal.cityservice.feature.service.rememberCategoryMarkerIcon
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeCareListScreen(
    onBack: () -> Unit,
    onNavigateToPostHomeCare: () -> Unit
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    val healthRepo = remember { HealthServiceRepository() }
    val firestoreHomeCareState by healthRepo.getHealthServicesByCategory("home_care").collectAsState(initial = emptyList())

    // Combine mock home care items with approved firestore items
    val allHomeCareItems = remember(firestoreHomeCareState) {
        val mockItems = getMockHomeCareServices()
        if (firestoreHomeCareState.isEmpty()) {
            mockItems
        } else {
            firestoreHomeCareState + mockItems
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilterRole by remember { mutableStateOf("all") } // all, nurse, assistant, caregiver, after_office
    var isMapView by remember { mutableStateOf(false) }
    var selectedServiceDetail by remember { mutableStateOf<HealthServiceDto?>(null) }

    // Filter items
    val filteredItems = remember(allHomeCareItems, searchQuery, selectedFilterRole) {
        allHomeCareItems.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.type.contains(searchQuery, ignoreCase = true) ||
                    item.address.contains(searchQuery, ignoreCase = true) ||
                    item.details.contains(searchQuery, ignoreCase = true)

            val matchesRole = when (selectedFilterRole) {
                "nurse" -> item.type.contains("নার্স", true) || item.type.contains("nurse", true)
                "assistant" -> item.type.contains("মেডিকেল অ্যাসিস্ট্যান্ট", true) || item.type.contains("assistant", true) || item.type.contains("MATS", true)
                "caregiver" -> item.type.contains("কেয়ারগিভার", true) || item.type.contains("caregiver", true) || item.type.contains("ফিজিওথেরাপিস্ট", true)
                "after_office" -> item.details.contains("অফিস", true) || item.details.contains("after office", true) || item.details.contains("বিকাল", true) || item.details.contains("সন্ধ্যা", true)
                else -> true
            }

            matchesSearch && matchesRole
        }
    }

    val primaryColor = Color(0xFFF59E0B) // Amber / Warm Orange for Home Care

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "হোম কেয়ার সার্ভিস" else "Home Care Services",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    IconButton(onClick = { isMapView = !isMapView }) {
                        Icon(
                            imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                            contentDescription = "Toggle View",
                            tint = Color(0xFF0F172A)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostHomeCare,
                icon = { Icon(Icons.Default.Add, contentDescription = null, tint = Color.White) },
                text = { Text(if (isBengali) "সেবা পোস্ট করুন" else "Post Service", fontWeight = FontWeight.Bold, color = Color.White) },
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text(if (isBengali) "নাম, এলাকা বা সেবার ধরন দিয়ে খুঁজুন..." else "Search by name, area or service...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = Color.Gray)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE2E8F0)
                ),
                singleLine = true
            )

            // Category Filter Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedFilterRole == "all",
                        onClick = { selectedFilterRole = "all" },
                        label = { Text(if (isBengali) "সকল (All)" else "All") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterRole == "nurse",
                        onClick = { selectedFilterRole = "nurse" },
                        label = { Text(if (isBengali) "👩‍⚕️ নার্স (Nurse)" else "👩‍⚕️ Nurse") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterRole == "assistant",
                        onClick = { selectedFilterRole = "assistant" },
                        label = { Text(if (isBengali) "👨‍⚕️ মেডিকেল অ্যাসিস্ট্যান্ট" else "👨‍⚕️ Med Assistant") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterRole == "caregiver",
                        onClick = { selectedFilterRole = "caregiver" },
                        label = { Text(if (isBengali) "🤝 কেয়ারগিভার / ফিজিও" else "🤝 Caregiver / Physio") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
                item {
                    FilterChip(
                        selected = selectedFilterRole == "after_office",
                        onClick = { selectedFilterRole = "after_office" },
                        label = { Text(if (isBengali) "⏰ অফিস সময় পর" else "⏰ After Office Hours") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Main View: List or Map
            if (isMapView) {
                HomeCareMapView(
                    items = filteredItems,
                    primaryColor = primaryColor,
                    onItemSelect = { selectedServiceDetail = it }
                )
            } else {
                if (filteredItems.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.HomeWork, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (isBengali) "কোনো হোম কেয়ার সার্ভিস পাওয়া যায়নি" else "No Home Care Services Found",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.DarkGray
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (isBengali) "অন্যান্য ফিল্টার দিয়ে চেষ্টা করুন বা আপনার তথ্য পোস্ট করুন।" else "Try adjusting filters or post a new service.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredItems) { service ->
                            HomeCareCard(
                                service = service,
                                isBengali = isBengali,
                                primaryColor = primaryColor,
                                onClick = { selectedServiceDetail = service },
                                onCall = { callNumber(context, service.contactInfo) },
                                onWhatsApp = { openWhatsApp(context, service.contactInfo) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Detail Dialog
    selectedServiceDetail?.let { service ->
        HomeCareDetailDialog(
            service = service,
            isBengali = isBengali,
            primaryColor = primaryColor,
            onDismiss = { selectedServiceDetail = null },
            onCall = { callNumber(context, service.contactInfo) },
            onWhatsApp = { openWhatsApp(context, service.contactInfo) }
        )
    }
}

@Composable
fun HomeCareCard(
    service: HealthServiceDto,
    isBengali: Boolean,
    primaryColor: Color,
    onClick: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                if (service.imageUrl.isNotBlank()) {
                    AsyncImage(
                        model = service.imageUrl,
                        contentDescription = service.name,
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                } else {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(primaryColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.MedicalServices, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Role / Designation Badge
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = primaryColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = service.type.ifEmpty { "হোম কেয়ার সেবাদাতা" },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "📍 ${service.address}${if (service.zilla.isNotBlank()) ", ${service.zilla}" else ""}",
                        fontSize = 14.sp,
                        color = Color(0xFF475569),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (service.details.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = service.details,
                    fontSize = 14.sp,
                    color = Color(0xFF334155),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF8FAFC))
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onCall,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBengali) "কল করুন" else "Call", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                OutlinedButton(
                    onClick = onWhatsApp,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981))
                ) {
                    Text(if (isBengali) "💬 হোয়াটসঅ্যাপ" else "💬 WhatsApp", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun HomeCareMapView(
    items: List<HealthServiceDto>,
    primaryColor: Color,
    onItemSelect: (HealthServiceDto) -> Unit
) {
    val barisalCenter = LatLng(22.7010, 90.3535)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(barisalCenter, 13f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState
    ) {
        items.forEach { item ->
            val latLngParts = item.latLng.split(",")
            val lat = latLngParts.getOrNull(0)?.trim()?.toDoubleOrNull()
            val lng = latLngParts.getOrNull(1)?.trim()?.toDoubleOrNull()
            if (lat != null && lng != null) {
                val position = LatLng(lat, lng)
                val icon = rememberCategoryMarkerIcon(
                    imageVector = Icons.Default.HomeWork,
                    backgroundColor = primaryColor,
                    isSelected = false
                )
                Marker(
                    state = MarkerState(position = position),
                    title = item.name,
                    snippet = item.type,
                    icon = icon,
                    onClick = {
                        onItemSelect(item)
                        true
                    }
                )
            }
        }
    }
}

@Composable
fun HomeCareDetailDialog(
    service: HealthServiceDto,
    isBengali: Boolean,
    primaryColor: Color,
    onDismiss: () -> Unit,
    onCall: () -> Unit,
    onWhatsApp: () -> Unit
) {
    CustomDialog(
        onDismissRequest = onDismiss,
        title = service.name,
        icon = Icons.Default.MedicalServices,
        iconTint = primaryColor,
        iconBackgroundColor = primaryColor.copy(alpha = 0.12f),
        confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
        onConfirm = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = primaryColor.copy(alpha = 0.12f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = service.type.ifEmpty { "হোম কেয়ার সেবাদাতা" },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }

            Text("📍 ঠিকানা: ${service.address}, ${service.zilla}", fontSize = 15.sp, color = Color.DarkGray)
            Text("📞 যোগাযোগ: ${service.contactInfo}", fontSize = 15.sp, color = Color.DarkGray, fontWeight = FontWeight.Bold)

            if (service.details.isNotBlank()) {
                Text("📋 সেবাসমূহ ও বিস্তারিত:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(service.details, fontSize = 14.sp, color = Color(0xFF334155))
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onCall,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "কল করুন" else "Call", color = Color.White, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onWhatsApp,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF059669)),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "💬 হোয়াটসঅ্যাপ" else "WhatsApp", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun callNumber(context: Context, number: String) {
    if (number.isBlank()) {
        Toast.makeText(context, "ফোন নম্বর পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.trim()}"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "কল করা সম্ভব হচ্ছে না", Toast.LENGTH_SHORT).show()
    }
}

private fun openWhatsApp(context: Context, number: String) {
    if (number.isBlank()) {
        Toast.makeText(context, "হোয়াটসঅ্যাপ নম্বর পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
        return
    }
    try {
        val cleanNumber = number.replace(Regex("[^0-9]"), "")
        val formattedNumber = if (cleanNumber.startsWith("88")) cleanNumber else "88$cleanNumber"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$formattedNumber"))
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "হোয়াটসঅ্যাপ খোলা যাচ্ছে না", Toast.LENGTH_SHORT).show()
    }
}

private fun getMockHomeCareServices(): List<HealthServiceDto> {
    return listOf(
        HealthServiceDto(
            id = "hc_mock_1",
            categoryKey = "home_care",
            name = "নার্স আমেনা বেগম",
            type = "সিনিয়র নার্স (BS.c Nursing)",
            address = "নথুল্লাবাদ, বরিশাল সদর",
            zilla = "বরিশাল",
            contactInfo = "01712345678",
            latLng = "22.7210,90.3520",
            details = "সেবাসমূহ: ইনজেকশন/IV স্যালাইন দেওয়া, ক্ষতের ড্রেসিং, ক্যাথেটার পরিবর্তন, ডায়াবেটিস ও প্রেসার পরিমাপ। সময়: অফিস সময়ের পর (বিকাল ৫:০০ - রাত ১০:০০)।",
            isApproved = true
        ),
        HealthServiceDto(
            id = "hc_mock_2",
            categoryKey = "home_care",
            name = "মেডিকেল অ্যাসিস্ট্যান্ট তারেক হোসেন",
            type = "মেডিকেল অ্যাসিস্ট্যান্ট (MATS)",
            address = "রূপাতলী, বরিশাল",
            zilla = "বরিশাল",
            contactInfo = "01819988776",
            latLng = "22.6780,90.3460",
            details = "সেবাসমূহ: প্রাথমিক চিকিৎসা, ড্রেসিং, ব্যান্ডেজ, নেবুলাইজার সাপোর্ট ও বয়স্ক রোগীদের হোমে এসে সার্ভিস। সময়: বিকাল ৪:৩০ থেকে রাত ৯:৩০ পর্যন্ত।",
            isApproved = true
        ),
        HealthServiceDto(
            id = "hc_mock_3",
            categoryKey = "home_care",
            name = "ফিজিওথেরাপিস্ট আব্দুর রহমান",
            type = "হোম ফিজিওথেরাপিস্ট",
            address = "বান্ধ রোড, বরিশাল",
            zilla = "বরিশাল",
            contactInfo = "01912233445",
            latLng = "22.6970,90.3680",
            details = "প্যারালাইসিস, কোমর ব্যথা, হাঁটু ব্যথা ও স্ট্রোকের পর বাসায় গিয়ে ফিজিওথেরাপি প্রদান করা হয়। সময়: প্রতিদিন সন্ধ্যা ৬:০০ - রাত ১০:০০।",
            isApproved = true
        )
    )
}
