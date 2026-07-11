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
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Plumbing
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
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
    onNavigateToSettings: () -> Unit = {}
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
    if (!view.isInEditMode) {
        SideEffect {
            var ctx = view.context
            while (ctx is android.content.ContextWrapper) {
                if (ctx is Activity) break
                ctx = ctx.baseContext
            }
            if (ctx is Activity) {
                val window = ctx.window
                window.statusBarColor = android.graphics.Color.parseColor("#020617") // Very Dark Navy / Almost Black
                androidx.core.view.WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            }
        }
    }

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
        Category("রিড", Icons.Default.MenuBook),
        Category("খাবার", Icons.Default.Restaurant),
        Category("ডাক্তার", Icons.Default.MedicalServices),
        Category("হোম সার্ভিস", Icons.Default.Home),
        Category("শিক্ষা", Icons.Default.School),
        Category("পরিবহন", Icons.Default.DirectionsCar),
        Category("সুপার শপ", Icons.Default.ShoppingCart),
        Category("ফিটনেস", Icons.Default.FitnessCenter)
    ) else listOf(
        Category("Read", Icons.Default.MenuBook),
        Category("Food", Icons.Default.Restaurant),
        Category("Doctor", Icons.Default.MedicalServices),
        Category("Home Service", Icons.Default.Home),
        Category("Education", Icons.Default.School),
        Category("Transport", Icons.Default.DirectionsCar),
        Category("Super Shop", Icons.Default.ShoppingCart),
        Category("Fitness", Icons.Default.FitnessCenter)
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
                TopAppBar(
                    title = { Text(if (isBengali) "বরিশাল সিটি সার্ভিস" else "Barisal City Service", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { coroutineScope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF1E3A8A), // Navy Blue
                        titleContentColor = Color.White,
                        navigationIconContentColor = Color.White,
                        actionIconContentColor = Color.White
                    ),
                    actions = {
                        Row(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.2f))
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
                                tint = Color.White
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isBengali) "ENG" else "BAN",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
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
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text(if (isBengali) "সার্ভিস খুঁজুন..." else "Search services...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
                        trailingIcon = { Icon(Icons.Default.Mic, contentDescription = "Voice", tint = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )
                }

                // 3. Category Grid (2 rows of 4 items)
                item {
                    Text(if (isBengali) "আমাদের সেবাসমূহ" else "Our Services", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            categories.take(4).forEach { category ->
                                CategoryItem(category, Modifier.weight(1f))
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            categories.drop(4).take(4).forEach { category ->
                                CategoryItem(category, Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                // 4. Banner Placeholder
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
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
fun CategoryItem(category: Category, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable { },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFEFF6FF)), // Very light blue
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = Color(0xFF2563EB), // Primary blue
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name, 
            fontSize = 12.sp, 
            color = Color(0xFF0F172A), // Dark slate
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
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
