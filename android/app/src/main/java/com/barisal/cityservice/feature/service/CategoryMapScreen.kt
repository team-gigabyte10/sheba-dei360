package com.barisal.cityservice.feature.service

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import android.graphics.Bitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.platform.LocalDensity
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import kotlin.math.*

data class NearestItem(
    val id: String,
    val titleBan: String,
    val titleEng: String,
    val categoryKey: String,
    val addressBan: String,
    val addressEng: String,
    val phone: String,
    val rating: Double,
    val reviews: Int,
    val latLng: LatLng,
    val openHoursBan: String = "২৪/৭ খোলা",
    val openHoursEng: String = "Open 24/7"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryMapScreen(
    categoryKey: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    val defaultLocation = LatLng(22.7010, 90.3535) // Barisal Center
    var userLocation by remember { mutableStateOf<LatLng?>(null) }
    var selectedItem by remember { mutableStateOf<NearestItem?>(null) }
    var isMapView by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Request Location Permission
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        userLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // Get Mock Data for the selected category
    val allItems = remember(categoryKey) { getMockItemsForCategory(categoryKey) }

    val currentCenter = userLocation ?: defaultLocation

    // Sort items by distance from user location or default location
    val sortedItems = remember(allItems, currentCenter, searchQuery) {
        allItems
            .filter { item ->
                searchQuery.isEmpty() ||
                        item.titleBan.contains(searchQuery, ignoreCase = true) ||
                        item.titleEng.contains(searchQuery, ignoreCase = true) ||
                        item.addressBan.contains(searchQuery, ignoreCase = true) ||
                        item.addressEng.contains(searchQuery, ignoreCase = true)
            }
            .sortedBy { calculateDistanceInKm(currentCenter, it.latLng) }
    }

    LaunchedEffect(sortedItems) {
        if (selectedItem == null && sortedItems.isNotEmpty()) {
            selectedItem = sortedItems.first()
        }
    }

    val categoryTitle = getCategoryTitle(categoryKey, isBengali)
    val categoryIcon = getCategoryIcon(categoryKey)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentCenter, 13f)
    }

    LaunchedEffect(selectedItem) {
        selectedItem?.let {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(it.latLng, 14.5f)
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = categoryTitle,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { isMapView = !isMapView }) {
                        Icon(
                            imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                            contentDescription = if (isMapView) "List View" else "Map View"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Search Bar & Filter Header
            Surface(
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                if (isBengali) "নিকটস্থ $categoryTitle খুঁজুন..." else "Search nearest $categoryTitle..."
                            )
                        },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1E3A8A),
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(categoryIcon, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isBengali) "${sortedItems.size}টি নিকটস্থ স্থান পাওয়া গেছে" else "${sortedItems.size} nearest locations found",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                        }

                        // View Toggle Pill Button
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFEFF6FF))
                                .clickable { isMapView = !isMapView }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isMapView) Icons.Default.List else Icons.Default.Map,
                                contentDescription = null,
                                tint = Color(0xFF1E3A8A),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMapView) (if (isBengali) "তালিকা" else "List") else (if (isBengali) "ম্যাপ" else "Map"),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A)
                            )
                        }
                    }
                }
            }

            if (isMapView) {
                // Interactive Map Layout
                Box(modifier = Modifier.fillMaxSize()) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState
                    ) {
                        // User location marker with Person icon badge
                        val userIcon = rememberCategoryMarkerIcon(
                            imageVector = Icons.Default.Person,
                            backgroundColor = Color(0xFF0EA5E9),
                            isSelected = false
                        )
                        userLocation?.let { loc ->
                            Marker(
                                state = MarkerState(position = loc),
                                title = if (isBengali) "আপনার অবস্থান" else "Your Location",
                                snippet = if (isBengali) "বর্তমান অবস্থান" else "Current Position",
                                icon = userIcon
                            )
                        }

                        // Category items markers with category-specific icon badges
                        val categoryVector = getCategoryIcon(categoryKey)
                        val categoryColor = getCategoryColor(categoryKey)

                        sortedItems.forEach { item ->
                            val dist = calculateDistanceInKm(currentCenter, item.latLng)
                            val isSelected = selectedItem?.id == item.id
                            val customIcon = rememberCategoryMarkerIcon(
                                imageVector = categoryVector,
                                backgroundColor = categoryColor,
                                isSelected = isSelected
                            )

                            Marker(
                                state = MarkerState(position = item.latLng),
                                title = if (isBengali) item.titleBan else item.titleEng,
                                snippet = if (isBengali) "দূরত্ব: %.1f কি.মি.".format(dist) else "Distance: %.1f km".format(dist),
                                icon = customIcon,
                                onClick = {
                                    selectedItem = item
                                    true
                                }
                            )
                        }
                    }

                    // Bottom Selected Item Card Overlay
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                    ) {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = selectedItem != null
                        ) {
                            selectedItem?.let { item ->
                                val dist = calculateDistanceInKm(currentCenter, item.latLng)
                                NearestItemCard(
                                    item = item,
                                    distanceKm = dist,
                                    isBengali = isBengali,
                                    onCallClick = { phone ->
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                        context.startActivity(intent)
                                    },
                                    onDirectionClick = { latLng ->
                                        val uri = Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")
                                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                        mapIntent.setPackage("com.google.android.apps.maps")
                                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                                            context.startActivity(mapIntent)
                                        } else {
                                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${latLng.latitude},${latLng.longitude}"))
                                            context.startActivity(browserIntent)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } else {
                // List View Layout
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(sortedItems) { item ->
                        val dist = calculateDistanceInKm(currentCenter, item.latLng)
                        NearestItemCard(
                            item = item,
                            distanceKm = dist,
                            isBengali = isBengali,
                            onCardClick = {
                                selectedItem = item
                                isMapView = true
                            },
                            onCallClick = { phone ->
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                context.startActivity(intent)
                            },
                            onDirectionClick = { latLng ->
                                val uri = Uri.parse("google.navigation:q=${latLng.latitude},${latLng.longitude}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                mapIntent.setPackage("com.google.android.apps.maps")
                                if (mapIntent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(mapIntent)
                                } else {
                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=${latLng.latitude},${latLng.longitude}"))
                                    context.startActivity(browserIntent)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NearestItemCard(
    item: NearestItem,
    distanceKm: Double,
    isBengali: Boolean,
    onCardClick: (() -> Unit)? = null,
    onCallClick: (String) -> Unit,
    onDirectionClick: (LatLng) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onCardClick != null) Modifier.clickable { onCardClick() } else Modifier),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isBengali) item.titleBan else item.titleEng,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE11D48), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBengali) item.addressBan else item.addressEng,
                            fontSize = 15.sp,
                            color = Color(0xFF64748B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Distance Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEFF6FF))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isBengali) "%.1f কি.মি.".format(distanceKm) else "%.1f km".format(distanceKm),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rating & Operating Hours
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "${item.rating}", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = " (${item.reviews})", fontSize = 14.sp, color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isBengali) item.openHoursBan else item.openHoursEng,
                        fontSize = 14.sp,
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons (Call & Directions)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onCallClick(item.phone) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E3A8A)),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, tint = Color(0xFF1E3A8A), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBengali) "কল করুন" else "Call", color = Color(0xFF1E3A8A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Button(
                    onClick = { onDirectionClick(item.latLng) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Directions, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isBengali) "দিকনির্দেশনা" else "Directions", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }
        }
    }
}

