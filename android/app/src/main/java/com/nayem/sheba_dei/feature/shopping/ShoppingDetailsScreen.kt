package com.nayem.sheba_dei.feature.shopping

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
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingDetailsScreen(
    productId: String,
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()
    
    val primaryColor = Color(0xFFE65100) // Orange theme
    
    // Fetch product using productId. Mocking here.
    val product = dummyProducts.find { it.id == productId } ?: dummyProducts.first()

    val hasLocationPermission = com.nayem.sheba_dei.core.utils.rememberLocationPermissionState()

    val parts = product.latLng.split(",")
    val sellerLat = parts.getOrNull(0)?.toDoubleOrNull() ?: 23.6061
    val sellerLng = parts.getOrNull(1)?.toDoubleOrNull() ?: 89.8406
    val sellerLocation = LatLng(sellerLat, sellerLng)
    
    // Mock Buyer Location (Dhaka)
    val buyerLat = 23.8103
    val buyerLng = 90.4125
    val buyerLocation = LatLng(buyerLat, buyerLng)
    
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(sellerLocation, 10f)
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "পণ্যের বিবরণ" else "Product Details",
                onBackClick = onBack
            )
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
                
                val distanceResults = FloatArray(1)
                Location.distanceBetween(buyerLat, buyerLng, sellerLat, sellerLng, distanceResults)
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
                        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
                    ) {
                        Marker(
                            state = MarkerState(position = sellerLocation),
                            title = product.shopName,
                            snippet = if (isBengali) "বিক্রেতা" else "Seller"
                        )
                        Marker(
                            state = MarkerState(position = buyerLocation),
                            title = if (isBengali) "আপনি (মক)" else "You (Mock)"
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
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
