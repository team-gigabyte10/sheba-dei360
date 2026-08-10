package com.barisal.cityservice.feature.ride

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.RentCarDto
import com.barisal.cityservice.data.repository.RentCarRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RentCarDetailScreen(
    carId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val repository = remember { RentCarRepository() }

    var car by remember { mutableStateOf<RentCarDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val primaryColor = Color(0xFF1D4ED8) // Deep Blue theme for vehicles

    LaunchedEffect(carId) {
        car = repository.getRentCarById(carId)
        if (car == null) {
            // Preset fallback dummy lookup if id matches preset
            car = when (carId) {
                "preset_1" -> RentCarDto(
                    id = "preset_1",
                    title = if (isBengali) "টয়োটা নোয়া মাইক্রোবাস" else "Toyota Noah Microbus",
                    subCategory = if (isBengali) "মাইক্রোবাস" else "Microbus",
                    driverName = if (isBengali) "মো: রফিকুল ইসলাম" else "Md. Rofiqul Islam",
                    contact = "01711-889900",
                    price = "3500",
                    priceUnit = "per_day",
                    vehicleModel = "Toyota Noah 2018",
                    seatingCapacity = if (isBengali) "৭ জন" else "7 Persons",
                    hasAC = true,
                    address = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal"
                )
                "preset_2" -> RentCarDto(
                    id = "preset_2",
                    title = if (isBengali) "টয়োটা এক্সিও প্রাইভেট কার" else "Toyota Axio Private Car",
                    subCategory = if (isBengali) "প্রাইভেট কার" else "Private Car",
                    driverName = if (isBengali) "আব্দুর রহিম" else "Abdur Rahim",
                    contact = "01712-778899",
                    price = "2800",
                    priceUnit = "per_day",
                    vehicleModel = "Toyota Axio 2019",
                    seatingCapacity = if (isBengali) "৪ জন" else "4 Persons",
                    hasAC = true,
                    address = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal"
                )
                else -> RentCarDto(
                    id = "preset_3",
                    title = if (isBengali) "টাটা ১ টন পিকআপ" else "Tata 1 Ton Pickup",
                    subCategory = if (isBengali) "পিকআপ" else "Pickup",
                    driverName = if (isBengali) "কালাম হোসেন" else "Kalam Hossain",
                    contact = "01713-667788",
                    price = "2000",
                    priceUnit = "trip",
                    vehicleModel = "Tata Ace 2020",
                    seatingCapacity = if (isBengali) "২ জন" else "2 Persons",
                    hasAC = false,
                    address = if (isBengali) "রূপাতলী, বরিশাল" else "Rupatali, Barisal"
                )
            }
        }
        isLoading = false
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = car?.title ?: (if (isBengali) "গাড়ির বিবরণ" else "Vehicle Details"),
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            val c = car
            if (c == null) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(if (isBengali) "তথ্য পাওয়া যায়নি" else "Vehicle details not found")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFF8FAFC))
                ) {
                    // Vehicle Image Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(Color.DarkGray)
                    ) {
                        if (c.coverImage.isNotBlank()) {
                            AsyncImage(
                                model = c.coverImage,
                                contentDescription = c.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color.White, modifier = Modifier.size(72.dp))
                            }
                        }
                    }

                    // Main Vehicle Card Info
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = primaryColor.copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            text = c.subCategory,
                                            color = primaryColor,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }

                                    val unitText = when (c.priceUnit) {
                                        "per_hour" -> if (isBengali) "/ঘণ্টা" else "/hr"
                                        "trip" -> if (isBengali) "/ট্রিপ" else "/trip"
                                        else -> if (isBengali) "/দিন" else "/day"
                                    }

                                    Text(
                                        text = "৳${c.price}$unitText",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }

                                Text(
                                    text = c.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = c.address, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }

                        // Feature Badges Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = if (isBengali) "গাড়ির বৈশিষ্ট্যসমূহ" else "Vehicle Specifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.AcUnit, contentDescription = null, tint = if (c.hasAC) Color(0xFF2563EB) else Color.Gray, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = if (c.hasAC) "AC" else "Non-AC", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.AirlineSeatReclineNormal, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = c.seatingCapacity.ifBlank { "N/A" }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(text = c.vehicleModel.ifBlank { "Standard" }, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Driver / Contact Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = if (isBengali) "ড্রাইভার/মালিকের যোগাযোগ" else "Driver/Owner Information",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )

                                if (c.driverName.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = c.driverName, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = c.contact, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${c.contact}"))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(if (isBengali) "কল করুন" else "Call Now")
                                    }

                                    Button(
                                        onClick = {
                                            val url = "https://api.whatsapp.com/send?phone=${c.contact}"
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("WhatsApp")
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
