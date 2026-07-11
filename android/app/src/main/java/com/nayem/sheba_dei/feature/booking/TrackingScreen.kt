package com.nayem.sheba_dei.feature.booking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.nayem.sheba_dei.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val providerLocation = LatLng(23.7561, 90.3872) // Farmgate
    var myLocation by remember { mutableStateOf<LatLng?>(null) }
    var routePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }
    var distanceText by remember { mutableStateOf("২.৫ কি.মি.") }
    var durationText by remember { mutableStateOf("8 মিনিট") }
    
    val apiKey = "AIzaSyBiols4lFvOc7_rGeOZVI6l-YE617w7xR0"
    
    // Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        myLocation = LatLng(location.latitude, location.longitude)
                    }
                }
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
    }

    // Check permissions on start
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        myLocation = LatLng(location.latitude, location.longitude)
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
    
    // Fetch Directions when location is available
    LaunchedEffect(myLocation) {
        val origin = myLocation
        if (origin != null) {
            withContext(Dispatchers.IO) {
                try {
                    val urlString = "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}&destination=${providerLocation.latitude},${providerLocation.longitude}&key=$apiKey"
                    val connection = URL(urlString).openConnection() as HttpURLConnection
                    connection.requestMethod = "GET"
                    
                    if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                        val response = connection.inputStream.bufferedReader().use { it.readText() }
                        val jsonObject = JSONObject(response)
                        val routes = jsonObject.optJSONArray("routes")
                        
                        if (routes != null && routes.length() > 0) {
                            val route = routes.getJSONObject(0)
                            val overviewPolyline = route.getJSONObject("overview_polyline").getString("points")
                            val decodedPath = com.google.maps.android.PolyUtil.decode(overviewPolyline)
                            
                            val legs = route.getJSONArray("legs")
                            if (legs.length() > 0) {
                                val leg = legs.getJSONObject(0)
                                val dist = leg.getJSONObject("distance").getString("text")
                                val dur = leg.getJSONObject("duration").getString("text")
                                
                                withContext(Dispatchers.Main) {
                                    routePoints = decodedPath
                                    distanceText = dist
                                    durationText = dur
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val cameraPositionState = com.google.maps.android.compose.rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(providerLocation, 13f)
            }
            
            // Adjust camera if location is fetched
            LaunchedEffect(myLocation) {
                myLocation?.let {
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(it, 13.5f)
                }
            }

            com.google.maps.android.compose.GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Provider Marker
                com.google.maps.android.compose.Marker(
                    state = com.google.maps.android.compose.MarkerState(position = providerLocation),
                    title = "মারুফ হোসেন",
                    snippet = "আপনার দিকে আসছেন"
                )
                
                // My Location Marker
                myLocation?.let {
                    com.google.maps.android.compose.Marker(
                        state = com.google.maps.android.compose.MarkerState(position = it),
                        title = "আপনার অবস্থান",
                        icon = com.google.android.gms.maps.model.BitmapDescriptorFactory.defaultMarker(com.google.android.gms.maps.model.BitmapDescriptorFactory.HUE_AZURE)
                    )
                }
                
                // Route Line
                if (routePoints.isNotEmpty()) {
                    com.google.maps.android.compose.Polyline(
                        points = routePoints,
                        color = Color(0xFF2563EB), // Blue route color
                        width = 12f
                    )
                }
            }
            
            // Back Button Overlay
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.7f), CircleShape)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
            }
            
            // Bottom Sheet Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                Color(0xFFF3F4F6).copy(alpha = 0.85f)
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Handle Bar
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color.LightGray)
                            .align(Alignment.CenterHorizontally)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Title
                    Text(
                        text = "মারুফ আপনার দিকে আসছেন",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Provider Info
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            painter = painterResource(id = R.drawable.dummy_person),
                            contentDescription = "Driver Avatar",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color.White, CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("মারুফ হোসেন", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("রেটিং: ৪.৯", fontSize = 14.sp, color = Color.DarkGray)
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                            }
                            Text("গাড়ী: টয়োটা একুয়া (ঢাকা মেট্রো-গ ১২-৩৪৫৬)", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // ETA and Distance
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if(durationText.contains("min")) "ETA ${durationText.replace("mins", "মিনিট").replace("min", "মিনিট")}" else "ETA $durationText", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if(distanceText.contains("km")) "${distanceText.replace("km", "কি.মি.")}" else distanceText, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0F2FE).copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("কল করুন", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                        
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3E8FF).copy(alpha = 0.7f)),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color.White)
                        ) {
                            Icon(Icons.Default.Message, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("বার্তা পাঠান", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
