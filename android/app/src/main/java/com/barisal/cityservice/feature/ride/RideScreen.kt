package com.barisal.cityservice.feature.ride

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.RoundCap
import com.google.maps.android.compose.*
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.delay

data class VehicleOption(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val icon: ImageVector,
    val baseFare: Int,
    val perKmRate: Int,
    val capacity: String,
    val etaMins: Int,
    val color: Color
)

data class RideDriver(
    val id: String,
    val name: String,
    val phone: String,
    val vehicleModel: String,
    val plateNumber: String,
    val rating: Double,
    val distanceKm: Double,
    val otpCode: String,
    val location: LatLng,
    val vehicleType: String = "bike"
)

val sampleVehicles = listOf(
    VehicleOption("bike", "বাইক (Bike)", "Bike Ride", Icons.Default.TwoWheeler, 30, 12, "১ জন", 3, Color(0xFF2563EB)),
    VehicleOption("car", "কার (Car AC)", "Car (AC)", Icons.Default.DirectionsCar, 100, 25, "৪ জন", 5, Color(0xFF0D9488)),
    VehicleOption("cng", "সিএনজি (CNG Auto)", "CNG Auto", Icons.Default.ElectricRickshaw, 50, 15, "৩ জন", 4, Color(0xFFD97706)),
    VehicleOption("pickup", "পিকআপ (Pickup)", "Goods Pickup", Icons.Default.LocalShipping, 250, 40, "১০০০ কেজী", 8, Color(0xFF7C3AED)),
    VehicleOption("ambulance", "অ্যাম্বুলেন্স (Ambulance)", "Emergency Ambulance", Icons.Default.Emergency, 500, 50, "জরুরী সেবা", 2, Color(0xFFDC2626))
)

val sampleDrivers = listOf(
    RideDriver("d1", "মোঃ রফিকুল ইসলাম", "01711223344", "Yamaha FZ-S (Black)", "ঢাকা মেট্রো হ-১১-২২৩৩", 4.9, 0.4, "৪২৮১", LatLng(23.8115, 90.4130), "bike"),
    RideDriver("d2", "আরিফুল হক", "01811998877", "Toyota Premio (White)", "ঢাকা মেট্রো গ-৪৫-৬৭৮৯", 4.8, 0.8, "৯১৪৫", LatLng(23.8080, 90.4160), "car"),
    RideDriver("d3", "কামরুল হাসান", "01922334455", "Bajaj RE CNG Auto", "ঢাকা থ-১২-৩৪৫৬", 4.7, 0.6, "৫৬৭২", LatLng(23.8140, 90.4090), "cng")
)

data class SearchLocation(
    val nameEn: String,
    val nameBn: String,
    val latLng: LatLng
)

val sampleLocations = listOf(
    SearchLocation("Banani, Dhaka", "বনানী, ঢাকা", LatLng(23.8103, 90.4125)),
    SearchLocation("Dhanmondi 27, Dhaka", "ধানমন্ডি ২৭, ঢাকা", LatLng(23.7509, 90.3725)),
    SearchLocation("Gulshan 2 Circle, Dhaka", "গুলশান ২ সার্কেল, ঢাকা", LatLng(23.7949, 90.4143)),
    SearchLocation("Uttara Sector 7, Dhaka", "উত্তরা সেক্টর ৭, ঢাকা", LatLng(23.8720, 90.3980)),
    SearchLocation("Motijheel C/A, Dhaka", "মতিঝিল বা/এ, ঢাকা", LatLng(23.7330, 90.4170)),
    SearchLocation("Shahbagh Square, Dhaka", "শাহবাগ স্কয়ার, ঢাকা", LatLng(23.7380, 90.3960)),
    SearchLocation("Mirpur 10 Circle, Dhaka", "মিরপুর ১০ সার্কেল, ঢাকা", LatLng(23.8069, 90.3687)),
    SearchLocation("Farmgate Bus Stand, Dhaka", "ফার্মগেট বাস স্ট্যান্ড, ঢাকা", LatLng(23.7561, 90.3872)),
    SearchLocation("Hazrat Shahjalal Int. Airport", "হযরত শাহজালাল আন্তর্জাতিক বিমানবন্দর", LatLng(23.8511, 90.4071)),
    SearchLocation("Bashundhara R/A, Dhaka", "বসুন্ধরা আ/এ, ঢাকা", LatLng(23.8151, 90.4255))
)

