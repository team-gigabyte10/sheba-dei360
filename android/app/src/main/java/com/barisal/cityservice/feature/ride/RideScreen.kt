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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
import com.barisal.cityservice.data.model.RideDriverDto
import com.barisal.cityservice.data.model.RideRequestDto
import com.barisal.cityservice.data.repository.RideRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

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

val sampleVehicles = listOf(
    VehicleOption("bike", "বাইক (Bike)", "Bike Ride", Icons.Default.TwoWheeler, 30, 12, "১ জন", 3, Color(0xFF2563EB)),
    VehicleOption("car", "কার (Car AC)", "Car (AC)", Icons.Default.DirectionsCar, 100, 25, "৪ জন", 5, Color(0xFF0D9488)),
    VehicleOption("cng", "সিএনজি (CNG Auto)", "CNG Auto", Icons.Default.ElectricRickshaw, 50, 15, "৩ জন", 4, Color(0xFFD97706)),
    VehicleOption("pickup", "পিকআপ (Pickup)", "Goods Pickup", Icons.Default.LocalShipping, 250, 40, "১০০০ কেজী", 8, Color(0xFF7C3AED)),
    VehicleOption("ambulance", "অ্যাম্বুলেন্স (Ambulance)", "Emergency Ambulance", Icons.Default.Emergency, 500, 50, "জরুরী সেবা", 2, Color(0xFFDC2626))
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
    return listOf(
        start,
        LatLng(start.latitude, start.longitude + dLng * 0.35),
        LatLng(start.latitude + dLat * 0.50, start.longitude + dLng * 0.35),
        LatLng(start.latitude + dLat * 0.50, start.longitude + dLng * 0.85),
        LatLng(start.latitude + dLat * 0.85, start.longitude + dLng * 0.85),
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
    val coroutineScope = rememberCoroutineScope()
    val rideRepo = remember { RideRepository() }

    SetStatusBarColor()

    var userModeTab by remember { mutableStateOf(0) } // 0: Customer, 1: Driver Mode
    val currentUserId = remember { rideRepo.getCurrentUserId() }

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

    // --- CUSTOMER STATE ---
    var activePinTarget by remember { mutableStateOf("drop") }
    var pickupLatLng by remember { mutableStateOf(LatLng(23.8103, 90.4125)) }
    var dropLatLng by remember { mutableStateOf(LatLng(23.7509, 90.3725)) }
    val pickupMarkerState = rememberMarkerState(position = pickupLatLng)
    val dropMarkerState = rememberMarkerState(position = dropLatLng)

    var pickupAddress by remember { mutableStateOf(if (isBengali) "আমার অবস্থান (বনানী, ঢাকা)" else "My Location (Banani, Dhaka)") }
    var dropAddress by remember { mutableStateOf(if (isBengali) "ধানমন্ডি ২৭, ঢাকা" else "Dhanmondi 27, Dhaka") }

    var selectedVehicle by remember { mutableStateOf(sampleVehicles.first()) }
    var estimatedDistanceKm by remember { mutableStateOf(7.5) }
    var estimatedTimeMins by remember { mutableStateOf(22) }

    val grossFare = (selectedVehicle.baseFare + (estimatedDistanceKm * selectedVehicle.perKmRate)).toInt()

    var currentRequestId by remember { mutableStateOf<String?>(null) }
    val activeRideRequest by rideRepo.listenToRideRequest(currentRequestId ?: "").collectAsState(initial = null)
    val onlineDrivers by rideRepo.getOnlineDrivers(selectedVehicle.id).collectAsState(initial = emptyList())

    // --- DRIVER STATE ---
    val driverProfile by rideRepo.getDriverProfile(currentUserId).collectAsState(initial = null)
    var driverRegName by remember { mutableStateOf("") }
    var driverRegPhone by remember { mutableStateOf("") }
    var driverRegVehicleType by remember { mutableStateOf("bike") }
    var driverRegVehicleModel by remember { mutableStateOf("") }
    var driverRegPlateNumber by remember { mutableStateOf("") }
    var driverRegLicenseNumber by remember { mutableStateOf("") }

    val pendingRequestsForDriver by rideRepo.listenToPendingRideRequests(driverProfile?.vehicleType ?: "").collectAsState(initial = emptyList())
    var activeDriverRideId by remember { mutableStateOf<String?>(null) }
    val activeDriverRide by rideRepo.listenToRideRequest(activeDriverRideId ?: "").collectAsState(initial = null)
    var driverOtpInput by remember { mutableStateOf("") }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(pickupLatLng, 12f)
    }

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
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(midLat, midLng), 13f)
        }
    }

    fun makePhoneCall(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, if (isBengali) "কল করা যাচ্ছে না" else "Could not initiate call", Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            Column {
                GlobalAppBar(
                    title = if (isBengali) "রাইড শেয়ারিং (ShebaRide)" else "ShebaRide",
                    onBackClick = onBack
                )
                TabRow(
                    selectedTabIndex = userModeTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFF2563EB)
                ) {
                    Tab(
                        selected = userModeTab == 0,
                        onClick = { userModeTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBengali) "গ্রাহক (Ride Book)" else "Customer Mode", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = userModeTab == 1,
                        onClick = { userModeTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DriveEta, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBengali) "ড্রাইভার মোড" else "Driver Mode", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (userModeTab == 0) {
            // CUSTOMER MODE VIEW
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
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
                            } else {
                                dropLatLng = tappedLatLng
                                dropAddress = if (isBengali) "চিহ্নিত গন্তব্য ($latStr, $lngStr)" else "Pinned Destination ($latStr, $lngStr)"
                            }
                            calculateRoute(pickupLatLng, dropLatLng, updateCamera = false)
                        }
                    ) {
                        MarkerComposable(
                            state = pickupMarkerState,
                            title = if (isBengali) "পিকআপ লোকেশন" else "Pickup Location",
                            snippet = pickupAddress,
                            anchor = Offset(0.5f, 1.0f)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(46.dp))
                        }

                        MarkerComposable(
                            state = dropMarkerState,
                            title = if (isBengali) "গন্তব্যস্থল" else "Destination",
                            snippet = dropAddress,
                            anchor = Offset(0.5f, 1.0f)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(46.dp))
                        }

                        // Online Drivers Markers from Firestore
                        onlineDrivers.forEach { driver ->
                            MarkerComposable(
                                state = rememberMarkerState(position = LatLng(driver.currentLat, driver.currentLng)),
                                title = driver.name,
                                snippet = "${driver.vehicleModel} (${driver.plateNumber})"
                            ) {
                                Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                            }
                        }

                        Polyline(
                            points = generateRoadPolylinePoints(pickupLatLng, dropLatLng),
                            color = Color(0xFF2563EB),
                            width = 12f,
                            jointType = JointType.ROUND,
                            startCap = RoundCap(),
                            endCap = RoundCap()
                        )
                    }

                    // Map Overlay Route Info Pill
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 12.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("দূরত্ব: ${estimatedDistanceKm} কিমি | সময়: ${estimatedTimeMins} মি", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                        }
                    }
                }

                // Customer Booking Bottom Panel
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0f),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (activeRideRequest != null && activeRideRequest!!.status != "COMPLETED" && activeRideRequest!!.status != "CANCELLED") {
                            // ACTIVE RIDE / SEARCHING STATUS
                            val status = activeRideRequest!!.status
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = when (status) {
                                            "PENDING" -> "🔍 নিকটস্থ ড্রাইভার খোঁজা হচ্ছে..."
                                            "ACCEPTED" -> "🚗 ড্রাইভার আপনার রাইড গ্রহণ করেছেন!"
                                            "ARRIVED" -> "📍 ড্রাইভার পিকআপ পয়েন্টে উপস্থিত!"
                                            "STARTED" -> "🏁 রাইড চলমান রয়েছে..."
                                            else -> "রাইড স্ট্যাটাস: $status"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color(0xFF1E3A8A)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    if (status != "PENDING" && activeRideRequest!!.assignedDriverName.isNotBlank()) {
                                        Text("👤 ড্রাইভার: ${activeRideRequest!!.assignedDriverName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("📞 ফোন: ${activeRideRequest!!.assignedDriverPhone}", fontSize = 13.sp, color = Color.Gray)
                                        Text("🚘 গাড়ি/বাইক: ${activeRideRequest!!.assignedDriverVehicle} (${activeRideRequest!!.assignedDriverPlate})", fontSize = 13.sp, color = Color.DarkGray)
                                        Text("🔑 রাইড OTP: ${activeRideRequest!!.otpCode}", fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        if (status != "PENDING" && activeRideRequest!!.assignedDriverPhone.isNotBlank()) {
                                            Button(
                                                onClick = { makePhoneCall(activeRideRequest!!.assignedDriverPhone) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text("কল করুন")
                                            }
                                        }
                                        OutlinedButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    rideRepo.cancelRideRequest(currentRequestId ?: "")
                                                    currentRequestId = null
                                                }
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text("বাতিল করুন")
                                        }
                                    }
                                }
                            }
                        } else {
                            // RIDE FORM
                            Text(if (isBengali) "১. রাইডের স্থান নির্বাচন করুন:" else "1. Select Ride Locations:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            OutlinedTextField(
                                value = pickupAddress,
                                onValueChange = { pickupAddress = it },
                                label = { Text(if (isBengali) "পিকআপ লোকেশন" else "Pickup Location") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color(0xFF16A34A)) },
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = dropAddress,
                                onValueChange = { dropAddress = it },
                                label = { Text(if (isBengali) "গন্তব্যস্থল" else "Destination") },
                                modifier = Modifier.fillMaxWidth(),
                                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626)) },
                                singleLine = true
                            )

                            Text(if (isBengali) "২. বাহন নির্বাচন করুন:" else "2. Select Vehicle Option:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(sampleVehicles) { vehicle ->
                                    val isSelected = vehicle.id == selectedVehicle.id
                                    Card(
                                        modifier = Modifier
                                            .width(130.dp)
                                            .clickable { selectedVehicle = vehicle },
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
                                        border = androidx.compose.foundation.BorderStroke(
                                            width = if (isSelected) 2.dp else 1.dp,
                                            color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                        )
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(vehicle.icon, contentDescription = null, tint = vehicle.color, modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(if (isBengali) vehicle.nameBn else vehicle.nameEn, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                            Text("৳ ${(vehicle.baseFare + estimatedDistanceKm * vehicle.perKmRate).toInt()}", fontSize = 12.sp, color = Color(0xFF16A34A), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        val req = RideRequestDto(
                                            pickupAddress = pickupAddress,
                                            pickupLat = pickupLatLng.latitude,
                                            pickupLng = pickupLatLng.longitude,
                                            dropAddress = dropAddress,
                                            dropLat = dropLatLng.latitude,
                                            dropLng = dropLatLng.longitude,
                                            vehicleType = selectedVehicle.id,
                                            estimatedDistanceKm = estimatedDistanceKm,
                                            estimatedTimeMins = estimatedTimeMins,
                                            fareAmount = grossFare
                                        )
                                        val res = rideRepo.createRideRequest(req)
                                        if (res.isSuccess) {
                                            currentRequestId = res.getOrNull()
                                            Toast.makeText(context, if (isBengali) "রাইড বুকিং রিকোয়েস্ট তৈরি করা হয়েছে!" else "Ride request created!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                            ) {
                                Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "রাইড নিশ্চিত করুন (৳ $grossFare)" else "Confirm Ride (৳ $grossFare)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        } else {
            // DRIVER MODE VIEW
            if (driverProfile == null) {
                // DRIVER REGISTRATION FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(if (isBengali) "ড্রাইভার হিসেবে নিবন্ধন করুন" else "Register as a Driver", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F766E))
                            Text(if (isBengali) "আপনার ড্রাইভারের তথ্য প্রদান করুন। অ্যাডমিন অনুমোদনের পর আপনি অনলাইন হয়ে রাইড সেবা দিতে পারবেন।" else "Provide driver details for admin approval.", fontSize = 12.sp, color = Color.Gray)

                            OutlinedTextField(value = driverRegName, onValueChange = { driverRegName = it }, label = { Text(if (isBengali) "আপনার নাম" else "Driver Name") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            OutlinedTextField(value = driverRegPhone, onValueChange = { driverRegPhone = it }, label = { Text(if (isBengali) "মোবাইল নম্বর" else "Phone Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            
                            Text(if (isBengali) "যানবাহনের ধরন:" else "Vehicle Type:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            
                            // Flexible Scrollable LazyRow for Vehicle FilterChips
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(sampleVehicles) { vehicle ->
                                    val isSelected = vehicle.id == driverRegVehicleType
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { driverRegVehicleType = vehicle.id },
                                        label = { Text(vehicle.nameBn, fontSize = 11.sp) }
                                    )
                                }
                            }

                            OutlinedTextField(value = driverRegVehicleModel, onValueChange = { driverRegVehicleModel = it }, label = { Text(if (isBengali) "গাড়ি/বাইকের মডেল (যেমন: Yamaha FZ-S)" else "Vehicle Model") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            OutlinedTextField(value = driverRegPlateNumber, onValueChange = { driverRegPlateNumber = it }, label = { Text(if (isBengali) "প্লেট নম্বর (যেমন: ঢাকা মেট্রো হ-১১-২২৩৩)" else "Plate Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                            OutlinedTextField(value = driverRegLicenseNumber, onValueChange = { driverRegLicenseNumber = it }, label = { Text(if (isBengali) "লাইসেন্স নম্বর" else "Driving License Number") }, modifier = Modifier.fillMaxWidth(), singleLine = true)

                            Button(
                                onClick = {
                                    if (driverRegName.isBlank() || driverRegPhone.isBlank() || driverRegVehicleModel.isBlank() || driverRegPlateNumber.isBlank()) {
                                        Toast.makeText(context, if (isBengali) "সকল তথ্য পূরণ করুন" else "Please fill all details", Toast.LENGTH_SHORT).show()
                                    } else {
                                        coroutineScope.launch {
                                            val driverDto = RideDriverDto(
                                                id = currentUserId,
                                                name = driverRegName,
                                                phone = driverRegPhone,
                                                vehicleType = driverRegVehicleType,
                                                vehicleModel = driverRegVehicleModel,
                                                plateNumber = driverRegPlateNumber,
                                                licenseNumber = driverRegLicenseNumber
                                            )
                                            val res = rideRepo.registerDriver(driverDto)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "নিবন্ধন জমা হয়েছে! অ্যাডমিন অনুমোদনের অপেক্ষা করুন।" else "Registration submitted for admin approval!", Toast.LENGTH_LONG).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
                            ) {
                                Text(if (isBengali) "নিবন্ধন আবেদন জমা দিন" else "Submit Application", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            } else if (!driverProfile!!.isApproved) {
                // PENDING ADMIN APPROVAL BANNER
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A))
                    ) {
                        Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.HourglassTop, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(if (isBengali) "ড্রাইভার আইডি অনুমোদন প্রক্রিয়াধীন" else "Application Pending Approval", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFFB45309))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isBengali) "আপনার ড্রাইভার আবেদনটি অ্যাডমিন প্যানেলে যাচাই করা হচ্ছে। অনুমোদন সম্পন্ন হলে আপনি অনলাইন হয়ে রাইড পাবেন।" else "Your driver application is being verified by admin.", fontSize = 12.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                        }
                    }
                }
            } else {
                // APPROVED DRIVER DASHBOARD WITH GOOGLE MAP
                val driver = driverProfile!!
                val driverPos = remember(driver.currentLat, driver.currentLng) { LatLng(driver.currentLat, driver.currentLng) }
                val driverMapCamera = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(driverPos, 13f)
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Top Google Map Container for Driver
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = driverMapCamera,
                            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                            uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
                        ) {
                            // Driver Current GPS Marker
                            MarkerComposable(
                                state = rememberMarkerState(position = driverPos),
                                title = "আমার অবস্থান (Driver)",
                                anchor = Offset(0.5f, 0.5f)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(36.dp))
                            }

                            // Active Ride Markers & Route Polyline
                            if (activeDriverRide != null) {
                                val pickupPos = LatLng(activeDriverRide!!.pickupLat, activeDriverRide!!.pickupLng)
                                val dropPos = LatLng(activeDriverRide!!.dropLat, activeDriverRide!!.dropLng)

                                MarkerComposable(
                                    state = rememberMarkerState(position = pickupPos),
                                    title = "পিকআপ পয়েন্ট",
                                    snippet = activeDriverRide!!.pickupAddress
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(40.dp))
                                }

                                MarkerComposable(
                                    state = rememberMarkerState(position = dropPos),
                                    title = "গন্তব্যস্থল",
                                    snippet = activeDriverRide!!.dropAddress
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(40.dp))
                                }

                                Polyline(
                                    points = generateRoadPolylinePoints(pickupPos, dropPos),
                                    color = Color(0xFF0F766E),
                                    width = 10f,
                                    jointType = JointType.ROUND
                                )
                            } else {
                                // Show pending customer pickup markers on map
                                pendingRequestsForDriver.forEach { req ->
                                    MarkerComposable(
                                        state = rememberMarkerState(position = LatLng(req.pickupLat, req.pickupLng)),
                                        title = "পিকআপ: ${req.customerName}",
                                        snippet = req.pickupAddress
                                    ) {
                                        Icon(Icons.Default.PersonPinCircle, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(36.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Bottom Driver Navigation Sheet
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.0f),
                        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Driver Status Bar Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                    Text("🚗 ${driver.vehicleType.uppercase()} | ${driver.plateNumber}", fontSize = 12.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Bold)
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(if (driver.isOnline) "অনলাইন" else "অফলাইন", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (driver.isOnline) Color(0xFF16A34A) else Color.Gray)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Switch(
                                        checked = driver.isOnline,
                                        onCheckedChange = { online ->
                                            coroutineScope.launch {
                                                rideRepo.updateDriverOnlineStatus(driver.id, online)
                                            }
                                        }
                                    )
                                }
                            }

                            Divider(color = Color(0xFFF1F5F9))

                            if (activeDriverRide != null && activeDriverRide!!.status != "COMPLETED" && activeDriverRide!!.status != "CANCELLED") {
                                // ACTIVE RIDE EXECUTION CARD FOR DRIVER
                                val req = activeDriverRide!!
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA7F3D0))
                                ) {
                                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = when (req.status) {
                                                "ACCEPTED" -> "🚗 রাইড গ্রহণ করেছেন - পিকআপ পয়েন্টে যান"
                                                "ARRIVED" -> "📍 আপনি পিকআপ পয়েন্টে পৌঁছেছেন"
                                                "STARTED" -> "🏁 রাইড চলমান..."
                                                else -> "রাইড স্ট্যাটাস: ${req.status}"
                                            },
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color(0xFF065F46)
                                        )
                                        Text("👤 প্যাসেঞ্জার: ${req.customerName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("📍 পিকআপ: ${req.pickupAddress}", fontSize = 12.sp, color = Color.DarkGray)
                                        Text("🏁 গন্তব্য: ${req.dropAddress}", fontSize = 12.sp, color = Color.DarkGray)
                                        Text("💰 ভাড়া: ৳ ${req.fareAmount}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF16A34A))

                                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                            Button(
                                                onClick = { makePhoneCall(req.customerPhone) },
                                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Icon(Icons.Default.Call, contentDescription = null)
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("কল দিন")
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        // LIFECYCLE ACTION BUTTONS
                                        when (req.status) {
                                            "ACCEPTED" -> {
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            rideRepo.updateRideStatus(req.requestId, "ARRIVED")
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("পৌঁছেছি (Arrived at Pickup)", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                            "ARRIVED" -> {
                                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                                    OutlinedTextField(
                                                        value = driverOtpInput,
                                                        onValueChange = { driverOtpInput = it },
                                                        label = { Text("গ্রাহকের ৪-সংখ্যার OTP লিখুন") },
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        modifier = Modifier.fillMaxWidth(),
                                                        singleLine = true
                                                    )
                                                    Button(
                                                        onClick = {
                                                            if (driverOtpInput.trim() == req.otpCode) {
                                                                coroutineScope.launch {
                                                                    rideRepo.updateRideStatus(req.requestId, "STARTED")
                                                                    Toast.makeText(context, if (isBengali) "OTP ভেরিফাইড! রাইড শুরু হলো।" else "OTP Verified! Ride Started.", Toast.LENGTH_SHORT).show()
                                                                }
                                                            } else {
                                                                Toast.makeText(context, if (isBengali) "ভুল OTP código!" else "Incorrect OTP PIN!", Toast.LENGTH_SHORT).show()
                                                            }
                                                        },
                                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                                        shape = RoundedCornerShape(8.dp)
                                                    ) {
                                                        Text("OTP ভেরিফাই ও রাইড শুরু (Start Ride)", fontWeight = FontWeight.Bold)
                                                    }
                                                }
                                            }
                                            "STARTED" -> {
                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            rideRepo.updateRideStatus(req.requestId, "COMPLETED")
                                                            activeDriverRideId = null
                                                            driverOtpInput = ""
                                                            Toast.makeText(context, if (isBengali) "রাইড সফলভাবে সম্পন্ন হয়েছে!" else "Ride completed successfully!", Toast.LENGTH_LONG).show()
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                                    modifier = Modifier.fillMaxWidth().height(44.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text("রাইড সম্পন্ন করুন (Complete Ride)", fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                    }
                                }
                            } else if (!driver.isOnline) {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
                                ) {
                                    Text(if (isBengali) "রাইড রিকোয়েস্ট পেতে আপনার ড্রাইভার স্ট্যাটাস 'অনলাইন' করুন।" else "Turn status 'Online' to receive ride requests.", modifier = Modifier.padding(16.dp), fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                }
                            } else {
                                Text(if (isBengali) "অপেক্ষমাণ রাইড রিকোয়েস্ট সমূহ:" else "Pending Ride Requests:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                if (pendingRequestsForDriver.isEmpty()) {
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White)
                                    ) {
                                        Text(if (isBengali) "বর্তমানে কোনো রাইড রিকোয়েস্ট নেই" else "No pending ride requests nearby", modifier = Modifier.padding(16.dp), fontSize = 13.sp, color = Color.Gray, textAlign = TextAlign.Center)
                                    }
                                } else {
                                    pendingRequestsForDriver.forEach { req ->
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(14.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color.White),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                                    Text("👤 ${req.customerName}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                                    Text("৳ ${req.fareAmount}", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color(0xFF16A34A))
                                                }
                                                Text("📍 পিকআপ: ${req.pickupAddress}", fontSize = 12.sp, color = Color.DarkGray)
                                                Text("🏁 গন্তব্য: ${req.dropAddress}", fontSize = 12.sp, color = Color.DarkGray)
                                                Text("📏 দূরত্ব: ${req.estimatedDistanceKm} কিমি | ⏱️ সময়: ${req.estimatedTimeMins} মি", fontSize = 11.sp, color = Color.Gray)

                                                Button(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            val res = rideRepo.acceptRideRequest(req.requestId, driver)
                                                            if (res.isSuccess) {
                                                                activeDriverRideId = req.requestId
                                                                Toast.makeText(context, if (isBengali) "রাইড গ্রহণ করা হয়েছে!" else "Ride accepted!", Toast.LENGTH_SHORT).show()
                                                            } else {
                                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                                    modifier = Modifier.fillMaxWidth().height(42.dp),
                                                    shape = RoundedCornerShape(8.dp)
                                                ) {
                                                    Text(if (isBengali) "রাইড গ্রহণ করুন (Accept)" else "Accept Ride", fontWeight = FontWeight.Bold, color = Color.White)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
