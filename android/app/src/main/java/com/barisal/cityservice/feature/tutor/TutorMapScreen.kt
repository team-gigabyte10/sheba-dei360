package com.barisal.cityservice.feature.tutor

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorMapScreen(
    initialTutor: TutorProfile? = null,
    selectedZilla: String? = null,
    onBack: () -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    var selectedTutor by remember { mutableStateOf<TutorProfile?>(initialTutor ?: sampleTutorsForMap.first()) }

    val defaultLocation = initialTutor?.let {
        LatLng(23.8103, 90.4125)
    } ?: LatLng(23.8103, 90.4125) // Dhaka Banani

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

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "টিউটর লোকেশন ম্যাপ" else "Tutor Map Location",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = false)
            ) {
                sampleTutorsForMap.forEach { tutor ->
                    val pos = tutorToLatLng(tutor)
                    MarkerComposable(
                        state = MarkerState(position = pos),
                        title = tutor.name,
                        snippet = "${tutor.classRange} • ${tutor.salary}",
                        onClick = {
                            selectedTutor = tutor
                            false
                        }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (selectedTutor?.name == tutor.name) Color(0xFF0F766E) else Color(0xFF2563EB),
                            shadowElevation = 8.dp,
                            border = androidx.compose.foundation.BorderStroke(2.dp, Color.White),
                            modifier = Modifier.padding(2.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.School, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = tutor.name,
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
                contentColor = Color(0xFF0F766E)
            ) {
                Icon(Icons.Default.MyLocation, contentDescription = "My Location")
            }

            // Bottom Tutor Profile Card
            selectedTutor?.let { tutor ->
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
                                Text(tutor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                Text(
                                    text = "${tutor.address}, ${tutor.thana}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFCCFBF1)
                            ) {
                                Text(
                                    text = tutor.classRange,
                                    color = Color(0xFF0F766E),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = tutor.bio,
                            fontSize = 13.sp,
                            color = Color.DarkGray,
                            maxLines = 2
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBengali) "বিষয়: ${tutor.subject}" else "Subject: ${tutor.subject}",
                                fontSize = 12.sp,
                                color = Color(0xFF4B5563)
                            )
                            Text(
                                text = if (isBengali) "বেতন: ${tutor.salary}" else "Salary: ${tutor.salary}",
                                fontSize = 12.sp,
                                color = Color(0xFF0F766E),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        Button(
                            onClick = {
                                val phoneNum = tutor.phone.ifEmpty { "01712345678" }
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNum"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBengali) "শিক্ষকের সাথে যোগাযোগ করুন (${tutor.phone})" else "Contact Tutor (${tutor.phone})", fontSize = 13.sp)
                        }
                    }
                }
            }
        }
    }
}

val sampleTutorsForMap = listOf(
    TutorProfile("সুজন শেখ", "30 Dec 2024", "আমি রাজেন্দ্র কলেজ এ বোটানি ডিপার্টমেন্ট ৩য় বর্ষের ছাত্র।", "১ম–৫ম", "৪ দিন/সপ্তাহে", "সকল বিষয় (প্রাথমিক)", "৳৩,৫০০ টাকা", "ছেলে ও মেয়ে", "উপজেলা ভুমি অফিস, ঝিলটুলি", "ফরিদপুর সদর", "01712345678"),
    TutorProfile("শেখ ফেরদৌস", "13 Oct 2025", "বাণিজ্য বিভাগের অভিজ্ঞ শিক্ষক।", "৬ষ্ঠ–১০ম", "৪ দিন/সপ্তাহে", "বাণিজ্য বিভাগ ও সাধারণ গণিত", "৳৪,৫০০ টাকা", "ছেলে", "মেডিকেল কলেজ সংলগ্ন", "ফরিদপুর সদর", "01898765432"),
    TutorProfile("সজীব রায় মৃত্যুঞ্জয়", "17 Dec 2024", "পদার্থবিজ্ঞানে স্নাতকোত্তর (ঢাকা বিশ্ববিদ্যালয়)।", "এইচএসসি", "৩ দিন/সপ্তাহে", "পদার্থবিজ্ঞান, উচ্চতর গণিত", "৬,০০০ টাকা", "ছেলে ও মেয়ে", "মিরপুর ১০, ঢাকা", "পল্লবী", "01911223344"),
    TutorProfile("হাফেজ ক্বারী মাওলানা আব্দুল্লাহ", "20 Jan 2025", "হাফেজে কুরআন ও অভিজ্ঞ আরবি শিক্ষক। ಸಹিহ তাজবীদ সহ কুরআন শিক্ষা দেওয়া হয়।", "আরবি/কুরআন শিক্ষা", "৫ দিন/সপ্তাহে", "তাজবীদ সহ কুরআন শিক্ষা ও আরবি ভাষা", "৳৪,০০০ টাকা", "ছেলে ও মেয়ে", "উত্তরা সেক্টর ৭, ঢাকা", "উত্তরা", "01755667788")
)

fun tutorToLatLng(tutor: TutorProfile): LatLng {
    return when (tutor.name) {
        "সুজন শেখ" -> LatLng(23.6070, 89.8406) // Faridpur
        "শেখ ফেরদৌস" -> LatLng(23.6030, 89.8350) // Faridpur
        "সজীব রায় মৃত্যুঞ্জয়" -> LatLng(23.8069, 90.3687) // Mirpur
        "হাফেজ ক্বারী মাওলানা আব্দুল্লাহ" -> LatLng(23.8720, 90.3980) // Uttara
        else -> LatLng(23.8103, 90.4125)
    }
}
