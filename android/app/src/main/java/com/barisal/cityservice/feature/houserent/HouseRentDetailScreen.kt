package com.barisal.cityservice.feature.houserent

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.FlatDetailsDto
import com.barisal.cityservice.data.model.HouseRentDto
import com.barisal.cityservice.data.repository.HouseRentRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseRentDetailScreen(
    houseId: String,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repository = remember { HouseRentRepository() }

    val houseDtoState by repository.getHouseRentById(houseId).collectAsState(initial = null)

    SetStatusBarColor()

    val primaryTeal = Color(0xFF0F766E)
    val textDark = Color(0xFF0F172A)
    val textMuted = Color(0xFF64748B)

    var selectedImageIndex by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বাসা ভাড়ার বিস্তারিত" else "House Rent Details",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, houseDtoState?.title ?: "House Rent")
                            putExtra(Intent.EXTRA_TEXT, "${houseDtoState?.title}\n${houseDtoState?.address}\n${houseDtoState?.rentAmount}")
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Listing"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Black)
                    }
                }
            )
        },
        bottomBar = {
            houseDtoState?.let { house ->
                Surface(
                    shadowElevation = 12.dp,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Call Owner Button
                        Button(
                            onClick = {
                                val phone = house.contactInfo.ifEmpty { house.userPhone }
                                if (phone.isNotBlank()) {
                                    val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${phone.trim()}"))
                                    context.startActivity(dialIntent)
                                } else {
                                    Toast.makeText(context, if (isBengali) "ফোন নম্বর পাওয়া যায়নি" else "Phone number unavailable", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "কল করুন" else "Call Owner",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        // SMS Owner Button
                        OutlinedButton(
                            onClick = {
                                val phone = house.contactInfo.ifEmpty { house.userPhone }
                                if (phone.isNotBlank()) {
                                    val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${phone.trim()}"))
                                    context.startActivity(smsIntent)
                                } else {
                                    Toast.makeText(context, if (isBengali) "ফোন নম্বর পাওয়া যায়নি" else "Phone number unavailable", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(primaryTeal))
                        ) {
                            Icon(Icons.Default.Sms, contentDescription = null, tint = primaryTeal, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "মেসেজ দিন" else "Send SMS",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = primaryTeal
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (houseDtoState == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryTeal)
            }
        } else {
            val house = houseDtoState!!
            val photos = house.imageUrls

            // Parse LatLng
            val houseLatLng = remember(house.latLng) {
                try {
                    val parts = house.latLng.split(",")
                    if (parts.size == 2) {
                        LatLng(parts[0].trim().toDouble(), parts[1].trim().toDouble())
                    } else {
                        LatLng(22.7010, 90.3535) // Barisal Default
                    }
                } catch (e: Exception) {
                    LatLng(22.7010, 90.3535)
                }
            }

            val cameraPositionState = rememberCameraPositionState {
                position = CameraPosition.fromLatLngZoom(houseLatLng, 15f)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // Photo Gallery / Carousel
                if (photos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color.Black)
                    ) {
                        val mainPhotoModel = remember(selectedImageIndex) { photos.getOrNull(selectedImageIndex)?.toCoilModel() }
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(mainPhotoModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = "House Main Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Photo Counter Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "${selectedImageIndex + 1} / ${photos.size}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Thumbnail Row
                    if (photos.size > 1) {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth().background(Color(0xFFF8FAFC))
                        ) {
                            itemsIndexed(photos) { idx, url ->
                                val thumbModel = remember(url) { url.toCoilModel() }
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(
                                            width = if (idx == selectedImageIndex) 2.dp else 1.dp,
                                            color = if (idx == selectedImageIndex) primaryTeal else Color.Transparent,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .clickable { selectedImageIndex = idx }
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(thumbModel)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Thumbnail",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(if (isBengali) "কোন ছবি পাওয়া যায়নি" else "No photos uploaded", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Header Title & Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = house.title.ifEmpty { if (isBengali) "বাসা ভাড়া" else "House Rent" },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = textDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = house.houseType,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryTeal
                            )
                        }

                        // Rent Status Badge
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (house.isRented) Color(0xFFFEE2E2) else Color(0xFFD1FAE5)
                        ) {
                            Text(
                                text = if (house.isRented) (if (isBengali) "ভাড়া সম্পন্ন" else "Rented Out") else (if (isBengali) "ভাড়া দেওয়া হবে" else "Available"),
                                color = if (house.isRented) Color(0xFFDC2626) else Color(0xFF059669),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rent Amount Banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (isBengali) "ভাড়ার পরিমাণ" else "Rent Amount",
                                    fontSize = 11.sp,
                                    color = Color(0xFF15803D)
                                )
                                Text(
                                    text = house.rentAmount,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF166534)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Address Card
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = house.address,
                            fontSize = 14.sp,
                            color = textDark,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Flat Details Grid / Chips
                    val flat = house.flatDetails
                    Text(
                        text = if (isBengali) "ফ্ল্যাটের বিবরণ" else "Flat Specifications",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (flat.bedrooms.isNotBlank()) DetailChip(icon = Icons.Default.Bed, label = if (isBengali) "বেডরুম: ${flat.bedrooms}" else "Bedrooms: ${flat.bedrooms}", modifier = Modifier.weight(1f))
                            if (flat.bathrooms.isNotBlank()) DetailChip(icon = Icons.Default.Bathtub, label = if (isBengali) "বাথরুম: ${flat.bathrooms}" else "Baths: ${flat.bathrooms}", modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (flat.balconies.isNotBlank()) DetailChip(icon = Icons.Default.Balcony, label = if (isBengali) "বেলকনি: ${flat.balconies}" else "Balconies: ${flat.balconies}", modifier = Modifier.weight(1f))
                            if (flat.levelNo.isNotBlank()) DetailChip(icon = Icons.Default.Stairs, label = if (isBengali) "তলা: ${flat.levelNo}" else "Floor: ${flat.levelNo}", modifier = Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (flat.houseNo.isNotBlank()) DetailChip(icon = Icons.Default.MeetingRoom, label = if (isBengali) "বাসা নং: ${flat.houseNo}" else "House No: ${flat.houseNo}", modifier = Modifier.weight(1f))
                            if (flat.flatNo.isNotBlank()) DetailChip(icon = Icons.Default.Apartment, label = if (isBengali) "ফ্ল্যাট নং: ${flat.flatNo}" else "Flat No: ${flat.flatNo}", modifier = Modifier.weight(1f))
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Detailed Description
                    if (house.details.isNotBlank()) {
                        Text(
                            text = if (isBengali) "বিস্তারিত তথ্য" else "Description & Features",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textDark
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = house.details,
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Interactive Google Map Location
                    Text(
                        text = if (isBengali) "গুগল ম্যাপে সঠিক লোকেশন" else "Exact Google Map Location",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = textDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            GoogleMap(
                                modifier = Modifier.fillMaxSize(),
                                cameraPositionState = cameraPositionState
                            ) {
                                Marker(
                                    state = MarkerState(position = houseLatLng),
                                    title = house.title.ifEmpty { "House Location" },
                                    snippet = house.address
                                )
                            }

                            // Open in Google Maps Directions Button
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("geo:${houseLatLng.latitude},${houseLatLng.longitude}?q=${houseLatLng.latitude},${houseLatLng.longitude}(${Uri.encode(house.title)})")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                                        setPackage("com.google.android.apps.maps")
                                    }
                                    if (mapIntent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(mapIntent)
                                    } else {
                                        context.startActivity(Intent(Intent.ACTION_VIEW, gmmIntentUri))
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(12.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = primaryTeal)
                            ) {
                                Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isBengali) "দিকনির্দেশনা" else "Directions", fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun DetailChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0xFFF1F5F9)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = Color(0xFF1E293B),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
