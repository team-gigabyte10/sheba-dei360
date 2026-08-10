package com.barisal.cityservice.feature.restaurant

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.barisal.cityservice.data.repository.RestaurantRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repository = remember { RestaurantRepository() }
    val restaurantState by repository.getRestaurantById(restaurantId).collectAsState(initial = null)

    val primaryColor = Color(0xFFC2410C)

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = restaurantState?.name ?: if (isBengali) "রেস্টুরেন্ট বিস্তারিত" else "Restaurant Details",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (restaurantState == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            val restaurant = restaurantState!!
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Cover Image
                item {
                    if (restaurant.coverImage.isNotBlank()) {
                        AsyncImage(
                            model = restaurant.coverImage,
                            contentDescription = restaurant.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
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
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = primaryColor, modifier = Modifier.size(64.dp))
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
                            Text(text = restaurant.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                            Surface(color = Color(0xFFFEF3C7), shape = RoundedCornerShape(20.dp)) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(text = restaurant.rating, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                }
                            }
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(color = primaryColor.copy(alpha = 0.1f), shape = RoundedCornerShape(6.dp)) {
                                Text(
                                    text = restaurant.cuisineType,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            if (restaurant.deliveryAvailable) {
                                Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(6.dp)) {
                                    Text(
                                        text = if (isBengali) "হোম ডেলিভারি উপলব্ধ" else "Delivery Available",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF15803D),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${restaurant.address}, ${restaurant.thana}", fontSize = 14.sp, color = Color.DarkGray)
                        }
                    }
                }

                // Call Contact Button
                item {
                    if (restaurant.contact.isNotBlank()) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${restaurant.contact}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(48.dp)
                        ) {
                            Icon(Icons.Default.Phone, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${if (isBengali) "খাবারের অর্ডার দিন (" else "Call Order ("}${restaurant.contact})", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        }
                    }
                }

                // Food Menu Section Header
                item {
                    Text(
                        text = if (isBengali) "খাবারের বিশেষ মেনু (${restaurant.menuItems.size})" else "Special Food Menu (${restaurant.menuItems.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }

                // Food Menu List
                if (restaurant.menuItems.isEmpty()) {
                    item {
                        Surface(
                            color = Color(0xFFF8FAFC),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "বর্তমানে বিশেষ কোন মেনু যুক্ত করা নেই। অর্ডারের জন্য সরাসরি কল করুন।" else "No specific food menu posted yet. Call restaurant directly to order.",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                } else {
                    items(restaurant.menuItems) { menuItem ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (menuItem.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = menuItem.imageUrl,
                                        contentDescription = menuItem.itemName,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(primaryColor.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                                    }
                                }

                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = menuItem.itemName, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    if (menuItem.description.isNotBlank()) {
                                        Text(text = menuItem.description, fontSize = 13.sp, color = Color.Gray, maxLines = 2)
                                    }
                                    if (menuItem.price.isNotBlank()) {
                                        Text(text = menuItem.price, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
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