fun generateRoadPolylinePoints(start: LatLng, end: LatLng): List<LatLng> {
    val dLat = end.latitude - start.latitude
    val dLng = end.longitude - start.longitude

    // Generate multi-segment orthogonal street grid points with realistic road turn corners
    return listOf(
        start,
        // Turn 1: exit local block onto primary street
        LatLng(start.latitude, start.longitude + dLng * 0.35),
        // Turn 2: turn North/South onto major avenue
        LatLng(start.latitude + dLat * 0.50, start.longitude + dLng * 0.35),
        // Turn 3: main city junction turn onto East/West arterial boulevard
        LatLng(start.latitude + dLat * 0.50, start.longitude + dLng * 0.85),
        // Turn 4: turn onto destination street avenue
        LatLng(start.latitude + dLat * 0.85, start.longitude + dLng * 0.85),
        // Turn 5: final turn into destination street block
        LatLng(end.latitude, start.longitude + dLng * 0.85),
        end
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RideScreen(
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        hasLocationPermission = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    var activePinTarget by remember { mutableStateOf("drop") } // "pickup" or "drop"

    var pickupLatLng by remember { mutableStateOf(LatLng(23.8103, 90.4125)) } // Banani fallback
    var dropLatLng by remember { mutableStateOf(LatLng(23.7509, 90.3725)) }   // Dhanmondi 27

    val pickupMarkerState = rememberMarkerState(position = pickupLatLng)
    val dropMarkerState = rememberMarkerState(position = dropLatLng)

    var pickupAddress by remember { mutableStateOf(if (isBengali) "আমার অবস্থান (বনানী, ঢাকা)" else "My Location (Banani, Dhaka)") }
    var dropAddress by remember { mutableStateOf(if (isBengali) "ধানমন্ডি ২৭, ঢাকা" else "Dhanmondi 27, Dhaka") }

    var showPickupSuggestions by remember { mutableStateOf(false) }
    var showDropSuggestions by remember { mutableStateOf(false) }

    var selectedVehicle by remember { mutableStateOf(sampleVehicles.first()) }
    var promoCode by remember { mutableStateOf("") }
    var appliedDiscount by remember { mutableStateOf(0) }

    var isSearchingDriver by remember { mutableStateOf(false) }
    var assignedDriver by remember { mutableStateOf<RideDriver?>(null) }
    var isRideActive by remember { mutableStateOf(false) }

    var estimatedDistanceKm by remember { mutableStateOf(7.5) }
    var estimatedTimeMins by remember { mutableStateOf(22) }

    val grossFare = (selectedVehicle.baseFare + (estimatedDistanceKm * selectedVehicle.perKmRate)).toInt()
    val finalFare = (grossFare - appliedDiscount).coerceAtLeast(0)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pickupLatLng, 12f)
    }

    // Function to calculate route & distance between pickup and drop
    fun calculateRoute(start: LatLng = pickupLatLng, end: LatLng = dropLatLng, updateCamera: Boolean = false) {
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        val distKm = results[0] / 1000.0
        val roundedDist = Math.max(0.5, Math.round(distKm * 10.0) / 10.0)
        estimatedDistanceKm = roundedDist
        estimatedTimeMins = Math.max(3, (roundedDist * 3.0).toInt())

        if (updateCamera) {
            val midLat = (start.latitude + end.latitude) / 2.0
            val midLng = (start.longitude + end.longitude) / 2.0
            val targetZoom = if (roundedDist > 15) 10f else if (roundedDist > 5) 12f else 13.5f
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(midLat, midLng), targetZoom)
        }
    }

    // Keep marker states synchronized when coordinates change
    LaunchedEffect(pickupLatLng) {
        pickupMarkerState.position = pickupLatLng
    }
    LaunchedEffect(dropLatLng) {
        dropMarkerState.position = dropLatLng
    }

    // Listen to Drag events for Pickup Marker
    LaunchedEffect(pickupMarkerState.position) {
        if (pickupMarkerState.position != pickupLatLng) {
            pickupLatLng = pickupMarkerState.position
            val latStr = String.format(java.util.Locale.US, "%.4f", pickupLatLng.latitude)
            val lngStr = String.format(java.util.Locale.US, "%.4f", pickupLatLng.longitude)
            pickupAddress = if (isBengali) "চিহ্নিত ড্র্যাগ পিকআপ ($latStr, $lngStr)" else "Dragged Pickup ($latStr, $lngStr)"
            calculateRoute(pickupLatLng, dropLatLng, updateCamera = false)
        }
    }

    // Listen to Drag events for Drop-off Marker
    LaunchedEffect(dropMarkerState.position) {
        if (dropMarkerState.position != dropLatLng) {
            dropLatLng = dropMarkerState.position
            val latStr = String.format(java.util.Locale.US, "%.4f", dropLatLng.latitude)
            val lngStr = String.format(java.util.Locale.US, "%.4f", dropLatLng.longitude)
            dropAddress = if (isBengali) "চিহ্নিত ড্র্যাগ গন্তব্য ($latStr, $lngStr)" else "Dragged Destination ($latStr, $lngStr)"
            calculateRoute(pickupLatLng, dropLatLng, updateCamera = false)
        }
    }

    // Real GPS Location Fetcher
    fun fetchRealLocation() {
        try {
            if (hasLocationPermission) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        val realLatLng = LatLng(location.latitude, location.longitude)
                        pickupLatLng = realLatLng
                        val latStr = String.format(java.util.Locale.US, "%.4f", location.latitude)
                        val lngStr = String.format(java.util.Locale.US, "%.4f", location.longitude)
                        pickupAddress = if (isBengali) "আমার জিপিএস অবস্থান ($latStr, $lngStr)" else "My GPS Location ($latStr, $lngStr)"
                        cameraPositionState.position = CameraPosition.fromLatLngZoom(realLatLng, 15f)
                        calculateRoute(realLatLng, dropLatLng, updateCamera = true)
                        Toast.makeText(context, if (isBengali) "বর্তমান লোকেশন পাওয়া গেছে!" else "Current location updated!", Toast.LENGTH_SHORT).show()
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
        if (!hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            fetchRealLocation()
        }
    }

    // Driver search simulation logic
    LaunchedEffect(isSearchingDriver) {
        if (isSearchingDriver) {
            delay(3000) // 3 seconds searching simulation
            assignedDriver = sampleDrivers.first()
            isSearchingDriver = false
            isRideActive = true
            Toast.makeText(context, if (isBengali) "ড্রাইভার রাইড গ্রহণ করেছেন!" else "Driver accepted your ride!", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "রাইড শেয়ারিং (ShebaRide)" else "ShebaRide",
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
            // Flexible Touch Google Map Container (Weight 1.4f for large vertical height & 100% smooth touch)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.4f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false),
                    onMapClick = { tappedLatLng ->
                        val latStr = String.format(java.util.Locale.US, "%.4f", tappedLatLng.latitude)
                        val lngStr = String.format(java.util.Locale.US, "%.4f", tappedLatLng.longitude)
                        if (activePinTarget == "pickup") {
                            pickupLatLng = tappedLatLng
                            pickupAddress = if (isBengali) "চিহ্নিত পিকআপ ($latStr, $lngStr)" else "Pinned Pickup ($latStr, $lngStr)"
                            Toast.makeText(context, if (isBengali) "পিকআপ লোকেশন সেট করা হয়েছে!" else "Pickup location set!", Toast.LENGTH_SHORT).show()
                        } else {
                            dropLatLng = tappedLatLng
                            dropAddress = if (isBengali) "চিহ্নিত গন্তব্য ($latStr, $lngStr)" else "Pinned Destination ($latStr, $lngStr)"
                            Toast.makeText(context, if (isBengali) "গন্তব্য পিন করা হয়েছে!" else "Destination pinned!", Toast.LENGTH_SHORT).show()
                        }
                        calculateRoute(pickupLatLng, dropLatLng, updateCamera = false)
                    }
                ) {
                    // Draggable Pickup Marker (GREEN 📍 Pinpoint Marker - NO BACKGROUND BOX)
                    MarkerComposable(
                        state = pickupMarkerState,
                        title = if (isBengali) "পিকআপ লোকেশন" else "Pickup Location",
                        snippet = pickupAddress,
                        draggable = true,
                        anchor = Offset(0.5f, 1.0f),
                        zIndex = 10f
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Pickup Location",
                            tint = Color(0xFF16A34A), // Vibrant Green
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    // Draggable Destination Marker (RED 📍 Pinpoint Marker - NO BACKGROUND BOX)
                    MarkerComposable(
                        state = dropMarkerState,
                        title = if (isBengali) "গন্তব্যস্থল" else "Destination Location",
                        snippet = dropAddress,
                        draggable = true,
                        anchor = Offset(0.5f, 1.0f),
                        zIndex = 10f
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Destination Location",
                            tint = Color(0xFFDC2626), // Vibrant Red
                            modifier = Modifier.size(46.dp)
                        )
                    }

                    // Nearby Drivers Markers with Transparent Background Vehicle Icons (NO BACKGROUND COLOR)
                    sampleDrivers.forEach { driver ->
                        MarkerComposable(
                            state = MarkerState(position = driver.location),
                            title = driver.name,
                            snippet = "${driver.vehicleModel} • ⭐ ${driver.rating}",
                            zIndex = 8f
                        ) {
                            val (vehicleIcon, iconColor) = when (driver.vehicleType) {
                                "bike" -> Pair(Icons.Default.TwoWheeler, Color(0xFF2563EB))
                                "car" -> Pair(Icons.Default.DirectionsCar, Color(0xFF0D9488))
                                "cng" -> Pair(Icons.Default.ElectricRickshaw, Color(0xFFD97706))
                                else -> Pair(Icons.Default.DirectionsCar, Color(0xFF2563EB))
                            }
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = vehicleIcon,
                                    contentDescription = driver.vehicleModel,
                                    tint = iconColor,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }

                    // Multi-segment High-Visibility Road Polyline following grid roads
                    Polyline(
                        points = generateRoadPolylinePoints(pickupLatLng, dropLatLng),
                        color = Color(0xFF2563EB),
                        width = 14f,
                        zIndex = 5f,
                        jointType = JointType.ROUND,
                        startCap = RoundCap(),
                        endCap = RoundCap()
                    )
                }

                // Top Floating Control Bar: Pin Target Selector (Pickup vs Drop-off)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(12.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 8.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = activePinTarget == "pickup",
                            onClick = { activePinTarget = "pickup" },
                            label = { Text(if (isBengali) "📍 পিকআপ পিন" else "📍 Set Pickup", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF16A34A),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = activePinTarget == "drop",
                            onClick = { activePinTarget = "drop" },
                            label = { Text(if (isBengali) "🎯 গন্তব্য পিন" else "🎯 Set Drop-off", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFDC2626),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Floating Action Button on Top Right (My Location)
                FloatingActionButton(
                    onClick = { fetchRealLocation() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(44.dp),
                    containerColor = Color.White,
                    contentColor = Color(0xFF2563EB)
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "My Location", modifier = Modifier.size(22.dp))
                }
            }

            // Bottom Ride Panel (Scrollable Panel for Booking details)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (assignedDriver != null && isRideActive) {
                        // ACTIVE RIDE & DRIVER DETAILS CARD
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF166534)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(assignedDriver!!.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF14532D))
                                        Text(assignedDriver!!.vehicleModel, fontSize = 13.sp, color = Color.DarkGray)
                                        Text("প্লেট নম্বর: ${assignedDriver!!.plateNumber}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF15803D))
                                    }
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("⭐ ${assignedDriver!!.rating}", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFBBF7D0))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("স্টার্ট ওটিপি (OTP Code)", fontSize = 11.sp, color = Color.Gray)
                                        Text(assignedDriver!!.otpCode, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF15803D))
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedButton(
                                            onClick = {
                                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                    type = "text/plain"
                                                    putExtra(Intent.EXTRA_TEXT, "আমি শেবারাইডে ভ্রমণ করছি। ড্রাইভার: ${assignedDriver!!.name}, গাড়ি: ${assignedDriver!!.plateNumber}")
                                                }
                                                context.startActivity(Intent.createChooser(shareIntent, "লাইভ ট্রিপ শেয়ার করুন"))
                                            },
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isBengali) "শেয়ার" else "Share", fontSize = 12.sp)
                                        }

                                        Button(
                                            onClick = {
                                                val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${assignedDriver!!.phone}"))
                                                context.startActivity(callIntent)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                            shape = RoundedCornerShape(10.dp)
                                        ) {
                                            Icon(Icons.Default.Call, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(if (isBengali) "কল করুন" else "Call", color = Color.White, fontSize = 12.sp)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        assignedDriver = null
                                        isRideActive = false
                                        Toast.makeText(context, if (isBengali) "রাইড সম্পন্ন হয়েছে!" else "Ride completed!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D))
                                ) {
                                    Text(if (isBengali) "রাইড শেষ করুন (Complete Ride)" else "Complete Ride", color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        // RIDE BOOKING INPUT FORM
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Pickup Input Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    OutlinedTextField(
                                        value = pickupAddress,
                                        onValueChange = {
                                            pickupAddress = it
                                            showPickupSuggestions = true
                                        },
                                        label = { Text(if (isBengali) "পিকআপ লোকেশন" else "Pickup Location") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showPickupSuggestions = !showPickupSuggestions }) {
                                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF2563EB))
                                            }
                                        }
                                    )
                                }

                                // Pickup Autocomplete Suggestions List
                                val filteredPickupList = sampleLocations.filter {
                                    it.nameEn.contains(pickupAddress, ignoreCase = true) ||
                                    it.nameBn.contains(pickupAddress, ignoreCase = true)
                                }
                                if (showPickupSuggestions && filteredPickupList.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 30.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        shadowElevation = 6.dp,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Column {
                                            filteredPickupList.take(3).forEach { loc ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            pickupAddress = if (isBengali) loc.nameBn else loc.nameEn
                                                            pickupLatLng = loc.latLng
                                                            showPickupSuggestions = false
                                                            calculateRoute(pickupLatLng, dropLatLng)
                                                        }
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(if (isBengali) loc.nameBn else loc.nameEn, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                }
                                                Divider(color = Color(0xFFF1F5F9))
                                            }
                                        }
                                    }
                                }

                                // Drop-off Input Row
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    OutlinedTextField(
                                        value = dropAddress,
                                        onValueChange = {
                                            dropAddress = it
                                            showDropSuggestions = true
                                        },
                                        label = { Text(if (isBengali) "গন্তব্যস্থান (Drop-off Location)" else "Drop-off Location") },
                                        modifier = Modifier.weight(1f),
                                        singleLine = true,
                                        trailingIcon = {
                                            IconButton(onClick = { showDropSuggestions = !showDropSuggestions }) {
                                                Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFFDC2626))
                                            }
                                        }
                                    )
                                }

                                // Drop-off Autocomplete Suggestions List
                                val filteredDropList = sampleLocations.filter {
                                    it.nameEn.contains(dropAddress, ignoreCase = true) ||
                                    it.nameBn.contains(dropAddress, ignoreCase = true)
                                }
                                if (showDropSuggestions && filteredDropList.isNotEmpty()) {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(start = 30.dp),
                                        shape = RoundedCornerShape(12.dp),
                                        color = Color.White,
                                        shadowElevation = 6.dp,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                                    ) {
                                        Column {
                                            filteredDropList.take(3).forEach { loc ->
                                                Row(
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .clickable {
                                                            dropAddress = if (isBengali) loc.nameBn else loc.nameEn
                                                            dropLatLng = loc.latLng
                                                            showDropSuggestions = false
                                                            calculateRoute(pickupLatLng, dropLatLng)
                                                        }
                                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Text(if (isBengali) loc.nameBn else loc.nameEn, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                                }
                                                Divider(color = Color(0xFFF1F5F9))
                                            }
                                        }
                                    }
                                }

                                // Search Route Button
                                Button(
                                    onClick = {
                                        showPickupSuggestions = false
                                        showDropSuggestions = false
                                        calculateRoute()
                                        Toast.makeText(
                                            context,
                                            if (isBengali) "রুট গণনা করা হয়েছে! দূরত্ব: $estimatedDistanceKm কিমি" else "Route calculated! Distance: $estimatedDistanceKm km",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isBengali) "রুট খুঁজুন (Search Route)" else "Search Route",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // VEHICLE TYPE SELECTOR
                        Text(if (isBengali) "যানবাহন নির্বাচন করুন:" else "Select Vehicle:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(sampleVehicles) { vehicle ->
                                val isSelected = vehicle.id == selectedVehicle.id
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { selectedVehicle = vehicle },
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) vehicle.color.copy(alpha = 0.1f) else Color.White
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) vehicle.color else Color(0xFFE2E8F0)
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Icon(vehicle.icon, contentDescription = null, tint = vehicle.color, modifier = Modifier.size(32.dp))
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(if (isBengali) vehicle.nameBn else vehicle.nameEn, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
                                        Text("${vehicle.etaMins} মিনিটে পৌঁছাবে", fontSize = 10.sp, color = Color.Gray)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("৳ ${(vehicle.baseFare + (estimatedDistanceKm * vehicle.perKmRate)).toInt()}", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = vehicle.color)
                                    }
                                }
                            }
                        }

                        // PROMO CODE & FARE BREAKDOWN
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = promoCode,
                                onValueChange = { promoCode = it },
                                label = { Text(if (isBengali) "কুপন কোড (SHEBA50)" else "Coupon Code") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (promoCode.trim().uppercase() == "SHEBA50") {
                                        appliedDiscount = 50
                                        Toast.makeText(context, "৫০ টাকা ছাড় প্রযোজ্য হয়েছে!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "অকার্যকর কুপন কোড", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(if (isBengali) "প্রয়োগ" else "Apply")
                            }
                        }

                        // FARE & CONFIRM BUTTON
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("দূরত্ব: ${estimatedDistanceKm} কিমি | সময়: ${estimatedTimeMins} মিনিট", fontSize = 11.sp, color = Color.Gray)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("৳ $finalFare", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
                                    if (appliedDiscount > 0) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("৳ $grossFare", fontSize = 14.sp, color = Color.Gray, textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough)
                                    }
                                }
                            }

                            Button(
                                onClick = { isSearchingDriver = true },
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = selectedVehicle.color)
                            ) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBengali) "রাইড নিশ্চিত করুন" else "Confirm Ride", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // SEARCHING DRIVER MODAL DIALOG
    if (isSearchingDriver) {
        AlertDialog(
            onDismissRequest = { isSearchingDriver = false },
            title = {
                Text(
                    text = if (isBengali) "ড্রাইভার খোঁজা হচ্ছে..." else "Searching for nearby driver...",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = selectedVehicle.color
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = selectedVehicle.color)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isBengali) "অনুগ্রহ করে অপেক্ষা করুন, নিকটস্থ ড্রাইভারের সাথে সংযোগ স্থাপন করা হচ্ছে।" else "Connecting with the nearest driver, please wait.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { isSearchingDriver = false }) {
                    Text(if (isBengali) "বাতিল করুন" else "Cancel", color = Color.Red)
                }
            }
        )
    }
}
