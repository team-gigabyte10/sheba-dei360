package com.barisal.cityservice.feature.mistri

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar

data class MistriProvider(
    val id: String,
    val name: String,
    val phone: String,
    val categoryName: String,
    val rating: Double,
    val reviewCount: Int,
    val experienceYears: Int,
    val distanceKm: Double,
    val addressBn: String,
    val addressEn: String,
    val location: LatLng,
    val isAvailable: Boolean = true,
    val minCharge: Int = 300
)

val sampleMistriProviders = listOf(
    MistriProvider("p1", "মোঃ আল-আমিন মিস্ত্রি", "01712345678", "রাজ মিস্ত্রি", 4.9, 128, 7, 0.8, "বনানী ব্লক-ই, ঢাকা", "Banani Block-E, Dhaka", LatLng(23.8115, 90.4130), true, 400),
    MistriProvider("p2", "রফিকুল ইসলাম", "01898765432", "ইলেকট্রিশিয়ান", 4.8, 95, 5, 1.2, "গুলশান ২, ঢাকা", "Gulshan 2, Dhaka", LatLng(23.7949, 90.4143), true, 350),
    MistriProvider("p3", "শাহ আলম শেখ", "01911223344", "এসি সার্ভিসিং", 4.7, 82, 6, 1.5, "মিরপুর ১০, ঢাকা", "Mirpur 10, Dhaka", LatLng(23.8069, 90.3687), true, 500),
    MistriProvider("p4", "জাহাঙ্গীর হোসেন", "01755667788", "কাঠ মিস্ত্রি", 4.9, 110, 10, 2.1, "ধানমন্ডি ২৭, ঢাকা", "Dhanmondi 27, Dhaka", LatLng(23.7509, 90.3725), true, 450),
    MistriProvider("p5", "কামরুল হাসান", "01633445566", "গাড়ি সার্ভিসিং", 4.8, 140, 8, 2.5, "ফার্মগেট, ঢাকা", "Farmgate, Dhaka", LatLng(23.7561, 90.3872), true, 600),
    MistriProvider("p6", "শফিকুল ইসলাম", "01522334455", "প্লাম্বার (সেনেটারি)", 4.6, 64, 4, 3.0, "উত্তরা সেক্টর ৭, ঢাকা", "Uttara Sector 7, Dhaka", LatLng(23.8720, 90.3980), true, 300)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistriProviderMapScreen(
    category: MistriCategory,
    selectedZilla: String? = null,
    onBack: () -> Unit
) {
    // Intercept Back Button to safely return to Mistri Category Grid
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    var viewMode by remember { mutableStateOf("map") } // "map" or "list"
    var selectedProvider by remember { mutableStateOf<MistriProvider?>(sampleMistriProviders.first()) }
    var showBookingDialog by remember { mutableStateOf(false) }

    // Service Problem Form State
    var problemDescription by remember { mutableStateOf("") }
    var urgencyLevel by remember { mutableStateOf("today") } // "emergency", "today", "flexible"
    var preferredTime by remember { mutableStateOf("morning") } // "morning", "afternoon", "evening"
    var contactPhone by remember { mutableStateOf("") }
    var bookingAddress by remember { mutableStateOf("") }

    val defaultLocation = LatLng(23.8103, 90.4125) // Banani, Dhaka
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 13f)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (hasLocationPermission) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val realLatLng = LatLng(location.latitude, location.longitude)
                        userLocation = realLatLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(realLatLng, 14f)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    fun fetchRealLocation() {
        try {
            if (hasLocationPermission) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val realLatLng = LatLng(location.latitude, location.longitude)
                        userLocation = realLatLng
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(realLatLng, 14f)
                        Toast.makeText(context, if (isBengali) "আপনার জিপিএস অবস্থান পাওয়া গেছে!" else "Current location updated!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, if (isBengali) "জিপিএস সিগন্যাল খোঁজা হচ্ছে..." else "Locating GPS signal...", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            fetchRealLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val filteredProviders = remember(category, selectedZilla) {
        sampleMistriProviders.filter { 
            it.categoryName == category.name || category.name == "অন্যান্য মিস্ত্রি" 
        }.ifEmpty { sampleMistriProviders }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "${category.name} সমুহ" else "${category.name} Providers",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Control Bar: View Switcher (Map vs List) & Zilla indicator
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = viewMode == "map",
                            onClick = { viewMode = "map" },
                            label = { Text(if (isBengali) "🗺️ ম্যাপ ভিউ" else "🗺️ Map View", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = viewMode == "list",
                            onClick = { viewMode = "list" },
                            label = { Text(if (isBengali) "📋 তালিকা (${filteredProviders.size})" else "📋 List (${filteredProviders.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    if (selectedZilla != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFFEFF6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B82F6))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(selectedZilla, color = Color(0xFF1E40AF), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            if (viewMode == "map") {
                // MAP VIEW MODE
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
                    ) {
                        filteredProviders.forEach { provider ->
                            MarkerComposable(
                                state = MarkerState(position = provider.location),
                                title = provider.name,
                                snippet = "⭐ ${provider.rating} • ${provider.distanceKm} km",
                                onClick = {
                                    selectedProvider = provider
                                    false
                                }
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = if (selectedProvider?.id == provider.id) Color(0xFF16A34A) else Color(0xFF2563EB),
                                    shadowElevation = 8.dp,
                                    border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(category.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "⭐ ${provider.rating}",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Floating Action Button for My Location
                    FloatingActionButton(
                        onClick = { fetchRealLocation() },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(44.dp),
                        containerColor = Color.White,
                        contentColor = Color(0xFF2563EB)
                    ) {
                        Icon(Icons.Default.MyLocation, contentDescription = "My Location")
                    }

                    // Bottom Provider Preview Card
                    selectedProvider?.let { provider ->
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(16.dp),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White,
                            shadowElevation = 12.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                        Text(
                                            text = if (isBengali) provider.addressBn else provider.addressEn,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("${provider.rating} (${provider.reviewCount})", color = Color(0xFF92400E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBengali) "অভিজ্ঞতা: ${provider.experienceYears} বছর" else "Exp: ${provider.experienceYears} Yrs",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                    Text(
                                        text = if (isBengali) "দূরত্ব: ${provider.distanceKm} কিমি" else "Dist: ${provider.distanceKm} km",
                                        fontSize = 12.sp,
                                        color = Color(0xFF2563EB),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // Call Button
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isBengali) "কল করুন" else "Call Now", fontSize = 13.sp)
                                    }

                                    // Book Service Button
                                    Button(
                                        onClick = { showBookingDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isBengali) "বুক করুন" else "Book Service", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // LIST VIEW MODE
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredProviders) { provider ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedProvider = provider
                                    viewMode = "map"
                                },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = Color(0xFFEFF6FF),
                                            modifier = Modifier.size(44.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(category.icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                                            }
                                        }
                                        Column {
                                            Text(provider.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                                            Text(
                                                text = if (isBengali) provider.addressBn else provider.addressEn,
                                                fontSize = 12.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color(0xFFFEF3C7)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text("${provider.rating}", color = Color(0xFF92400E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (isBengali) "অভিজ্ঞতা: ${provider.experienceYears} বছর" else "Experience: ${provider.experienceYears} Yrs",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4B5563)
                                    )
                                    Text(
                                        text = if (isBengali) "চার্জ: ৳${provider.minCharge} থেকে" else "Starting ৳${provider.minCharge}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF16A34A),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                HorizontalDivider(color = Color(0xFFF1F5F9))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${provider.phone}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(if (isBengali) "কল করুন" else "Call", color = Color(0xFF16A34A), fontSize = 12.sp)
                                    }

                                    Button(
                                        onClick = {
                                            selectedProvider = provider
                                            showBookingDialog = true
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Text(if (isBengali) "বুকিং করুন" else "Book Now", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Comprehensive Service Booking Confirmation Dialog
        if (showBookingDialog && selectedProvider != null) {
            AlertDialog(
                onDismissRequest = { showBookingDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(category.icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBengali) "সার্ভিস বুকিং ফর্ম" else "Service Booking Form", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "${selectedProvider?.name}-কে বুকিং পাঠাচ্ছেন (${category.name})" else "Booking request for ${selectedProvider?.name} (${category.name})",
                            fontSize = 13.sp,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Medium
                        )

                        // Problem Description Field
                        OutlinedTextField(
                            value = problemDescription,
                            onValueChange = { problemDescription = it },
                            label = { Text(if (isBengali) "সমস্যার বিবরণ লিখুন *" else "Describe the Problem *") },
                            placeholder = { Text(if (isBengali) "যেমন: পানির পাইপ লিকেজ, লাইট ঝুলছে না..." else "e.g., Water pipe leaking, light fixture broken...") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5
                        )

                        // Urgency Level Selector
                        Text(
                            text = if (isBengali) "অগ্রাধিকার স্তর (Urgency Level):" else "Urgency Level:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = urgencyLevel == "emergency",
                                onClick = { urgencyLevel = "emergency" },
                                label = { Text(if (isBengali) "🚨 জরুরি" else "🚨 Urgent", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFDC2626), selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = urgencyLevel == "today",
                                onClick = { urgencyLevel = "today" },
                                label = { Text(if (isBengali) "📅 আজই" else "📅 Today", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF2563EB), selectedLabelColor = Color.White)
                            )
                            FilterChip(
                                selected = urgencyLevel == "flexible",
                                onClick = { urgencyLevel = "flexible" },
                                label = { Text(if (isBengali) "⏳ যেকোনো সময়" else "⏳ Flexible", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF0D9488), selectedLabelColor = Color.White)
                            )
                        }

                        // Preferred Time Slot Selector
                        Text(
                            text = if (isBengali) "পছন্দের সময় (Preferred Time):" else "Preferred Time:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = preferredTime == "morning",
                                onClick = { preferredTime = "morning" },
                                label = { Text(if (isBengali) "🌅 সকাল (8AM-12PM)" else "🌅 Morning", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = preferredTime == "afternoon",
                                onClick = { preferredTime = "afternoon" },
                                label = { Text(if (isBengali) "☀️ দুপুর (2PM-5PM)" else "☀️ Afternoon", fontSize = 10.sp) }
                            )
                            FilterChip(
                                selected = preferredTime == "evening",
                                onClick = { preferredTime = "evening" },
                                label = { Text(if (isBengali) "🌙 সন্ধ্যা (6PM-9PM)" else "🌙 Evening", fontSize = 10.sp) }
                            )
                        }

                        // Contact Phone Field
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text(if (isBengali) "যোগাযোগের ফোন নম্বর *" else "Contact Phone Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        // Full Address Field
                        OutlinedTextField(
                            value = bookingAddress,
                            onValueChange = { bookingAddress = it },
                            label = { Text(if (isBengali) "আপনার সম্পূর্ণ ঠিকানা *" else "Your Full Address *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (problemDescription.isBlank() || contactPhone.isBlank()) {
                                Toast.makeText(context, if (isBengali) "দয়া করে সমস্যা ও ফোন নম্বর লিখুন" else "Please enter problem description and phone number", Toast.LENGTH_SHORT).show()
                            } else {
                                showBookingDialog = false
                                problemDescription = ""
                                Toast.makeText(
                                    context,
                                    if (isBengali) "মিস্ত্রি বুকিং রিকোয়েস্ট সফলভাবে পাঠানো হয়েছে!" else "Service booking request submitted successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(if (isBengali) "নিশ্চিত বুকিং করুন" else "Confirm Booking")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBookingDialog = false }) {
                        Text(if (isBengali) "বাতিল" else "Cancel")
                    }
                }
            )
        }
    }
}
