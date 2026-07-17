package com.nayem.sheba_dei.feature.home

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.MoreVert
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor
import com.nayem.sheba_dei.ui.components.CustomDialog
import com.nayem.sheba_dei.core.utils.bangladeshZillas

data class DoctorInfo(
    val name: String,
    val specialization: String,
    val education: String,
    val workplace: String,
    val treatments: String = "",
    val latLng: String = "",
    val contactInfo: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(categoryName: String, onBack: () -> Unit) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }

    val dummyDoctors = listOf(
        DoctorInfo(
            name = if (isBengali) "ডাঃ শশাঙ্ক নাগ (সনেট)" else "Dr. Shashanka Nag (Sonnet)",
            specialization = if (isBengali) "বিশেষজ্ঞঃ হৃদরোগ বিশেষজ্ঞ" else "Specialist: Cardiologist",
            education = if (isBengali) "শিক্ষাগত যোগ্যতাঃ ডি.এম.ইউ (ডিইউ), পি.জি.টি (মেডিসিন)" else "Education: D.M.U (DU), P.G.T (Medicine)",
            workplace = if (isBengali) "বর্তমান কর্মস্থলঃ হার্ট ফাউন্ডেশন, ফরিদপুর\nডায়াবেটিকস মেডিকেল কলেজ হাসপাতাল, ফরিদপুর।" else "Workplace: Heart Foundation, Faridpur\nDiabetic Medical College Hospital, Faridpur.",
            treatments = if (isBengali) "ইসিজি, ইকোকার্ডিওগ্রাফি, এনজিওগ্রাম" else "ECG, Echocardiography, Angiogram",
            latLng = "23.6061,89.8406", // Faridpur lat-lng mock
            contactInfo = "01711-223344"
        ),
        DoctorInfo(
            name = if (isBengali) "ডাঃ মোঃ গোলাম সরোয়ার" else "Dr. Md. Golam Sarwar",
            specialization = if (isBengali) "বিশেষজ্ঞঃ হৃদরোগ বিশেষজ্ঞ" else "Specialist: Cardiologist",
            education = if (isBengali) "শিক্ষাগত যোগ্যতাঃ এম.বি.বি.এস, বি.সি.এস (স্বাস্থ্য), এফ.সি.পি.এস (মেডিসিন)" else "Education: MBBS, BCS (Health), FCPS (Medicine)",
            workplace = if (isBengali) "এম.ডি রেসিডেন্ট (মেডিসিন) সিসিডি (ডায়াবেটিস)\nবর্তমান কর্মস্থলঃ পিজি হাসপাতাল, শাহবাগ ঢাকা।" else "MD Resident (Medicine) CCD (Diabetes)\nWorkplace: PG Hospital, Shahbag Dhaka.",
            treatments = if (isBengali) "হার্ট ফেইলিউর, উচ্চ রক্তচাপ, এনজাইনা" else "Heart Failure, Hypertension, Angina",
            latLng = "23.7383,90.3957", // PG Hospital Shahbag lat-lng mock
            contactInfo = "01712-334455"
        )
    )

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = categoryName,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter by Zilla", tint = Color.Black)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = if (isBengali) "জেলা নির্বাচন করুন" else "Select Zilla",
                confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
                onConfirm = { showZillaFilterDialog = false }
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    item {
                        TextButton(onClick = { 
                            selectedZilla = null
                            showZillaFilterDialog = false 
                        }) {
                            Text(if (isBengali) "রিসেট" else "Reset", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(bangladeshZillas) { zilla ->
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
                                fontWeight = if (selectedZilla == zilla) FontWeight.Bold else FontWeight.Normal,
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
                .background(Color(0xFFF5F5F5))
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
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    placeholder = { Text(if (isBengali) "খুঁজুন (রোগের নাম, ডাক্তার)" else "Search (Disease, Doctor)") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = Color(0xFF00897B),
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFD0EBE5))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "17",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // List of Doctors
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(dummyDoctors) { doctor ->
                    DoctorCard(doctor = doctor, isBengali = isBengali)
                }
            }
        }
    }
}

@Composable
fun DoctorCard(doctor: DoctorInfo, isBengali: Boolean) {
    val primaryColor = Color(0xFF00897B)
    val context = LocalContext.current
    var showTreatmentsDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Doctor Image Placeholder
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Doctor Image",
                        modifier = Modifier.size(40.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Doctor Details
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = doctor.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doctor.specialization,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = doctor.education,
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = doctor.workplace,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { showTreatmentsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isBengali) "চিকিৎসা সমুহ" else "Treatments", fontSize = 12.sp, color = Color.White)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedButton(
                    onClick = { 
                        if (doctor.latLng.isNotEmpty()) {
                            val uri = Uri.parse("geo:${doctor.latLng}?q=${doctor.latLng}")
                            val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            if (mapIntent.resolveActivity(context.packageManager) != null) {
                                context.startActivity(mapIntent)
                            } else {
                                // Fallback if maps app not installed
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${doctor.latLng}"))
                                context.startActivity(browserIntent)
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, primaryColor),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isBengali) "গুগল ম্যাপ" else "Google Map", fontSize = 12.sp, color = primaryColor)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = { showDetailsDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(40.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(if (isBengali) "বিস্তারিত জানুন" else "Details", fontSize = 12.sp, color = Color.White)
                }
            }
        }
    }

    if (showTreatmentsDialog) {
        CustomDialog(
            onDismissRequest = { showTreatmentsDialog = false },
            title = if (isBengali) "চিকিৎসা সমুহ" else "Treatments",
            confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
            onConfirm = { showTreatmentsDialog = false }
        ) {
            Text(doctor.treatments, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
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
                    text = doctor.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Star, contentDescription = "Specialization", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doctor.specialization, color = Color.DarkGray, fontSize = 14.sp)
                }
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.School, contentDescription = "Education", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doctor.education, color = Color.DarkGray, fontSize = 14.sp)
                }
                
                Row(verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Workplace", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(doctor.workplace, color = Color.DarkGray, fontSize = 14.sp)
                }

                Divider(color = Color.LightGray, thickness = 1.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${doctor.contactInfo}")
                            }
                            context.startActivity(intent)
                        }
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Phone", tint = primaryColor)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (isBengali) "সিরিয়ালের জন্যঃ ${doctor.contactInfo}" else "For Serial: ${doctor.contactInfo}",
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
