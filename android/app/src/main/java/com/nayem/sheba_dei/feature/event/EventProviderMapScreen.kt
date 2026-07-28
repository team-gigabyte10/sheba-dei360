package com.nayem.sheba_dei.feature.event

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar

data class EventProvider(
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
    val startingPackage: Int = 15000
)

val sampleEventProviders = listOf(
    EventProvider("e1", "রয়েল ক্যাটারিং & ইভেন্ট ম্যানেজমেন্ট", "01712345678", "ক্যাটারিং সার্ভিস", 4.9, 142, 8, 1.2, "ধানমন্ডি ২৭, ঢাকা", "Dhanmondi 27, Dhaka", LatLng(23.7509, 90.3725), 25000),
    EventProvider("e2", "ড্রীম উইভার ফটোগ্রাফি & ভিডিও", "01898765432", "ফটোগ্রাফার", 4.8, 98, 6, 1.8, "বনানী ব্লক-এফ, ঢাকা", "Banani Block-F, Dhaka", LatLng(23.8115, 90.4130), 18000),
    EventProvider("e3", "সানরাইজ ডেকোরেশন & লাইটিং", "01911223344", "ডেকোরেটর", 4.7, 85, 10, 2.3, "গুলশান ২, ঢাকা", "Gulshan 2, Dhaka", LatLng(23.7949, 90.4143), 15000),
    EventProvider("e4", "অভিজাত কেটারিং & বাবুর্চি", "01755667788", "বাবুর্চি", 4.9, 160, 12, 2.8, "মিরপুর ১০, ঢাকা", "Mirpur 10, Dhaka", LatLng(23.8069, 90.3687), 20000),
    EventProvider("e5", "গ্ল্যামার বিউটি পার্লার & মেহেদী", "01633445566", "পার্লার", 4.8, 115, 5, 3.1, "উত্তরা সেক্টর ৩, ঢাকা", "Uttara Sector 3, Dhaka", LatLng(23.8720, 90.3980), 8000)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventProviderMapScreen(
    category: EventCategory,
    selectedZilla: String? = null,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    var viewMode by remember { mutableStateOf("map") } // "map" or "list"
    var selectedProvider by remember { mutableStateOf<EventProvider?>(sampleEventProviders.first()) }
    var showBookingDialog by remember { mutableStateOf(false) }

    // Event Booking Form State
    var eventType by remember { mutableStateOf("wedding") } // "wedding", "birthday", "corporate", "other"
    var eventDate by remember { mutableStateOf("") }
    var guestCountText by remember { mutableStateOf("150") }
    var venueAddress by remember { mutableStateOf("") }
    var specialNote by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }

    val defaultLocation = LatLng(23.8103, 90.4125) // Dhaka
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
                        Toast.makeText(context, if (isBengali) "আপনার জিপিএস অবস্থান পাওয়া গেছে!" else "GPS location updated!", Toast.LENGTH_SHORT).show()
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
        }
    }

    val filteredProviders = remember(category, selectedZilla) {
        sampleEventProviders.filter { 
            it.categoryName == category.title || category.title == "ইভেন্ট ম্যানেজমেন্ট"
        }.ifEmpty { sampleEventProviders }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "${category.title} সমুহ" else "${category.title} Vendors",
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
            // View Switcher Bar
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
                            Text(
                                text = selectedZilla,
                                color = Color(0xFF1E40AF),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (viewMode == "map") {
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
                                        text = if (isBengali) "প্যাকেজ শুরু: ৳${provider.startingPackage}" else "Start: ৳${provider.startingPackage}",
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

                                    Button(
                                        onClick = { showBookingDialog = true },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.EventAvailable, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isBengali) "ইভেন্ট বুক করুন" else "Book Event", fontSize = 13.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
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
                                        text = if (isBengali) "প্যাকেজ: ৳${provider.startingPackage} থেকে" else "Starting ৳${provider.startingPackage}",
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
                                        Text(if (isBengali) "বুকিং করুন" else "Book Event", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Detailed Event Booking Dialog
        if (showBookingDialog && selectedProvider != null) {
            AlertDialog(
                onDismissRequest = { showBookingDialog = false },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(category.icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBengali) "ইভেন্ট বুকিং ফরম" else "Event Booking Form", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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
                            text = if (isBengali) "${selectedProvider?.name}-কে বুকিং পাঠাচ্ছেন" else "Event booking for ${selectedProvider?.name}",
                            fontSize = 13.sp,
                            color = Color(0xFF2563EB),
                            fontWeight = FontWeight.Medium
                        )

                        // Event Type Selection
                        Text(if (isBengali) "অনুষ্ঠানের ধরন (Event Type):" else "Event Type:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            FilterChip(
                                selected = eventType == "wedding",
                                onClick = { eventType = "wedding" },
                                label = { Text(if (isBengali) "💍 বিবাহ/হলুদ" else "Wedding", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = eventType == "birthday",
                                onClick = { eventType = "birthday" },
                                label = { Text(if (isBengali) "🎂 জন্মদিন" else "Birthday", fontSize = 11.sp) }
                            )
                            FilterChip(
                                selected = eventType == "corporate",
                                onClick = { eventType = "corporate" },
                                label = { Text(if (isBengali) "💼 কর্পোরেট" else "Corporate", fontSize = 11.sp) }
                            )
                        }

                        // Event Date
                        OutlinedTextField(
                            value = eventDate,
                            onValueChange = { eventDate = it },
                            label = { Text(if (isBengali) "অনুষ্ঠানের তারিখ *" else "Event Date *") },
                            placeholder = { Text("DD/MM/YYYY") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Guest Count
                        OutlinedTextField(
                            value = guestCountText,
                            onValueChange = { guestCountText = it },
                            label = { Text(if (isBengali) "মেহমান সংখ্যা (আনুমানিক) *" else "Estimated Guests *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )

                        // Venue Address
                        OutlinedTextField(
                            value = venueAddress,
                            onValueChange = { venueAddress = it },
                            label = { Text(if (isBengali) "অনুষ্ঠানের স্থান / ঠিকানা *" else "Venue Address *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // Contact Phone
                        OutlinedTextField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = { Text(if (isBengali) "আপনার ফোন নম্বর *" else "Contact Phone Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                        )

                        // Special Instructions
                        OutlinedTextField(
                            value = specialNote,
                            onValueChange = { specialNote = it },
                            label = { Text(if (isBengali) "বিশেষ নির্দেশনা / পছন্দ" else "Special Requirements") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (eventDate.isBlank() || contactPhone.isBlank() || venueAddress.isBlank()) {
                                Toast.makeText(context, if (isBengali) "দয়া করে তারিখ, ঠিকানা ও ফোন নম্বর লিখুন" else "Please enter date, address and phone", Toast.LENGTH_SHORT).show()
                            } else {
                                showBookingDialog = false
                                Toast.makeText(
                                    context,
                                    if (isBengali) "ইভেন্ট বুকিং রিকোয়েস্ট সফলভাবে পাঠানো হয়েছে!" else "Event booking request submitted successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text(if (isBengali) "নিশ্চিত বুকিং করুন" else "Confirm Event Booking")
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
