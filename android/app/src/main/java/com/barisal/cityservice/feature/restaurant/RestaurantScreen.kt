package com.barisal.cityservice.feature.restaurant

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
import com.barisal.cityservice.data.model.RestaurantDto
import com.barisal.cityservice.data.repository.RestaurantRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostRestaurant: () -> Unit = {},
    onNavigateToRestaurantDetail: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val repository = remember { RestaurantRepository() }
    val firestoreRestaurants by repository.getRestaurantsFlow().collectAsState(initial = emptyList())

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFFC2410C) // Orange/Red theme for food

    val subCategories = listOf(
        if (isBengali) "সব" else "All",
        if (isBengali) "বাংলা খাবার/বিরিয়ানি" else "Bengali Food/Biriyani",
        if (isBengali) "ফাস্ট ফুড ও ক্যাফে" else "Fast Food & Cafe",
        if (isBengali) "চাইনিজ ও থাই" else "Chinese & Thai",
        if (isBengali) "বেকারি ও মিষ্টি" else "Bakery & Sweets"
    )

    val dummyRestaurants = listOf(
        RestaurantDto(
            id = "preset_1",
            name = if (isBengali) "হাজী বিরিয়ানি" else "Haji Biriyani",
            cuisineType = if (isBengali) "বাংলা খাবার, বিরিয়ানি" else "Bengali Food, Biriyani",
            address = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal",
            rating = "4.7",
            reviewsCount = "(1.2k)",
            deliveryAvailable = true,
            contact = "01711-333333"
        ),
        RestaurantDto(
            id = "preset_2",
            name = if (isBengali) "কাশ্মীরি কিচেন" else "Kashmiri Kitchen",
            cuisineType = if (isBengali) "ইন্ডিয়ান, চাইনিজ" else "Indian, Chinese",
            address = if (isBengali) "বান্দ রোড, বরিশাল" else "Band Road, Barisal",
            rating = "4.4",
            reviewsCount = "(850)",
            deliveryAvailable = false,
            contact = "01712-444444"
        )
    )

    val combinedRestaurants = remember(firestoreRestaurants, searchQuery, selectedSubCategory, selectedZilla) {
        val approvedFirestore = firestoreRestaurants.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyRestaurants else dummyRestaurants
        list.filter { restaurant ->
            val matchesSearch = searchQuery.isBlank() ||
                    restaurant.name.contains(searchQuery, ignoreCase = true) ||
                    restaurant.address.contains(searchQuery, ignoreCase = true) ||
                    restaurant.cuisineType.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    restaurant.cuisineType.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(restaurant.cuisineType, ignoreCase = true)
            val matchesZilla = selectedZilla == null || restaurant.zilla.contains(selectedZilla!!, ignoreCase = true) || restaurant.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesCategory && matchesZilla
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "রেস্টুরেন্ট" else "Restaurant",
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
                onClick = onNavigateToPostRestaurant,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Restaurant")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "রেস্টুরেন্ট পোস্ট করুন" else "Post Restaurant",
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
                            text = if (isBengali) "রেস্টুরেন্ট খুঁজুন..." else "Search Restaurant...",
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

            // Restaurant Items List Shown Initially
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(combinedRestaurants) { restaurant ->
                    RestaurantCardDto(
                        restaurant = restaurant,
                        primaryColor = primaryColor,
                        isBengali = isBengali,
                        onClick = { onNavigateToRestaurantDetail(restaurant.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantCardDto(
    restaurant: RestaurantDto,
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
                if (restaurant.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = restaurant.coverImage,
                        contentDescription = restaurant.name,
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
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = restaurant.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = restaurant.cuisineType, fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = restaurant.address, fontSize = 13.sp, color = Color.Gray)
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
                    Text(text = "${restaurant.rating} ${restaurant.reviewsCount}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                }
                Text(text = if (isBengali) "খাবারের মেনু বিবরণ ➔" else "View Menu ➔", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            }
        }
    }
}
