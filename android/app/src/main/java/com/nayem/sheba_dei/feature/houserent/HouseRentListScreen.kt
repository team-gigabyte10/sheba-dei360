package com.nayem.sheba_dei.feature.houserent

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.core.utils.bangladeshZillas
import com.nayem.sheba_dei.ui.components.CustomDialog
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

data class HouseRentInfo(
    val landlordName: String,
    val date: String,
    val houseType: String,
    val address: String,
    val latLng: String,
    val contactInfo: String,
    val rentAmount: String,
    val details: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseRentListScreen(onBack: () -> Unit) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFF00897B) // Teal theme matching the image

    val dummyHouses = listOf(
        HouseRentInfo(
            landlordName = "Sabira Priyq",
            date = "06 Jun 2026",
            houseType = if (isBengali) "ফ্লাট বাসা" else "Flat House",
            address = if (isBengali) "অম্বিকাপুর, গ্রামীন ফোনের টাওয়ারের সামনে। সদর, ফরিদপুর" else "Ambikapur, In front of GP Tower. Sadar, Faridpur",
            latLng = "23.6061,89.8406",
            contactInfo = "01711-223344",
            rentAmount = if (isBengali) "৳১০,০০০/মাস" else "৳10,000/month",
            details = if (isBengali) "৩ বেডরুম, ২ বাথরুম, ড্রয়িং ও ডাইনিং স্পেস সহ সুন্দর ফ্ল্যাট। গ্যাস ও পানির সুব্যবস্থা রয়েছে।" else "Beautiful flat with 3 bedrooms, 2 bathrooms, drawing and dining space. Gas and water available."
        ),
        HouseRentInfo(
            landlordName = "MD Shahidul Islam",
            date = "03 Jun 2026",
            houseType = if (isBengali) "ফ্লাট বাসা" else "Flat House",
            address = if (isBengali) "চুনাঘাটা ব্রীজের ওপার, ইকবালের ফার্ম এর সামনের বাসা" else "Across Chunaghata Bridge, In front of Iqbal's farm",
            latLng = "23.6012,89.8322",
            contactInfo = "01712-334455",
            rentAmount = if (isBengali) "৳৮,৫০০/মাস" else "৳8,500/month",
            details = if (isBengali) "২ বেডরুমের ছিমছাম বাসা। ছোট ফ্যামিলির জন্য উপযোগী।" else "Neat 2 bedroom house. Suitable for small family."
        )
    )

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বাসা ভাড়া" else "House Rent",
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(androidx.compose.material.icons.Icons.Default.MoreVert, contentDescription = "Filter", tint = Color.Black)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            com.nayem.sheba_dei.ui.components.CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = if (isBengali) "জেলা নির্বাচন করুন" else "Select Zilla",
                confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
                onConfirm = { showZillaFilterDialog = false }
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    item {
                        TextButton(onClick = { 
                            selectedZilla = null
                            showZillaFilterDialog = false 
                        }) {
                            Text(if (isBengali) "রিসেট" else "Reset", color = Color.Red, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                        }
                    }
                    items(com.nayem.sheba_dei.core.utils.bangladeshZillas) { zilla ->
                        TextButton(
                            onClick = { 
                                selectedZilla = zilla
                                showZillaFilterDialog = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = zilla,
                                color = if (selectedZilla == zilla) Color(0xFF1E3A8A) else Color.Black,
                                fontWeight = if (selectedZilla == zilla) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F4F6)) // Light gray background
        ) {
            // Search Bar Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text(if (isBengali) "বাসা খুঁজুন..." else "Search house...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(8.dp)),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.LightGray,
                        focusedBorderColor = primaryColor
                    ),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Count Badge
                Box(
                    modifier = Modifier
                        .background(Color(0xFFB2DFDB), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${dummyHouses.size}",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // List of Houses
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyHouses) { house ->
                    HouseRentCard(house = house, isBengali = isBengali, primaryColor = primaryColor)
                }
            }
        }
    }
}

@Composable
fun HouseRentCard(house: HouseRentInfo, isBengali: Boolean, primaryColor: Color) {
    val context = LocalContext.current
    var showDetailsDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icon representing building/owner
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE0F2F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Domain, contentDescription = null, tint = primaryColor)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = house.landlordName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Text(
                        text = house.date,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
            
            // Image Placeholder (Carousel representation)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(Color(0xFFE5E7EB)), // Light Gray
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Image, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
            }
            
            // Carousel Dots Mock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (index == 0) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(if (index == 0) Color.Red else Color.LightGray)
                    )
                }
            }
            
            // Details Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Left Column (Type)
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.House, contentDescription = null, tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(if (isBengali) "বাসার ধরণ" else "House Type", fontSize = 12.sp, color = Color.Gray)
                        Text(house.houseType, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
                
                // Right Column (Address)
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF3F4F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(if (isBengali) "ঠিকানা" else "Address", fontSize = 12.sp, color = Color.Gray)
                        Text(house.address, fontSize = 14.sp, color = Color.DarkGray)
                    }
                }
            }
            
            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:${house.contactInfo}")
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isBengali) "যোগাযোগ করুন" else "Contact", fontSize = 14.sp, color = Color.White)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Button(
                    onClick = { showDetailsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isBengali) "বিস্তারিত দেখুন" else "View Details", fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }

    if (showDetailsDialog) {
        CustomDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = if (isBengali) "বিস্তারিত তথ্য" else "Details",
            icon = Icons.Default.Info,
            confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
            onConfirm = { showDetailsDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = house.landlordName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                
                Text(
                    text = if (isBengali) "ভাড়া: ${house.rentAmount}" else "Rent: ${house.rentAmount}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(house.details, color = Color.DarkGray, fontSize = 14.sp)
                
                Divider(color = Color.LightGray, thickness = 1.dp)

                // Contact Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${house.contactInfo}")
                            }
                            context.startActivity(intent)
                        }
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Phone", tint = primaryColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBengali) "যোগাযোগঃ ${house.contactInfo}" else "Contact: ${house.contactInfo}",
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontSize = 16.sp
                    )
                }
                
                // Map Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable {
                            val gmmIntentUri = Uri.parse("geo:${house.latLng}?q=${house.latLng}(${Uri.encode(house.landlordName)})")
                            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${house.latLng}"))
                                context.startActivity(browserIntent)
                            }
                        }
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Map, contentDescription = "Map", tint = primaryColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Google Map",
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
