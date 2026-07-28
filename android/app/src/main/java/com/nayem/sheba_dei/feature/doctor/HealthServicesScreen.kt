package com.nayem.sheba_dei.feature.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

data class HealthCategoryItem(
    val id: String,
    val titleBan: String,
    val titleEng: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthServicesScreen(
    onBack: () -> Unit,
    onNavigateToDoctor: () -> Unit,
    onNavigateToHospital: () -> Unit,
    onNavigateToBloodDonor: () -> Unit,
    onNavigateToCategoryMap: (String) -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor()

    val healthSubCategories = listOf(
        HealthCategoryItem("doctor", "ডাক্তার", "Doctor", Icons.Default.MedicalServices, Color(0xFF10B981)),
        HealthCategoryItem("hospital", "হাসপাতাল", "Hospital", Icons.Default.LocalHospital, Color(0xFFEC4899)),
        HealthCategoryItem("diagnostic", "ডায়াগনস্টিক সেন্টার", "Diagnostic Center", Icons.Default.Biotech, Color(0xFF8B5CF6)),
        HealthCategoryItem("ambulance", "অ্যাম্বুলেন্স", "Ambulance", Icons.Default.AirportShuttle, Color(0xFFEF4444)),
        HealthCategoryItem("pharmacy", "ফার্মেসি", "Pharmacy", Icons.Default.Medication, Color(0xFF3B82F6)),
        HealthCategoryItem("blood", "ব্লাড ডোনার", "Blood Donor", Icons.Default.Bloodtype, Color(0xFFDC2626)),
        HealthCategoryItem("home_care", "হোম কেয়ার", "Home Care", Icons.Default.HomeWork, Color(0xFFF59E0B))
    )

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "স্বাস্থ্য সেবা" else "Healthcare Services",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // 1. Auto Slider Image Banner
            com.nayem.sheba_dei.feature.home.AutoSliderBanner()

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Health Special Banner Ads Carousel
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .height(125.dp)
                            .clickable { onNavigateToDoctor() },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D9488))
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Box(modifier = Modifier.size(100.dp).offset(x = 160.dp, y = (-20).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)))
                            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                Text(if (isBengali) "ফ্রি হেলথ ক্যাম্প!" else "Free Health Camp!", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(if (isBengali) "অভিজ্ঞ ডাক্তারের পরামর্শ ফ্রি" else "Free Expert Doctor Consultation", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 14.dp, vertical = 5.dp)
                                ) {
                                    Text(if (isBengali) "বুকিং করুন" else "Book Now", color = Color(0xFF0D9488), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                item {
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .height(125.dp)
                            .clickable { onNavigateToCategoryMap("ambulance") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFDC2626))
                    ) {
                        Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            Box(modifier = Modifier.size(90.dp).offset(x = 170.dp, y = 30.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)))
                            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                Text(if (isBengali) "জরুরি অ্যাম্বুলেন্স ২০% ছাড়!" else "20% Off Emergency Ambulance!", color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Text(if (isBengali) "২৪/৭ তাৎক্ষণিক সেবা" else "24/7 Instant Service", color = Color(0xFFFFC107), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                Spacer(modifier = Modifier.height(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White)
                                        .padding(horizontal = 14.dp, vertical = 5.dp)
                                ) {
                                    Text(if (isBengali) "কল করুন" else "Call Now", color = Color(0xFFDC2626), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (isBengali) "সকল স্বাস্থ্য সেবা সাব-ক্যাটাগরি" else "All Healthcare Sub-Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(healthSubCategories) { cat ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                when (cat.id) {
                                    "doctor" -> onNavigateToDoctor()
                                    "hospital" -> onNavigateToHospital()
                                    "blood" -> onNavigateToBloodDonor()
                                    else -> onNavigateToCategoryMap(cat.id)
                                }
                            },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(cat.color.copy(alpha = 0.12f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = cat.icon,
                                    contentDescription = null,
                                    tint = cat.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isBengali) cat.titleBan else cat.titleEng,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
