package com.barisal.cityservice.feature.shopping

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.location.Location
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import android.Manifest
import com.google.android.gms.location.LocationServices
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.Polygon
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingDetailsScreen(
    productId: String,
    onBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()
    
    val primaryColor = Color(0xFFE65100) // Orange theme
    
    // Fetch product using productId. Mocking here.
    val product = dummyProducts.find { it.id == productId } ?: dummyProducts.first()

    val hasLocationPermission = com.barisal.cityservice.core.utils.rememberLocationPermissionState()

    val parts = product.latLng.split(",")
    val sellerLat = parts.getOrNull(0)?.toDoubleOrNull() ?: 23.6061
    val sellerLng = parts.getOrNull(1)?.toDoubleOrNull() ?: 89.8406
    val sellerLocation = LatLng(sellerLat, sellerLng)
    
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var myLocation by remember { mutableStateOf<LatLng?>(null) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
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

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sellerLocation, 10f)
    }

    LaunchedEffect(myLocation) {
        myLocation?.let { userLoc ->
            val centerLat = (userLoc.latitude + sellerLocation.latitude) / 2
            val centerLng = (userLoc.longitude + sellerLocation.longitude) / 2
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(centerLat, centerLng), 10.5f)
        }
    }

    var showBuyNowDialog by remember { mutableStateOf(false) }
    var showChatDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "পণ্যের বিবরণ" else "Product Details",
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showChatDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "চ্যাট করুন" else "Chat", fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { showBuyNowDialog = true },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBengali) "সরাসরি অর্ডার" else "Buy Now", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF9FAFB)) // Very light gray
                .verticalScroll(rememberScrollState(), enabled = !cameraPositionState.isMoving)
        ) {
            // Top Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .background(Color(0xFFE5E7EB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(80.dp))
                
                // Carousel Dots Mock inside image
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(4) { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (index == 0) 8.dp else 6.dp)
                                .clip(CircleShape)
                                .background(if (index == 0) primaryColor else Color.White.copy(alpha = 0.5f))
                        )
                    }
                }
            }
            
            // Details Content
            Column(modifier = Modifier.padding(20.dp)) {
                // Name & Price Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = product.productName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = product.price,
                        color = primaryColor,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Date
                Text(
                    text = if (isBengali) "প্রকাশের তারিখ: ${product.date}" else "Posted on: ${product.date}",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Shop Details Section
                Text(
                    text = if (isBengali) "দোকানের বিবরণ" else "Shop Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).clip(CircleShape).background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = primaryColor)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(product.shopName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.DarkGray)
                        Text(product.address, fontSize = 14.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Description Section
                Text(
                    text = if (isBengali) "পণ্যের বিবরণ" else "Description",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = product.description,
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                Divider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))
                
                // Actions (Contact & Map)
                Text(
                    text = if (isBengali) "যোগাযোগ করুন" else "Contact Seller",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Contact Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White)
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${product.contactInfo}")
                            }
                            context.startActivity(intent)
                        }
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(Color(0xFFE8F5E9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color(0xFF2E7D32))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (isBengali) "কল করুন" else "Call Now",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                        Text(
                            text = product.contactInfo,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 18.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Location & Map Section
                Text(
                    text = if (isBengali) "অবস্থান এবং দূরত্ব" else "Location & Distance",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                val activeUserLocation = myLocation ?: LatLng(23.8103, 90.4125)
                val distanceResults = FloatArray(1)
                Location.distanceBetween(activeUserLocation.latitude, activeUserLocation.longitude, sellerLat, sellerLng, distanceResults)
                val distanceKm = distanceResults[0] / 1000f
                val distanceText = String.format("%.1f", distanceKm)

                Text(
                    text = if (isBengali) "আপনার থেকে দূরত্ব: $distanceText কি.মি." else "Distance from you: $distanceText km",
                    color = Color.DarkGray,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                ) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(isMyLocationEnabled = true),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
                    ) {
                        // Seller Marker
                        Marker(
                            state = MarkerState(position = sellerLocation),
                            title = product.shopName,
                            snippet = if (isBengali) "বিক্রেতার অবস্থান" else "Seller Location",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)
                        )

                        // User Location Marker
                        myLocation?.let { userLoc ->
                            Marker(
                                state = MarkerState(position = userLoc),
                                title = if (isBengali) "আপনার অবস্থান" else "Your Location",
                                snippet = if (isBengali) "বর্তমান অবস্থান" else "Current Position",
                                icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                            )
                        }

                        // Connecting Route Line (Polyline)
                        Polyline(
                            points = listOf(activeUserLocation, sellerLocation),
                            color = primaryColor,
                            width = 10f
                        )

                        // Polygon Corridor between User & Seller Location
                        Polygon(
                            points = listOf(
                                activeUserLocation,
                                LatLng(activeUserLocation.latitude, sellerLocation.longitude),
                                sellerLocation,
                                LatLng(sellerLocation.latitude, activeUserLocation.longitude)
                            ),
                            fillColor = primaryColor.copy(alpha = 0.15f),
                            strokeColor = primaryColor.copy(alpha = 0.5f),
                            strokeWidth = 3f
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Navigate Button
                Button(
                    onClick = {
                        val navIntentUri = Uri.parse("google.navigation:q=${sellerLat},${sellerLng}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, navIntentUri)
                        mapIntent.setPackage("com.google.android.apps.maps")
                        if (mapIntent.resolveActivity(context.packageManager) != null) {
                            context.startActivity(mapIntent)
                        } else {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${sellerLat},${sellerLng}"))
                            context.startActivity(browserIntent)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "নেভিগেশন শুরু করুন" else "Start Navigation",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Popular & Related Products Section
                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = Color(0xFFE5E7EB), thickness = 1.dp)
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = if (isBengali) "জনপ্রিয় ও সম্পর্কিত পণ্যসমূহ" else "Popular & Related Products",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                val relatedProducts = remember(product.id) {
                    dummyProducts.filter { it.id != product.id }
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(relatedProducts) { relatedItem ->
                        Card(
                            modifier = Modifier
                                .width(180.dp)
                                .clickable { onNavigateToDetails(relatedItem.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Image Thumbnail Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF3F4F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ShoppingBag,
                                        contentDescription = null,
                                        tint = primaryColor,
                                        modifier = Modifier.size(44.dp)
                                    )
                                    // Condition Badge
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(primaryColor)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = relatedItem.condition,
                                            fontSize = 10.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Product Title
                                Text(
                                    text = relatedItem.productName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                // Shop / Address
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Place,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = relatedItem.address,
                                        fontSize = 11.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Price Tag
                                Text(
                                    text = relatedItem.price,
                                    color = primaryColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showBuyNowDialog) {
        BuyNowModalDialog(
            product = product,
            onDismiss = { showBuyNowDialog = false }
        )
    }

    if (showChatDialog) {
        SellerChatDialog(
            product = product,
            onDismiss = { showChatDialog = false }
        )
    }
}

@Composable
fun BuyNowModalDialog(
    product: ProductInfo,
    onDismiss: () -> Unit
) {
    var buyerName by remember { mutableStateOf("") }
    var buyerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("COD") } // COD, bKash, Nagad
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("সরাসরি অর্ডার করুন (Instant Buy)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFFE65100))
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("পণ্য: ${product.productName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("মূল্য: ${product.price}", color = Color(0xFFE65100), fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)

                OutlinedTextField(
                    value = buyerName,
                    onValueChange = { buyerName = it },
                    label = { Text("আপনার নাম *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = buyerPhone,
                    onValueChange = { buyerPhone = it },
                    label = { Text("ফোন নম্বর *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = deliveryAddress,
                    onValueChange = { deliveryAddress = it },
                    label = { Text("ডেলিভারি ঠিকানা *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Text("পেমেন্ট পদ্ধতি নির্বাচন করুন:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("COD" to "ক্যাশ অন ডেলিভারি", "bKash" to "বিকাশ", "Nagad" to "নগদ").forEach { (key, label) ->
                        val isSelected = paymentMethod == key
                        FilterChip(
                            selected = isSelected,
                            onClick = { paymentMethod = key },
                            label = { Text(label, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFE65100),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (buyerName.isBlank() || buyerPhone.isBlank() || deliveryAddress.isBlank()) {
                        android.widget.Toast.makeText(context, "অনুগ্রহ করে সব তথ্য পূরণ করুন", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        onDismiss()
                        android.widget.Toast.makeText(context, "অর্ডার সফলভাবে সম্পূর্ণ হয়েছে! ইনভয়েস কোড: ORD-${(10000..99999).random()}", android.widget.Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Text("অর্ডার নিশ্চিত করুন", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = Color.Gray)
            }
        }
    )
}

@Composable
fun SellerChatDialog(
    product: ProductInfo,
    onDismiss: () -> Unit
) {
    var messageText by remember { mutableStateOf("") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = Color(0xFFE65100))
                Spacer(modifier = Modifier.width(8.dp))
                Text("বিক্রেতার সাথে চ্যাট (${product.shopName})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "পণ্য: ${product.productName} (${product.price})",
                        modifier = Modifier.padding(10.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }

                OutlinedTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    placeholder = { Text("আপনার প্রশ্ন বা দামের অফার লিখুন...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (messageText.isBlank()) {
                        android.widget.Toast.makeText(context, "মেসেজ লিখুন", android.widget.Toast.LENGTH_SHORT).show()
                    } else {
                        onDismiss()
                        android.widget.Toast.makeText(context, "বিক্রেতার কাছে মেসেজ পাঠানো হয়েছে!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) {
                Icon(Icons.Default.Send, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("পাঠান", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন", color = Color.Gray)
            }
        }
    )
}
