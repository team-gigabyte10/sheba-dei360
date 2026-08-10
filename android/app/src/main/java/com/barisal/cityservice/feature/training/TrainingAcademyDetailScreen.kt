package com.barisal.cityservice.feature.training

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
import com.barisal.cityservice.data.model.TrainingAcademyDto
import com.barisal.cityservice.data.repository.TrainingAcademyRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingAcademyDetailScreen(
    courseId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val repository = remember { TrainingAcademyRepository() }

    var course by remember { mutableStateOf<TrainingAcademyDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val primaryColor = Color(0xFF6D28D9) // Purple theme for education & academies

    LaunchedEffect(courseId) {
        course = repository.getTrainingPostById(courseId)
        if (course == null) {
            // Preset fallback dummy lookup if id matches preset
            course = when (courseId) {
                "preset_1" -> TrainingAcademyDto(
                    id = "preset_1",
                    title = if (isBengali) "প্রফেশনাল কার ড্রাইভিং কোর্স" else "Professional Car Driving Course",
                    subCategory = if (isBengali) "কার ড্রাইভিং" else "Car Driving",
                    academyName = if (isBengali) "বরিশাল ড্রাইভ ফেয়ার একাডেমি" else "Barisal Drive Fair Academy",
                    courseFee = "5000",
                    duration = if (isBengali) "১ মাস (২০টি ক্লাস)" else "1 Month (20 Classes)",
                    contact = "01711-445566",
                    address = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal"
                )
                "preset_2" -> TrainingAcademyDto(
                    id = "preset_2",
                    title = if (isBengali) "কম্পিউটার অফিস অ্যাপ্লিকেশন ও গ্রাফিক্স" else "Computer Office & Graphics",
                    subCategory = if (isBengali) "কম্পিউটার ট্রেনিং" else "Computer Training",
                    academyName = if (isBengali) "সিটি আইটি ইনস্টিটিউট" else "City IT Institute",
                    courseFee = "3500",
                    duration = if (isBengali) "৩ মাস" else "3 Months",
                    contact = "01712-556677",
                    address = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal"
                )
                else -> TrainingAcademyDto(
                    id = "preset_3",
                    title = if (isBengali) "স্পোকেন ইংলিশ ও আইইএলটিএস" else "Spoken English & IELTS",
                    subCategory = if (isBengali) "ভাষা শিক্ষা" else "Language Learning",
                    academyName = if (isBengali) "গ্লোবাল লিঙ্গুয়া একাডেমি" else "Global Lingua Academy",
                    courseFee = "4000",
                    duration = if (isBengali) "২ মাস" else "2 Months",
                    contact = "01713-667788",
                    address = if (isBengali) "বান্দ রোড, বরিশাল" else "Band Road, Barisal"
                )
            }
        }
        isLoading = false
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = course?.title ?: (if (isBengali) "কোর্সের বিবরণ" else "Course Details"),
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = primaryColor)
            }
        } else {
            val c = course
            if (c == null) {
                Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                    Text(if (isBengali) "তথ্য পাওয়া যায়নি" else "Course details not found")
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .background(Color(0xFFF8FAFC))
                ) {
                    // Course Image Header
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .background(primaryColor.copy(alpha = 0.2f))
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
                                Icon(Icons.Default.School, contentDescription = null, tint = primaryColor, modifier = Modifier.size(72.dp))
                            }
                        }
                    }

                    // Main Course Card Info
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

                                    if (c.courseFee.isNotBlank()) {
                                        Text(
                                            text = "৳${c.courseFee}",
                                            fontSize = 22.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = primaryColor
                                        )
                                    }
                                }

                                Text(
                                    text = c.title,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Text(
                                    text = c.academyName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = primaryColor
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = c.address, fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }

                        // Course Duration & Trainer Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = if (isBengali) "কোর্সের অন্যান্য বিবরণ" else "Course Summary",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )

                                if (c.duration.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Schedule, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = if (isBengali) "মেয়াদ: ${c.duration}" else "Duration: ${c.duration}", fontSize = 14.sp)
                                    }
                                }

                                if (c.trainerName.isNotBlank()) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(text = if (isBengali) "প্রশিক্ষক: ${c.trainerName}" else "Trainer: ${c.trainerName}", fontSize = 14.sp)
                                    }
                                }
                            }
                        }

                        // Contact Card
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text(
                                    text = if (isBengali) "যোগাযোগ করুন" else "Contact Information",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color(0xFF1E293B)
                                )

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(text = c.contact, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = primaryColor)
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
