package com.barisal.cityservice.feature.hotel

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.HotelDto
import com.barisal.cityservice.data.repository.HotelRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HotelDetailScreen(
    hotelId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repository = remember { HotelRepository() }
    val hotelState by repository.getHotelById(hotelId).collectAsState(initial = null)

    val primaryColor = Color(0xFF0F766E)

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = hotelState?.name ?: if (isBengali) "হোটেল বিস্তারিত" else "Hotel Details",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (hotelState == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            val hotel = hotelState!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cover Image
                item {
                    if (hotel.coverImage.isNotBlank()) {
                        AsyncImage(
                            model = hotel.coverImage,
                            contentDescription = hotel.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .clip(RoundedCornerShape(14.dp))
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(primaryColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hotel, contentDescription = null, tint = primaryColor, modifier = Modifier.size(64.dp))
                        }
                    }
                }

                // Header Info
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = hotel.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(20.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = hotel.rating, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                }
                            }
                        }

                        Surface(color = primaryColor.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                text = hotel.type,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${hotel.address}, ${hotel.thana}", fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Call Contact Button
                item {
                    if (hotel.contact.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotel.contact}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${if (isBengali) "হোটেল বুকিং কল করুন (" else "Call Hotel ("}${hotel.contact})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // Available Rooms Section Header
                item {
                    Text(
                        text = if (isBengali) "উপলব্ধ কক্ষ ও রুমসমূহ (${hotel.rooms.size})" else "Available Hotel Rooms (${hotel.rooms.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }

                // Rooms List
                if (hotel.rooms.isEmpty()) {
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "বর্তমানে বিশেষ কোন রুমের তালিকা পোস্ট করা নেই। বিস্তারিত তথ্যের জন্য ফোন করুন।" else "No specific rooms posted. Call hotel directly for room details.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(hotel.rooms) { room ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = room.roomType, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    if (room.pricePerNight.isNotBlank()) {
                                        Text(text = room.pricePerNight, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                    }
                                }

                                if (room.capacity.isNotBlank() || room.bedType.isNotBlank()) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        if (room.capacity.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.People, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = room.capacity, fontSize = 13.sp, color = Color.DarkGray)
                                            }
                                        }
                                        if (room.bedType.isNotBlank()) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Bed, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(text = room.bedType, fontSize = 13.sp, color = Color.DarkGray)
                                            }
                                        }
                                    }
                                }

                                // Amenities
                                if (room.amenities.isNotEmpty()) {
                                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                        room.amenities.forEach { amenity ->
                                            Surface(
                                                color = Color(0xFFF1F5F9),
                                                shape = RoundedCornerShape(16.dp)
                                            ) {
                                                Text(
                                                    text = amenity,
                                                    fontSize = 11.sp,
                                                    color = Color.DarkGray,
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Room Images Carousel
                                if (room.roomImages.isNotEmpty()) {
                                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        items(room.roomImages) { imgUrl ->
                                            AsyncImage(
                                                model = imgUrl,
                                                contentDescription = room.roomType,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier
                                                    .size(100.dp, 80.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                            )
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
