package com.barisal.cityservice.feature.hotel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.barisal.cityservice.data.model.HotelDto
import com.barisal.cityservice.data.repository.HotelRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostHotel: () -> Unit = {},
    onNavigateToHotelDetail: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val repository = remember { HotelRepository() }
    val firestoreHotels by repository.getHotelsFlow().collectAsState(initial = emptyList())

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFF0F766E) // Teal theme

    val subCategories = listOf(
        if (isBengali) "সব" else "All",
        if (isBengali) "আবাসিক হোটেল" else "Residential Hotel",
        if (isBengali) "রিসোর্ট ও কটেজ" else "Resort & Cottage",
        if (isBengali) "রেস্ট হাউস" else "Rest House"
    )

    val dummyHotels = listOf(
        HotelDto(
            id = "preset_1",
            name = if (isBengali) "হোটেল রয়্যাল প্যালেস" else "Hotel Royal Palace",
            type = if (isBengali) "আবাসিক হোটেল" else "Residential Hotel",
            address = if (isBengali) "স্টেশন রোড, বরিশাল" else "Station Road, Barisal",
            pricePerNight = if (isBengali) "৳২,৫০০/রাত" else "৳2,500/night",
            rating = "4.5",
            reviewsCount = "(120)",
            contact = "01711-223344"
        ),
        HotelDto(
            id = "preset_2",
            name = if (isBengali) "গ্র্যান্ড সুলতান টি রিসোর্ট" else "Grand Sultan Tea Resort",
            type = if (isBengali) "রিসোর্ট ও কটেজ" else "Resort & Cottage",
            address = if (isBengali) "শ্রীমঙ্গল, মৌলভীবাজার" else "Sreemangal, Moulvibazar",
            pricePerNight = if (isBengali) "৳১০,০০০/রাত" else "৳10,000/night",
            rating = "4.8",
            reviewsCount = "(350)",
            contact = "01712-334455"
        )
    )

    val combinedHotels = remember(firestoreHotels, searchQuery, selectedSubCategory, selectedZilla) {
        val approvedFirestore = firestoreHotels.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyHotels else dummyHotels
        list.filter { hotel ->
            val matchesSearch = searchQuery.isBlank() ||
                    hotel.name.contains(searchQuery, ignoreCase = true) ||
                    hotel.address.contains(searchQuery, ignoreCase = true) ||
                    hotel.type.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    hotel.type.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(hotel.type, ignoreCase = true)
            val matchesZilla = selectedZilla == null || hotel.zilla.contains(selectedZilla!!, ignoreCase = true) || hotel.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesCategory && matchesZilla
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "হোটেল" else "Hotel",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostHotel,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Hotel")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "হোটেল পোস্ট করুন" else "Post Hotel",
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
                .background(Color(0xFFF8FAFC))
        ) {
            // Search Bar Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            text = if (isBengali) "হোটেল খুঁজুন..." else "Search Hotel...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    },
                    singleLine = true
                )
            }

            // Sub-Category Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subCategories) { cat ->
                    val isSelected = selectedSubCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubCategory = cat },
                        label = { Text(cat, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Hotel Items List Shown Initially
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(combinedHotels) { hotel ->
                    HotelCardDto(
                        hotel = hotel,
                        primaryColor = primaryColor,
                        isBengali = isBengali,
                        onClick = { onNavigateToHotelDetail(hotel.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelCardDto(
    hotel: HotelDto,
    primaryColor: Color,
    isBengali: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                if (hotel.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = hotel.coverImage,
                        contentDescription = hotel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Hotel, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = hotel.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = hotel.type, fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = hotel.address, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${hotel.rating} ${hotel.reviewsCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
                if (hotel.pricePerNight.isNotBlank()) {
                    Text(text = hotel.pricePerNight, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                } else {
                    Text(text = if (isBengali) "রুম বিস্তারিত বিবরণ ➔" else "View Rooms ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                }
            }
        }
    }
}
