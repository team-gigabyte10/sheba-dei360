package com.barisal.cityservice.feature.ride

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
import com.barisal.cityservice.data.model.RentCarDto
import com.barisal.cityservice.data.repository.RentCarRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentCarScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostRentCar: () -> Unit = {},
    onNavigateToRentCarDetail: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val repository = remember { RentCarRepository() }
    val firestoreCars by repository.getRentCarsFlow().collectAsState(initial = emptyList())

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFF1D4ED8) // Deep Blue theme for vehicles

    val subCategories = listOf(
        if (isBengali) "সব" else "All",
        if (isBengali) "প্রাইভেট কার" else "Private Car",
        if (isBengali) "মাইক্রোবাস" else "Microbus",
        if (isBengali) "পিকআপ" else "Pickup",
        if (isBengali) "ট্রাক" else "Truck",
        if (isBengali) "রাইড শেয়ারিং" else "Ride Sharing",
        if (isBengali) "ভ্যান ভাড়া" else "Van Vara",
        if (isBengali) "অটো ভাড়া" else "Auto Vara"
    )

    val dummyCars = listOf(
        RentCarDto(
            id = "preset_1",
            title = if (isBengali) "টয়োটা নোয়া মাইক্রোবাস" else "Toyota Noah Microbus",
            subCategory = if (isBengali) "মাইক্রোবাস" else "Microbus",
            driverName = if (isBengali) "মো: রফিকুল ইসলাম" else "Md. Rofiqul Islam",
            contact = "01711-889900",
            price = "3500",
            priceUnit = "per_day",
            vehicleModel = "Toyota Noah 2018",
            seatingCapacity = if (isBengali) "৭ জন" else "7 Persons",
            hasAC = true,
            address = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal"
        ),
        RentCarDto(
            id = "preset_2",
            title = if (isBengali) "টয়োটা এক্সিও প্রাইভেট কার" else "Toyota Axio Private Car",
            subCategory = if (isBengali) "প্রাইভেট কার" else "Private Car",
            driverName = if (isBengali) "আব্দুর রহিম" else "Abdur Rahim",
            contact = "01712-778899",
            price = "2800",
            priceUnit = "per_day",
            vehicleModel = "Toyota Axio 2019",
            seatingCapacity = if (isBengali) "৪ জন" else "4 Persons",
            hasAC = true,
            address = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal"
        ),
        RentCarDto(
            id = "preset_3",
            title = if (isBengali) "টাটা ১ টন পিকআপ" else "Tata 1 Ton Pickup",
            subCategory = if (isBengali) "পিকআপ" else "Pickup",
            driverName = if (isBengali) "কালাম হোসেন" else "Kalam Hossain",
            contact = "01713-667788",
            price = "2000",
            priceUnit = "trip",
            vehicleModel = "Tata Ace 2020",
            seatingCapacity = if (isBengali) "২ জন" else "2 Persons",
            hasAC = false,
            address = if (isBengali) "রূপাতলী, বরিশাল" else "Rupatali, Barisal"
        )
    )

    val combinedCars = remember(firestoreCars, searchQuery, selectedSubCategory, selectedZilla) {
        val approvedFirestore = firestoreCars.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyCars else dummyCars
        list.filter { car ->
            val matchesSearch = searchQuery.isBlank() ||
                    car.title.contains(searchQuery, ignoreCase = true) ||
                    car.address.contains(searchQuery, ignoreCase = true) ||
                    car.vehicleModel.contains(searchQuery, ignoreCase = true) ||
                    car.subCategory.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    car.subCategory.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(car.subCategory, ignoreCase = true)
            val matchesZilla = selectedZilla == null || car.zilla.contains(selectedZilla!!, ignoreCase = true) || car.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesCategory && matchesZilla
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "গাড়ি ভাড়া" else "Rent a Car",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter", tint = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostRentCar,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Rent Car")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "গাড়ি ভাড়া পোস্ট করুন" else "Post Rent Car",
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
                            text = if (isBengali) "গাড়ি খুঁজুন (কার, পিকআপ, মাইক্রো)..." else "Search Car, Pickup, Micro...",
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

            // Vehicles List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(combinedCars) { car ->
                    RentCarCard(
                        car = car,
                        primaryColor = primaryColor,
                        isBengali = isBengali,
                        onClick = { onNavigateToRentCarDetail(car.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentCarCard(
    car: RentCarDto,
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
                if (car.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = car.coverImage,
                        contentDescription = car.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = primaryColor, modifier = Modifier.size(38.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = car.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = car.subCategory, fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = car.address, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (car.hasAC) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFEFF6FF)
                        ) {
                            Text(
                                text = "AC",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    if (car.seatingCapacity.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AirlineSeatReclineNormal, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = car.seatingCapacity, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }

                val unitText = when (car.priceUnit) {
                    "per_hour" -> if (isBengali) "/ঘণ্টা" else "/hr"
                    "trip" -> if (isBengali) "/ট্রিপ" else "/trip"
                    else -> if (isBengali) "/দিন" else "/day"
                }

                Text(
                    text = "৳${car.price}$unitText",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}
