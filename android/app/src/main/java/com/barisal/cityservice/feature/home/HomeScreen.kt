package com.barisal.cityservice.feature.home

import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.BackHandler

import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import android.content.Intent
import android.net.Uri
import com.barisal.cityservice.R
import com.barisal.cityservice.ui.components.ExitAppDialog
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.language.AppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToProviderDetails: (String) -> Unit = {},
    onNavigateToBookings: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToDoctor: () -> Unit = {},
    onNavigateToHospital: () -> Unit = {},
    onNavigateToHouseRent: () -> Unit = {},
    onNavigateToShopping: () -> Unit = {},
    onNavigateToMatrimony: () -> Unit = {},
    onNavigateToBloodDonor: () -> Unit = {},
    onNavigateToEventService: () -> Unit = {},
    onNavigateToMistriService: () -> Unit = {},
    onNavigateToTutor: () -> Unit = {},
    onNavigateToHotel: () -> Unit = {},
    onNavigateToRestaurant: () -> Unit = {},
    onNavigateToFlatLand: () -> Unit = {},
    onNavigateToCategoryMap: (String) -> Unit = {},
    onNavigateToAllServices: () -> Unit = {},
    onNavigateToHealthServices: () -> Unit = {},
    onNavigateToRide: () -> Unit = {},
    onNavigateToCourier: () -> Unit = {},
    onNavigateToTransportService: () -> Unit = {},
    onNavigateToEmergencyService: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToAddCategory: () -> Unit = {},
    onNavigateToAddSubCategory: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    var showExitDialog by remember { mutableStateOf(false) }
    val activity = LocalContext.current as? Activity
    
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    var backPressedTime by remember { mutableStateOf(0L) }
    
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    
    val view = LocalView.current
    SetStatusBarColor()

    BackHandler {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            val currentTime = System.currentTimeMillis()
            if (currentTime - backPressedTime < 2000) {
                showExitDialog = true
            } else {
                backPressedTime = currentTime
                Toast.makeText(context, if (isBengali) "প্রস্থান অপশন দেখতে আবার ব্যাক চাপুন" else "Press back again to exit", Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showExitDialog) {
        ExitAppDialog(
            onDismissRequest = { showExitDialog = false },
            onExitConfirm = { activity?.finish() }
        )
    }

    var selectedZilla by remember { mutableStateOf<String?>(null) }
    var showZillaFilterDialog by remember { mutableStateOf(false) }

    val categoryRepository = remember { com.barisal.cityservice.data.repository.CategoryRepository() }
    val categoryItems by categoryRepository.getCategoriesFlow()
        .collectAsState(initial = categoryRepository.getDefaultCategories())

    val categories = remember(isBengali, categoryItems) {
        categoryItems.map { item ->
            Category(
                name = item.getDisplayName(isBengali),
                icon = item.fallbackIcon ?: Icons.Default.Category,
                iconUrl = item.iconUrl
            )
        }
    }

    val filteredCategories = remember(searchQuery, categories) {
        if (searchQuery.isBlank()) categories
        else categories.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    val providers = remember(isBengali, selectedZilla, searchQuery) {
        val list = listOf(
            ProviderMock("1", if(isBengali) "মোঃ রুবেল মিয়া" else "Md. Rubel Mia", if(isBengali) "৳ ৬০০ /ঘণ্টা" else "৳600 /hr", 4.8, 213, "বনানী, গুলশান, ঢাকা", "Plumber", if (isBengali) "প্লাম্বার" else "Plumber", Icons.Default.Plumbing),
            ProviderMock("2", if(isBengali) "রহিম ট্রেডার্স" else "Rahim Traders", if(isBengali) "৳ ৪৫০ /ঘণ্টা" else "৳450 /hr", 4.5, 189, "কেডিএ অ্যাভিনিউ, বরিশাল", "General", if (isBengali) "সাধারণ" else "General", Icons.Default.Person),
            ProviderMock("3", if(isBengali) "প্লাম্বিং এক্সপার্ট" else "Plumbing Expert", if(isBengali) "৳ ৭০০ /ঘণ্টা" else "৳700 /hr", 4.9, 310, "ধানমন্ডি, ঢাকা", "Plumber", "Plumber", Icons.Default.Plumbing)
        )
        list.filter { provider ->
            val matchesZilla = selectedZilla == null || provider.area.contains(selectedZilla!!, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() || provider.name.contains(searchQuery, ignoreCase = true) || provider.area.contains(searchQuery, ignoreCase = true)
            matchesZilla && matchesQuery
        }
    }
    
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color(0xCC0F172A), // Dark soft transparent
                modifier = Modifier.width(300.dp)
            ) {
                // Profile Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color(0xFFD4AF37), CircleShape) // Gold border
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo), // Using logo as placeholder
                            contentDescription = "Profile Picture",
                            modifier = Modifier
                                .size(94.dp)
                                .clip(CircleShape)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = if (isBengali) "রিয়া চৌধুরী" else "Riya Chowdhury",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    
                    Text(
                        text = "riya.chowdhury91@gmail.com",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Gold Member Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFD4AF37)) // Gold
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Gold Member",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) "গোল্ড মেম্বার" else "Gold Member",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                HorizontalDivider(color = Color(0x40FFFFFF), modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))
                
                // Menu Items
                DrawerMenuItem(
                    icon = Icons.Default.Home,
                    text = if (isBengali) "হোম" else "Home",
                    isSelected = true,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon = Icons.Default.DateRange,
                    text = if (isBengali) "আমার বুকিং" else "My Bookings",
                    isSelected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    text = if (isBengali) "ওয়ালেট" else "Wallet",
                    isSelected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Category,
                    text = if (isBengali) "ক্যাটাগরি এন্ট্রি (City_Service)" else "Category Entry (City_Service)",
                    isSelected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToAddCategory()
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.FormatListBulleted,
                    text = if (isBengali) "সাব-ক্যাটাগরি এন্ট্রি (City_Service)" else "Sub-Category Entry (City_Service)",
                    isSelected = false,
                    onClick = {
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToAddSubCategory()
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.Settings,
                    text = if (isBengali) "সেটিংস" else "Settings",
                    isSelected = false,
                    onClick = { 
                        coroutineScope.launch { drawerState.close() }
                        onNavigateToSettings()
                    }
                )
                DrawerMenuItem(
                    icon = Icons.Default.HelpOutline,
                    text = if (isBengali) "সাহায্য কেন্দ্র" else "Help Center",
                    isSelected = false,
                    onClick = { coroutineScope.launch { drawerState.close() } }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                GlobalAppBar(
                    title = if (isBengali) "সেবা দেই ৩৬০" else "Sheba Dei 360",
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        // District Location Selector Badge
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showZillaFilterDialog = true }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Select District",
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF0F766E)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = selectedZilla ?: (if (isBengali) "সকল জেলা" else "All Districts"),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E)
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = Color(0xFF0F766E)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        // Language Switcher
                        Row(
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.LightGray.copy(alpha = 0.3f))
                                .clickable {
                                    languageState.currentLanguage = if (isBengali) AppLanguage.ENGLISH else AppLanguage.BENGALI
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = "Change Language",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Black
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBengali) "ENG" else "BAN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text(if (isBengali) "হোম" else "Home") },
                        selected = true,
                        onClick = { },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E3A8A),
                            selectedTextColor = Color(0xFF1E3A8A),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Assignment, contentDescription = "Order") },
                        label = { Text(if (isBengali) "বুকিংস" else "Bookings") },
                        selected = false,
                        onClick = { onNavigateToBookings() },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E3A8A),
                            selectedTextColor = Color(0xFF1E3A8A),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                        label = { Text(if (isBengali) "প্রোফাইল" else "Profile") },
                        selected = false,
                        onClick = { onNavigateToProfile() },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E3A8A),
                            selectedTextColor = Color(0xFF1E3A8A),
                            indicatorColor = Color.Transparent,
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        )
                    )
                }
            }
        ) { innerPadding ->
            if (showZillaFilterDialog) {
                com.barisal.cityservice.ui.components.CustomDialog(
                    onDismissRequest = { showZillaFilterDialog = false },
                    title = if (isBengali) "জেলা নির্বাচন করুন" else "Select District",
                    confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
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
                                Text(if (isBengali) "সকল বাংলাদেশ (রিসেট)" else "All Bangladesh (Reset)", color = Color.Red, fontWeight = FontWeight.Bold)
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
                                    color = if (selectedZilla == zilla) Color(0xFF0F766E) else Color.Black,
                                    fontWeight = if (selectedZilla == zilla) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Auto Slider Banner
                item {
                    AutoSliderBanner()
                }

                // 2. Category Grid Header
                item {
                    Text(
                        text = if (isBengali) "সেবাসমূহ" else "Our Services",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        filteredCategories.chunked(4).forEach { rowCategories ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowCategories.forEach { category ->
                                    CategoryItem(
                                        category = category,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            when (category.name) {
                                                "লোকেশন ভিত্তিক সেবা", "Location Based Services", "সকল সেবা", "All Services" -> onNavigateToAllServices()
                                                "স্বাস্থ্য সেবা", "Health Services" -> onNavigateToHealthServices()
                                                "যাতায়াত সেবা", "Transport Services", "বাস", "Bus", "ট্রেন", "Train", "লঞ্চ", "Launch" -> onNavigateToTransportService()
                                                "ডাক্তার", "Doctor" -> onNavigateToDoctor()
                                                "হাসপাতাল", "Hospital" -> onNavigateToHospital()
                                                "বাসা ভাড়া", "House Rent" -> onNavigateToHouseRent()
                                                "কেনা-কাটা", "Shopping" -> onNavigateToShopping()
                                                "পাত্র-পাত্রী", "Matrimony" -> onNavigateToMatrimony()
                                                "রাইড", "Ride" -> onNavigateToRide()
                                                "কুরিয়ার", "Courier" -> onNavigateToCourier()
                                                "রক্তদাতা", "Blood Donor" -> onNavigateToBloodDonor()
                                                "ইভেন্ট সার্ভিস", "Event Service" -> onNavigateToEventService()
                                                "জরুরী সেবা", "Emergency Service" -> onNavigateToEmergencyService()
                                                "মিস্ত্রি", "Mistri", "Labour" -> onNavigateToMistriService()
                                                "টিউটর", "Tutor" -> onNavigateToTutor()
                                                "ফ্ল্যাট ও জমি", "Flat and Land" -> onNavigateToFlatLand()
                                                else -> onNavigateToCategoryMap(category.name)
                                            }
                                        }
                                    )
                                }
                                repeat(4 - rowCategories.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
                
                // 4. Offers and Ads Carousel
                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth(0.9f)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF2563EB)) // Blue Banner
                                    .clickable { onNavigateToCategoryMap("ঈদ স্পেশাল অফার") }
                                    .padding(16.dp)
                            ) {
                                // Decorative circles to simulate the image background
                                Box(modifier = Modifier.size(80.dp).offset(x = 180.dp, y = (-20).dp).clip(CircleShape).background(Color(0xFFFFC107)))
                                Box(modifier = Modifier.size(100.dp).offset(x = 240.dp, y = 40.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                                Box(modifier = Modifier.size(60.dp).offset(x = 160.dp, y = 80.dp).clip(CircleShape).background(Color(0xFF10B981)))

                                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                    Text(if (isBengali) "ঈদ স্পেশাল অফার!" else "Eid Special Offer!", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isBengali) "৳৫০০ পর্যন্ত ছাড়!" else "Up to ৳500 off!", color = Color(0xFFFFC107), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color.White)
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text(if (isBengali) "বুকিং করুন" else "Book Now", color = Color(0xFF2563EB), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        
                        item {
                            Box(
                                modifier = Modifier
                                    .fillParentMaxWidth(0.9f)
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F766E)) // Teal/Green Banner
                                    .clickable { onNavigateToMistriService() }
                                    .padding(16.dp)
                            ) {
                                // Background decoration
                                Box(modifier = Modifier.size(120.dp).offset(x = 200.dp, y = 20.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)))
                                
                                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                    Text(if (isBengali) "নতুন এসি ইন্সটলেশন" else "New AC Installation", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isBengali) "২০% ছাড় উপভোগ করুন" else "Enjoy 20% Discount", color = Color(0xFFFFC107), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(Color.White)
                                            .padding(horizontal = 16.dp, vertical = 6.dp)
                                    ) {
                                        Text(if (isBengali) "বিস্তারিত দেখুন" else "View Details", color = Color(0xFF0F766E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                // 5. Popular Services Header
                item {
                    Text(if (isBengali) "জনপ্রিয় সেবা প্রোভাইডার" else "Popular Service Providers", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                // 6. Provider List
                items(providers) { provider ->
                    ProviderCard(
                        provider = provider,
                        onCallClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:01712345678"))
                            context.startActivity(intent)
                        },
                        onClick = {
                            onNavigateToProviderDetails(provider.id)
                        }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    val colorPalette = listOf(
        Pair(Color(0xFF3B82F6), Color(0xFFDBEAFE)), // Blue
        Pair(Color(0xFF14B8A6), Color(0xFFCCFBF1)), // Teal
        Pair(Color(0xFFF97316), Color(0xFFFFEDD5)), // Orange
        Pair(Color(0xFF8B5CF6), Color(0xFFEDE9FE)), // Purple
        Pair(Color(0xFFEF4444), Color(0xFFFEE2E2)), // Red
        Pair(Color(0xFF10B981), Color(0xFFD1FAE5)), // Emerald
        Pair(Color(0xFFEAB308), Color(0xFFFEF9C3)), // Yellow
        Pair(Color(0xFFEC4899), Color(0xFFFCE7F3))  // Pink
    )
    val colorIndex = kotlin.math.abs(category.name.hashCode()) % colorPalette.size
    val iconColor = colorPalette[colorIndex].first
    val bgColor = colorPalette[colorIndex].second

    Card(
        modifier = modifier
            .aspectRatio(0.9f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (!category.iconUrl.isNullOrBlank()) {
                coil.compose.AsyncImage(
                    model = category.iconUrl.toCoilModel(),
                    contentDescription = category.name,
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = iconColor,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.name, 
                fontSize = 13.sp, 
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun ProviderCard(
    provider: ProviderMock,
    onCallClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                // Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = provider.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Rating: ${provider.rating}", fontSize = 14.sp, color = Color(0xFF475569))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "(${provider.reviews} রেটিং)", fontSize = 14.sp, color = Color(0xFF475569))
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "এলাকা: ${provider.area}", fontSize = 14.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "মূল্য: ${provider.price}", fontSize = 14.sp, color = Color(0xFF475569))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Buttons and Category
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(provider.icon, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = provider.categoryTextBan, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F766E))
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCallClick,
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF0F766E)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "কল করুন", color = Color(0xFF0F766E), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Button(
                        onClick = onClick,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.CalendarMonth, contentDescription = "Book", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "বুকিং করুন", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

data class Category(
    val name: String,
    val icon: ImageVector,
    val iconUrl: String? = null
)

data class ProviderMock(
    val id: String,
    val name: String,
    val price: String,
    val rating: Double,
    val reviews: Int,
    val area: String,
    val categoryTextEng: String,
    val categoryTextBan: String,
    val icon: ImageVector
)

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0x3360A5FA) else Color.Transparent // Light blue transparent
    val contentColor = if (isSelected) Color(0xFF60A5FA) else Color.White // Lighter blue vs White
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = contentColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = text,
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun AutoSliderBanner() {
    val banners = listOf(
        R.drawable.open,
        R.drawable.open1,
        R.drawable.open2
    )
    
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { banners.size })
    
    LaunchedEffect(pagerState) {
        while (true) {
            kotlinx.coroutines.delay(3000)
            val nextPage = (pagerState.currentPage + 1) % banners.size
            pagerState.animateScrollToPage(nextPage)
        }
    }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().height(150.dp)
        ) { page ->
            Card(
                modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Image(
                    painter = painterResource(id = banners[page]),
                    contentDescription = "Banner ${page + 1}",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(banners.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF1E3A8A) else Color.LightGray
                val width = if (pagerState.currentPage == iteration) 24.dp else 8.dp
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .height(8.dp)
                        .width(width)
                )
            }
        }
    }
}