// Distance Calculation using Haversine formula
fun calculateDistanceInKm(loc1: LatLng, loc2: LatLng): Double {
    val r = 6371.0 // Earth radius in km
    val latDistance = Math.toRadians(loc2.latitude - loc1.latitude)
    val lonDistance = Math.toRadians(loc2.longitude - loc1.longitude)
    val a = sin(latDistance / 2) * sin(latDistance / 2) +
            cos(Math.toRadians(loc1.latitude)) * cos(Math.toRadians(loc2.latitude)) *
            sin(lonDistance / 2) * sin(lonDistance / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

fun getCategoryTitle(key: String, isBengali: Boolean): String {
    return when (key.lowercase()) {
        "historical", "historical_place", "ঐতিহাসিক স্থান" -> if (isBengali) "ঐতিহাসিক স্থান" else "Historical Place"
        "fire_service", "ফায়ার সার্ভিস" -> if (isBengali) "ফায়ার সার্ভিস" else "Fire Service"
        "police", "thana_police", "থানা-পুলিশ" -> if (isBengali) "থানা-পুলিশ" else "Thana-Police"
        "paurashava", "municipality", "পৌর সেবা" -> if (isBengali) "পৌর সেবা" else "Municipality Service"
        "hotel", "হোটেল" -> if (isBengali) "হোটেল" else "Hotel"
        "restaurant", "রেস্টুরেন্ট" -> if (isBengali) "রেস্টুরেন্ট" else "Restaurant"
        "education", "educational_institution", "শিক্ষা প্রতিষ্ঠান" -> if (isBengali) "শিক্ষা প্রতিষ্ঠান" else "Educational Institution"
        else -> key
    }
}

fun getCategoryIcon(key: String): ImageVector {
    return when (key.lowercase()) {
        "historical", "historical_place", "ঐতিহাসিক স্থান" -> Icons.Default.AccountBalance
        "fire_service", "ফায়ার সার্ভিস" -> Icons.Default.LocalFireDepartment
        "police", "thana_police", "থানা-পুলিশ" -> Icons.Default.LocalPolice
        "paurashava", "municipality", "পৌর সেবা" -> Icons.Default.LocationCity
        "hotel", "হোটেল" -> Icons.Default.Hotel
        "restaurant", "রেস্টুরেন্ট" -> Icons.Default.Restaurant
        "education", "educational_institution", "শিক্ষা প্রতিষ্ঠান" -> Icons.Default.CastForEducation
        else -> Icons.Default.Place
    }
}

@Composable
fun rememberCategoryMarkerIcon(
    imageVector: ImageVector,
    backgroundColor: Color,
    isSelected: Boolean = false
): BitmapDescriptor {
    val density = LocalDensity.current
    val vectorPainter = rememberVectorPainter(imageVector)

    return remember(imageVector, backgroundColor, isSelected) {
        val sizeDp = if (isSelected) 50 else 42
        val sizePx = with(density) { sizeDp.dp.toPx() }.toInt()
        val pinHeightPx = (sizePx * 1.25f).toInt()

        val bitmap = Bitmap.createBitmap(sizePx, pinHeightPx, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)

        val radius = sizePx / 2f
        val centerX = sizePx / 2f
        val centerY = radius

        // 1. Gold outer ring if selected
        if (isSelected) {
            val goldPaint = android.graphics.Paint().apply {
                isAntiAlias = true
                color = android.graphics.Color.parseColor("#FFC107")
                style = android.graphics.Paint.Style.FILL
            }
            canvas.drawCircle(centerX, centerY, radius, goldPaint)
        }

        // 2. Main Circle Fill
        val bgPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = backgroundColor.toArgb()
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, radius - 3f, bgPaint)

        // 3. Crisp White Border
        val borderPaint = android.graphics.Paint().apply {
            isAntiAlias = true
            color = android.graphics.Color.WHITE
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 3f * density.density
        }
        canvas.drawCircle(centerX, centerY, radius - 3f, borderPaint)

        // 4. Pointer Triangle at Bottom
        val pointerPath = android.graphics.Path().apply {
            moveTo(centerX - 10f * density.density, centerY + radius - 5f)
            lineTo(centerX, pinHeightPx.toFloat())
            lineTo(centerX + 10f * density.density, centerY + radius - 5f)
            close()
        }
        canvas.drawPath(pointerPath, bgPaint)

        // 5. Draw vector icon inside the circle
        val composeCanvas = androidx.compose.ui.graphics.Canvas(canvas)
        val iconPadding = sizePx * 0.22f
        val iconSize = sizePx - (iconPadding * 2)

        composeCanvas.save()
        composeCanvas.translate(iconPadding, iconPadding)
        val canvasDrawScope = CanvasDrawScope()
        canvasDrawScope.draw(
            density = density,
            layoutDirection = LayoutDirection.Ltr,
            canvas = composeCanvas,
            size = androidx.compose.ui.geometry.Size(iconSize, iconSize)
        ) {
            with(vectorPainter) {
                draw(size = size, colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White))
            }
        }
        composeCanvas.restore()

        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

fun getCategoryColor(key: String): Color {
    return when (key.lowercase()) {
        "historical", "historical_place", "ঐতিহাসিক স্থান" -> Color(0xFF8B5CF6)
        "fire_service", "ফায়ার সার্ভিস" -> Color(0xFFEF4444)
        "police", "thana_police", "থানা-পুলিশ" -> Color(0xFF1E3A8A)
        "paurashava", "municipality", "পৌর সেবা" -> Color(0xFF0F766E)
        "hotel", "হোটেল" -> Color(0xFFD97706)
        "restaurant", "রেস্টুরেন্ট" -> Color(0xFFF97316)
        "education", "educational_institution", "শিক্ষা প্রতিষ্ঠান" -> Color(0xFF2563EB)
        "doctor", "ডাক্তার", "hospital", "হাসপাতাল" -> Color(0xFFEC4899)
        else -> Color(0xFF2563EB)
    }
}

fun getMockItemsForCategory(key: String): List<NearestItem> {
    return when (key.lowercase()) {
        "historical", "historical_place", "ঐতিহাসিক স্থান" -> listOf(
            NearestItem("h1", "অক্সফোর্ড মিশন চার্চ", "Oxford Mission Church", "historical", "জেল রোড, বরিশাল", "Jail Road, Barisal", "01711122334", 4.8, 340, LatLng(22.7025, 90.3601)),
            NearestItem("h2", "গুঠিয়া মসজিদ", "Guthia Mosque", "historical", "উজিরপুর, বরিশাল", "Wazirpur, Barisal", "01711223344", 4.9, 1250, LatLng(22.8012, 90.2310)),
            NearestItem("h3", "দুর্গাসাগর দিঘি", "Durga Sagar Dighi", "historical", "স্বরূপকাঠি সড়ক, মাধবপাশা, বরিশাল", "Swarupkathi Road, Madhabpasha, Barisal", "01711334455", 4.7, 890, LatLng(22.7850, 90.2830)),
            NearestItem("h4", "বেল পার্ক (বিআইডব্লিউটিএ)", "Bells Park (BIWTA)", "historical", "কীর্তনখোলা নদীর পাড়, বরিশাল", "Kirtankhola Riverfront, Barisal", "01711445566", 4.6, 560, LatLng(22.6950, 90.3700))
        )
        "fire_service", "ফায়ার সার্ভিস" -> listOf(
            NearestItem("f1", "বরিশাল ফায়ার সার্ভিস ও সিভিল ডিফেন্স সদর দপ্তর", "Barisal Central Fire Station", "fire_service", "সদর রোড, বরিশাল", "Sadar Road, Barisal", "01713032222", 4.9, 120, LatLng(22.7015, 90.3650)),
            NearestItem("f2", "রূপাতলী ফায়ার সাব-স্টেশন", "Rupatali Fire Sub-Station", "fire_service", "রূপাতলী বাস টার্মিনাল, বরিশাল", "Rupatali Bus Terminal, Barisal", "01713032223", 4.7, 85, LatLng(22.6780, 90.3450)),
            NearestItem("f3", "কাউনিয়া ফায়ার স্টেশন", "Kawnia Fire Station", "fire_service", "কাউনিয়া প্রধান সড়ক, বরিশাল", "Kawnia Main Road, Barisal", "01713032224", 4.8, 64, LatLng(22.7210, 90.3680))
        )
        "police", "thana_police", "থানা-পুলিশ" -> listOf(
            NearestItem("p1", "কোতোয়ালী মডেল থানা", "Kotwali Model Thana", "police", "জেল রোড, বরিশাল", "Jail Road, Barisal", "01713374261", 4.6, 210, LatLng(22.7040, 90.3620)),
            NearestItem("p2", "বিমানবন্দর থানা", "Airport Police Station", "police", "নথুল্লাবাদ, বরিশাল", "Nathullabad, Barisal", "01713374262", 4.7, 180, LatLng(22.7250, 90.3480)),
            NearestItem("p3", "কাউনিয়া থানা", "Kawnia Police Station", "police", "কাউনিয়া, বরিশাল", "Kawnia, Barisal", "01713374263", 4.5, 95, LatLng(22.7180, 90.3750)),
            NearestItem("p4", "বান্ধ রোড পুলিশ ফাঁড়ি", "Band Road Police Outpost", "police", "বান্ধ রোড, বরিশাল", "Band Road, Barisal", "01713374264", 4.4, 52, LatLng(22.6980, 90.3690))
        )
        "paurashava", "municipality", "পৌর সেবা" -> listOf(
            NearestItem("ps1", "বরিশাল সিটি কর্পোরেশন নগর ভবন", "Barisal City Corporation Nagar Bhaban", "paurashava", "ফজলুল হক এভিনিউ, বরিশাল", "Fazlul Huq Avenue, Barisal", "01711998877", 4.5, 410, LatLng(22.7005, 90.3665), "সকাল ৯.০০ - বিকাল ৫.০০", "9:00 AM - 5:00 PM"),
            NearestItem("ps2", "বিসিসি ওয়ার্ড নং ১০ সেবা কেন্দ্র", "BCC Ward 10 Service Center", "paurashava", "কাউনিয়া, বরিশাল", "Kawnia, Barisal", "01711998878", 4.3, 130, LatLng(22.7150, 90.3700), "সকাল ৯.০০ - বিকাল ৫.০০", "9:00 AM - 5:00 PM"),
            NearestItem("ps3", "বিসিসি পানি শোধনাগার কেন্দ্র", "BCC Water Treatment Plant", "paurashava", "কীর্তনখোলা ঘাট, বরিশাল", "Kirtankhola Ghat, Barisal", "01711998879", 4.4, 88, LatLng(22.6920, 90.3720))
        )
        "hotel", "হোটেল" -> listOf(
            NearestItem("ht1", "গ্র্যান্ড পার্ক হোটেল", "Hotel Grand Park", "hotel", "বান্ধ রোড, বরিশাল", "Band Road, Barisal", "01777778899", 4.8, 520, LatLng(22.6960, 90.3685)),
            NearestItem("ht2", "হোটেল অ্যাথেনা", "Hotel Athena", "hotel", "সদর রোড, বরিশাল", "Sadar Road, Barisal", "01777778890", 4.5, 310, LatLng(22.7010, 90.3640)),
            NearestItem("ht3", "হোটেল এরিনা", "Hotel Arena", "hotel", "ফজলুল হক এভিনিউ, বরিশাল", "Fazlul Huq Avenue, Barisal", "01777778891", 4.6, 240, LatLng(22.7030, 90.3660))
        )
        "restaurant", "রেস্টুরেন্ট" -> listOf(
            NearestItem("r1", "হাঁড়ি রেস্টুরেন্ট", "Handi Restaurant", "restaurant", "সদর রোড, বরিশাল", "Sadar Road, Barisal", "01811223344", 4.7, 980, LatLng(22.7020, 90.3645), "সকাল ১০.০০ - রাত ১১.০০", "10:00 AM - 11:00 PM"),
            NearestItem("r2", "গার্ডেন ইন ক্যাফে", "Garden Inn Cafe", "restaurant", "বান্ধ রোড, বরিশাল", "Band Road, Barisal", "01811223345", 4.6, 650, LatLng(22.6970, 90.3680), "দুপুর ১২.০০ - রাত ১১.০০", "12:00 PM - 11:00 PM"),
            NearestItem("r3", "ছায়াবিথী রিভার ভিউ", "Chaya Bithi River View", "restaurant", "কীর্তনখোলা রিভারসাইড, বরিশাল", "Kirtankhola Riverside, Barisal", "01811223346", 4.8, 1420, LatLng(22.6930, 90.3710))
        )
        "education", "educational_institution", "শিক্ষা প্রতিষ্ঠান" -> listOf(
            NearestItem("e1", "সরকারি ব্রজমোহন (বিএম) কলেজ", "Govt. Brojomohun (BM) College", "education", "কলেজ রোড, বরিশাল", "College Road, Barisal", "01712345678", 4.9, 2300, LatLng(22.7120, 90.3560)),
            NearestItem("e2", "বরিশাল বিশ্ববিদ্যালয়", "University of Barisal", "education", "কর্ণকাঠি, বরিশাল", "Karnakati, Barisal", "01712345679", 4.8, 1800, LatLng(22.6580, 90.3620)),
            NearestItem("e3", "শেফা-ই-বাংলা মেডিকেল কলেজ (শের-ই-বাংলা)", "Sher-e-Bangla Medical College", "education", "বান্দ রোড, বরিশাল", "Band Road, Barisal", "01712345680", 4.9, 1560, LatLng(22.6890, 90.3610)),
            NearestItem("e4", "বরিশাল জিলা স্কুল", "Barisal Zilla School", "education", "সদর রোড, বরিশাল", "Sadar Road, Barisal", "01712345681", 4.7, 950, LatLng(22.7035, 90.3630))
        )
        else -> listOf(
            NearestItem("gen1", "বরিশাল সেবা কেন্দ্র", "Barisal Central Service Hub", key, "সদর রোড, বরিশাল", "Sadar Road, Barisal", "01700000000", 4.5, 100, LatLng(22.7010, 90.3535))
        )
    }
}
