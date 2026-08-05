package com.barisal.cityservice.feature.houserent

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import com.barisal.cityservice.data.model.FlatDetailsDto
import com.barisal.cityservice.data.model.HouseRentDto
import com.barisal.cityservice.data.repository.HouseRentRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

data class HouseRentInfo(
    val id: String = "",
    val landlordName: String,
    val date: String,
    val houseType: String,
    val address: String,
    val latLng: String,
    val contactInfo: String,
    val rentAmount: String,
    val details: String,
    val zilla: String = "",
    val imageUrls: List<String> = emptyList(),
    val flatDetails: FlatDetailsDto = FlatDetailsDto(),
    val isRented: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseRentListScreen(
    onBack: () -> Unit,
    onNavigateToPostHouseRent: () -> Unit = {},
    onNavigateToHouseRentDetail: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    val primaryColor = Color(0xFF00897B)

    var selectedSubCategory by remember { mutableStateOf("সব") }
    val houseRentSubCategories = listOf(
        "সব",
        "ফ্ল্যাট ভাড়া",
        "ব্যাচেলর রুম/সিট",
        "সাবলেট",
        "হোস্টেল",
        "অফিস স্পেস",
        "দোকান",
        "গ্যারেজ"
    )

    val houseRentRepository = remember { HouseRentRepository() }
    val firestoreHouseRentsState by houseRentRepository.getHouseRents(if (selectedSubCategory == "সব") "" else selectedSubCategory)
        .collectAsState(initial = emptyList())

    val dummyHouses = remember(isBengali) {
        listOf(
            HouseRentInfo(
                id = "dummy-1",
                landlordName = "Sabira Priyq",
                date = "06 Jun 2026",
                houseType = "ফ্ল্যাট ভাড়া",
                address = if (isBengali) "অম্বিকাপুর, গ্রামীন ফোনের টাওয়ারের সামনে। সদর, ফরিদপুর" else "Ambikapur, In front of GP Tower. Sadar, Faridpur",
                latLng = "23.6061,89.8406",
                contactInfo = "01711-223344",
                rentAmount = if (isBengali) "৳১০,০০০/মাস" else "৳10,000/month",
                details = if (isBengali) "৩ বেডরুম, ২ বাথরুম, ড্রয়িং ও ডাইনিং স্পেস সহ সুন্দর ফ্ল্যাট। গ্যাস ও পানির সুব্যবস্থা রয়েছে।" else "Beautiful flat with 3 bedrooms, 2 bathrooms, drawing and dining space. Gas and water available.",
                zilla = if (isBengali) "ফরিদপুর" else "Faridpur",
                flatDetails = FlatDetailsDto(houseNo = "৪৫", levelNo = "৩য় তলা", flatNo = "B-2", bedrooms = "৩টি", bathrooms = "২টি")
            ),
            HouseRentInfo(
                id = "dummy-2",
                landlordName = "MD Shahidul Islam",
                date = "03 Jun 2026",
                houseType = "ব্যাচেলর রুম/সিট",
                address = if (isBengali) "চুনাঘাটা ব্রীজের ওপার, ইকবালের ফার্ম এর সামনের বাসা" else "Across Chunaghata Bridge, In front of Iqbal's farm",
                latLng = "23.6012,89.8322",
                contactInfo = "01712-334455",
                rentAmount = if (isBengali) "৳৪,৫০০/মাস" else "৳4,500/month",
                details = if (isBengali) "ব্যাচেলর ছাত্রদের জন্য ১টি সিঙ্গেল সিট খালি আছে। ওয়াইফাই ও ফিল্টার পানি ফ্রি।" else "Single bachelor seat available for students. Free Wifi & Filter water.",
                zilla = if (isBengali) "ফরিদপুর" else "Faridpur",
                flatDetails = FlatDetailsDto(bedrooms = "১টি সিট", bathrooms = "১টি")
            )
        )
    }

    val allHouses = remember(firestoreHouseRentsState, dummyHouses) {
        if (firestoreHouseRentsState.isNotEmpty()) {
            firestoreHouseRentsState.map { dto ->
                val photos = dto.imageUrls
                HouseRentInfo(
                    id = dto.id,
                    landlordName = dto.title.ifEmpty { if (isBengali) "বাসা ভাড়া" else "House Rent" },
                    date = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.US).format(java.util.Date(dto.createdAt)),
                    houseType = dto.houseType,
                    address = dto.address,
                    latLng = dto.latLng,
                    contactInfo = dto.contactInfo,
                    rentAmount = dto.rentAmount,
                    details = dto.details,
                    zilla = dto.zilla,
                    imageUrls = photos,
                    flatDetails = dto.flatDetails,
                    isRented = dto.isRented
                )
            }
        } else {
            dummyHouses
        }
    }

    val filteredHouses = remember(allHouses, searchQuery, selectedSubCategory, selectedZilla) {
        allHouses.filter { house ->
            val matchesQuery = searchQuery.isBlank() ||
                    house.landlordName.contains(searchQuery, ignoreCase = true) ||
                    house.address.contains(searchQuery, ignoreCase = true) ||
                    house.details.contains(searchQuery, ignoreCase = true)

            val matchesCategory = selectedSubCategory == "সব" || house.houseType.equals(selectedSubCategory, ignoreCase = true)
            val matchesZilla = selectedZilla == null || house.zilla.equals(selectedZilla, ignoreCase = true) || house.address.contains(selectedZilla!!, ignoreCase = true)

            matchesQuery && matchesCategory && matchesZilla
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বাসা ভাড়া" else "House Rent",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter", tint = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostHouseRent,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post House Rent")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "বাসা ভাড়া পোস্ট করুন" else "Post House Rent",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
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
                    items(bangladeshZillas) { zilla ->
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
            // 1. Sub-Categories horizontal bar
            ScrollableTabRow(
                selectedTabIndex = houseRentSubCategories.indexOf(selectedSubCategory).coerceAtLeast(0),
                containerColor = Color.White,
                edgePadding = 12.dp,
                divider = {}
            ) {
                houseRentSubCategories.forEach { category ->
                    val isSelected = selectedSubCategory == category
                    Tab(
                        selected = isSelected,
                        onClick = { selectedSubCategory = category },
                        text = {
                            Text(
                                text = category,
                                color = if (isSelected) primaryColor else Color(0xFF475569),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        }
                    )
                }
            }

            // 2. Search & Filter Bar
            Surface(
                color = Color.White,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isBengali) "বাসা ভাড়া বা এলাকা খুঁজুন..." else "Search house rent or area...", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(if (selectedZilla != null) primaryColor else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Zilla",
                                tint = if (selectedZilla != null) Color.White else Color(0xFF475569)
                            )
                        }
                    }
                }
            }

            // 3. Main List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredHouses) { house ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                if (house.id.isNotBlank()) {
                                    onNavigateToHouseRentDetail(house.id)
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            // Header: Landlord / Title & Rent Status / Amount Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(primaryColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Home,
                                            contentDescription = null,
                                            tint = primaryColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column {
                                        Text(
                                            text = house.landlordName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = house.date,
                                            fontSize = 11.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        color = if (house.isRented) Color(0xFFDC2626) else primaryColor,
                                        shape = RoundedCornerShape(16.dp)
                                    ) {
                                        Text(
                                            text = if (house.isRented) (if (isBengali) "ভাড়া হয়ে গেছে" else "RENTED") else house.rentAmount,
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                    if (house.isRented) {
                                        Text(
                                            text = house.rentAmount,
                                            fontSize = 11.sp,
                                            color = Color.Gray,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }

                            // Multiple Image Carousel (if present)
                            if (house.imageUrls.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(house.imageUrls) { imgUrl ->
                                        val coilModel = remember(imgUrl) { imgUrl.toCoilModel() }
                                        if (coilModel != null) {
                                            AsyncImage(
                                                model = coilModel,
                                                contentDescription = "House Photo",
                                                modifier = Modifier
                                                    .width(160.dp)
                                                    .height(110.dp)
                                                    .clip(RoundedCornerShape(8.dp)),
                                                contentScale = ContentScale.Crop
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Sub-category badge & Flat details chips
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = Color(0xFFE0F2FE),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = house.houseType,
                                        color = Color(0xFF0369A1),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }

                                if (house.flatDetails.levelNo.isNotBlank()) {
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                        Text("🏢 ${house.flatDetails.levelNo}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (house.flatDetails.flatNo.isNotBlank()) {
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                        Text("🔑 ${house.flatDetails.flatNo}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (house.flatDetails.bedrooms.isNotBlank()) {
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                        Text("🛏️ ${house.flatDetails.bedrooms}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                if (house.flatDetails.bathrooms.isNotBlank()) {
                                    Surface(color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp)) {
                                        Text("🚿 ${house.flatDetails.bathrooms}", fontSize = 11.sp, color = Color.DarkGray, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Address
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color.Red,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = house.address,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155),
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Details
                            if (house.details.isNotBlank()) {
                                Text(
                                    text = house.details,
                                    fontSize = 12.sp,
                                    color = Color(0xFF64748B),
                                    maxLines = 2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            Divider(color = Color(0xFFF1F5F9))

                            Spacer(modifier = Modifier.height(8.dp))

                            // Action buttons: Rent Status Toggle + Call & SMS
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (house.id.isNotBlank() && !house.id.startsWith("dummy")) {
                                    TextButton(
                                        onClick = {
                                            coroutineScope.launch {
                                                val newStatus = !house.isRented
                                                val res = houseRentRepository.updateRentStatus(house.id, newStatus)
                                                if (res.isSuccess) {
                                                    Toast.makeText(
                                                        context,
                                                        if (newStatus) (if (isBengali) "বাসাটি 'ভাড়া হয়ে গেছে' মার্ক করা হলো" else "Marked as Rented")
                                                        else (if (isBengali) "বাসাটি খালি মার্ক করা হলো" else "Marked as Available"),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = if (house.isRented) (if (isBengali) "খালি মার্ক করুন" else "Mark Available") else (if (isBengali) "ভাড়া সম্পন্ন মার্ক করুন" else "Mark Rented"),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (house.isRented) primaryColor else Color(0xFFDC2626)
                                        )
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                Row {
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                                data = Uri.parse("smsto:${house.contactInfo}")
                                                putExtra("sms_body", "আসসালামু আলাইকুম, আপনার বাসা ভাড়া পোস্টটি সম্পর্কে জানতে চাই।")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor)
                                    ) {
                                        Icon(Icons.Default.Sms, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBengali) "মেসেজ" else "SMS", fontSize = 11.sp, color = primaryColor)
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                                data = Uri.parse("tel:${house.contactInfo}")
                                            }
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.height(34.dp),
                                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                                    ) {
                                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBengali) "কল করুন" else "Call", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
