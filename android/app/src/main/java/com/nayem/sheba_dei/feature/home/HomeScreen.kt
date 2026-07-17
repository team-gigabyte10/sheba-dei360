package com.nayem.sheba_dei.feature.home

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
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.nayem.sheba_dei.R
import com.nayem.sheba_dei.ui.components.ExitAppDialog
import com.nayem.sheba_dei.core.language.AppLanguage
import com.nayem.sheba_dei.core.language.LocalAppLanguage
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
    onNavigateToFlatLand: () -> Unit = {}
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

    val categories = if (isBengali) listOf(
        Category("ডাক্তার", Icons.Default.MedicalServices),
        Category("হাসপাতাল", Icons.Default.LocalHospital),
        Category("বাস", Icons.Default.DirectionsBus),
        Category("ট্রেন", Icons.Default.Train),
        Category("লঞ্চ", Icons.Default.DirectionsBoat),
        Category("ঐতিহাসিক স্থান", Icons.Default.AccountBalance),
        Category("বাসা ভাড়া", Icons.Default.House),
        Category("কেনা-কাটা", Icons.Default.ShoppingCart),
        Category("পাত্র-পাত্রী", Icons.Default.People),
        Category("ফায়ার সার্ভিস", Icons.Default.LocalFireDepartment),
        Category("রক্তদাতা", Icons.Default.Bloodtype),
        Category("ডায়াগনস্টিক", Icons.Default.Biotech),
        Category("ইভেন্ট সার্ভিস", Icons.Default.Event),
        Category("রাইড", Icons.Default.TwoWheeler),
        Category("কুরিয়ার", Icons.Default.LocalShipping),
        Category("থানা-পুলিশ", Icons.Default.LocalPolice),
        Category("পৌর সেবা", Icons.Default.LocationCity),
        Category("বিদ্যুৎ সেবা", Icons.Default.ElectricBolt),
        Category("মিস্ত্রি", Icons.Default.Construction),
        Category("জরুরী সেবা", Icons.Default.Emergency),
        Category("চাকরি", Icons.Default.Work),
        Category("উদ্যোক্তা", Icons.Default.BusinessCenter),
        Category("টিউটর", Icons.Default.School),
        Category("হোটেল", Icons.Default.Hotel),
        Category("রেস্টুরেন্ট", Icons.Default.Restaurant),
        Category("ফ্ল্যাট ও জমি", Icons.Default.Landscape),
        Category("শিক্ষা প্রতিষ্ঠান", Icons.Default.CastForEducation),
        Category("নার্সারি", Icons.Default.Nature)
    ) else listOf(
        Category("Doctor", Icons.Default.MedicalServices),
        Category("Hospital", Icons.Default.LocalHospital),
        Category("Bus", Icons.Default.DirectionsBus),
        Category("Train", Icons.Default.Train),
        Category("Launch", Icons.Default.DirectionsBoat),
        Category("Historical Place", Icons.Default.AccountBalance),
        Category("House Rent", Icons.Default.House),
        Category("Shopping", Icons.Default.ShoppingCart),
        Category("Fire Service", Icons.Default.LocalFireDepartment),
        Category("Blood Donor", Icons.Default.Bloodtype),
        Category("Diagnostic", Icons.Default.Biotech),
        Category("Event Service", Icons.Default.Event),
        Category("Ride", Icons.Default.TwoWheeler),
        Category("Courier", Icons.Default.LocalShipping),
        Category("Thana-Police", Icons.Default.LocalPolice),
        Category("Municipality Service", Icons.Default.LocationCity),
        Category("Electricity Service", Icons.Default.ElectricBolt),
        Category("Mistri", Icons.Default.Construction),
        Category("Emergency Service", Icons.Default.Emergency),
        Category("Job", Icons.Default.Work),
        Category("Entrepreneur", Icons.Default.BusinessCenter),
        Category("Tutor", Icons.Default.School),
        Category("Hotel", Icons.Default.Hotel),
        Category("Restaurant", Icons.Default.Restaurant),
        Category("Flat and Land", Icons.Default.Landscape),
        Category("Educational Institution", Icons.Default.CastForEducation),
        Category("Nursery", Icons.Default.Nature)
    )

    val providers = listOf(
        ProviderMock("1", if(isBengali) "মোঃ রুবেল মিয়া" else "Md. Rubel Mia", if(isBengali) "৳ ৬০০ /ঘণ্টা" else "৳600 /hr", 4.8, 213, "বনানী, গুলশান", "Plumber", if (isBengali) "প্লাম্বার" else "Plumber", Icons.Default.Plumbing),
        ProviderMock("2", if(isBengali) "রহিম ট্রেডার্স" else "Rahim Traders", if(isBengali) "৳ ৪৫০ /ঘণ্টা" else "৳450 /hr", 4.5, 189, "মিরপুর, আগারগাঁও", "General", if (isBengali) "সাধারণ" else "General", Icons.Default.Person),
        ProviderMock("3", if(isBengali) "প্লাম্বিং এক্সপার্ট" else "Plumbing Expert", if(isBengali) "৳ ৭০০ /ঘণ্টা" else "৳700 /hr", 4.9, 310, "ধানমন্ডি", "Plumber", "Plumber", Icons.Default.Plumbing)
    )
    
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
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Footer
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* Logout action */ }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color(0xFFDC2626) // Red
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = if (isBengali) "লগ আউট" else "Log Out",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                
                Text(
                    text = if (isBengali) "অ্যাপ ভার্সন v3.1.2" else "App Version v3.1.2",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 24.dp, bottom = 24.dp)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                GlobalAppBar(
                    title = if (isBengali) "বরিশাল সিটি সার্ভিস" else "Barisal City Service",
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
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
                        selected = false,
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
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text(if (isBengali) "অনুসন্ধান" else "Search") },
                        selected = true, // Set search as active based on image
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
                        onClick = { },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    AutoSliderBanner()
                }

                // 3. Category Grid (2 rows of 4 items)
                item {
                    Text(if (isBengali) "আমাদের সেবাসমূহ" else "Our Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        categories.chunked(4).forEach { rowCategories ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                rowCategories.forEach { category ->
                                    CategoryItem(
                                        category = category,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            if (category.name == "ডাক্তার" || category.name == "Doctor") {
                                                onNavigateToDoctor()
                                            } else if (category.name == "হাসপাতাল" || category.name == "Hospital") {
                                                onNavigateToHospital()
                                            } else if (category.name == "বাসা ভাড়া" || category.name == "House Rent") {
                                                onNavigateToHouseRent()
                                            } else if (category.name == "কেনা-কাটা" || category.name == "Shopping") {
                                                onNavigateToShopping()
                                            } else if (category.name == "পাত্র-পাত্রী" || category.name == "Matrimony") {
                                                onNavigateToMatrimony()
                                            } else if (category.name == "রক্তদাতা" || category.name == "Blood Donor") {
                                                onNavigateToBloodDonor()
                                            } else if (category.name == "ইভেন্ট সার্ভিস" || category.name == "Event Service") {
                                                onNavigateToEventService()
                                            } else if (category.name == "মিস্ত্রি" || category.name == "Mistri" || category.name == "Labour") {
                                                onNavigateToMistriService()
                                            } else if (category.name == "টিউটর" || category.name == "Tutor") {
                                                onNavigateToTutor()
                                            } else if (category.name == "হোটেল" || category.name == "Hotel") {
                                                onNavigateToHotel()
                                            } else if (category.name == "রেস্টুরেন্ট" || category.name == "Restaurant") {
                                                onNavigateToRestaurant()
                                            } else if (category.name == "ফ্ল্যাট ও জমি" || category.name == "Flat and Land") {
                                                onNavigateToFlatLand()
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
                                            .clickable { }
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
                                            .clickable { }
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
                    Text(if (isBengali) "জনপ্রিয় সেবা" else "Popular Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                // 6. Provider List
                items(providers) { provider ->
                    ProviderCard(provider) {
                        onNavigateToProviderDetails(provider.id)
                    }
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
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = iconColor,
                modifier = Modifier.size(40.dp)
            )
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
fun ProviderCard(provider: ProviderMock, onClick: () -> Unit) {
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
                    Icon(provider.icon, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = provider.categoryTextBan, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                }
                
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { },
                        shape = RoundedCornerShape(20.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF1E3A8A), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "কল করুন", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold)
                    }
                    Button(
                        onClick = { },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(Icons.Default.Message, contentDescription = "Message", tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "মেসেজ করুন", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

data class Category(
    val name: String,
    val icon: ImageVector
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
