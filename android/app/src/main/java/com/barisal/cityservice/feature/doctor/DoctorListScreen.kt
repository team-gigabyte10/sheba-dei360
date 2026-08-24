package com.barisal.cityservice.feature.doctor

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.ChamberDto
import com.barisal.cityservice.data.model.DoctorDto
import com.barisal.cityservice.data.repository.DoctorRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class DoctorInfo(
    val id: String = "",
    val name: String,
    val specialization: String,
    val education: String,
    val chambers: List<ChamberDto> = emptyList(),
    val treatments: String = "",
    val latLng: String = "",
    val contactInfo: String = "",
    val zilla: String = "",
    val imageUrl: String = ""
) {
    val workplaceFormatted: String
        get() {
            if (chambers.isEmpty()) return ""
            return chambers.joinToString(separator = "\n\n") { chamber ->
                var text = chamber.chamberName
                if (chamber.address.isNotBlank()) {
                    text += "\n📍 ${chamber.address}"
                }
                if (chamber.visitingTime.isNotBlank()) {
                    text += "\n⏰ ${chamber.visitingTime}"
                }
                text
            }
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorListScreen(
    categoryName: String,
    onBack: () -> Unit,
    onNavigateToPostDoctor: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }

    val doctorRepository = remember { DoctorRepository() }
    val firestoreDoctorsState by doctorRepository.getDoctorsByCategory(categoryName)
        .collectAsState(initial = emptyList())

    val dummyDoctors = remember(isBengali) {
        listOf(
            DoctorInfo(
                id = "dummy-1",
                name = if (isBengali) "ডাঃ শশাঙ্ক নাগ (সনেট)" else "Dr. Shashanka Nag (Sonnet)",
                specialization = if (isBengali) "বিশেষজ্ঞঃ হৃদরোগ বিশেষজ্ঞ" else "Specialist: Cardiologist",
                education = if (isBengali) "শিক্ষাগত যোগ্যতাঃ ডি.এম.ইউ (ডিইউ), পি.জি.টি (মেডিসিন)" else "Education: D.M.U (DU), P.G.T (Medicine)",
                chambers = listOf(
                    ChamberDto(
                        chamberName = if (isBengali) "হার্ট ফাউন্ডেশন, ফরিদপুর" else "Heart Foundation, Faridpur",
                        address = if (isBengali) "ফরিদপুর" else "Faridpur",
                        visitingTime = if (isBengali) "প্রতিদিন: বিকাল ৪টা - রাত ৮টা" else "Everyday: 4:00 PM - 8:00 PM"
                    ),
                    ChamberDto(
                        chamberName = if (isBengali) "ডায়াবেটিকস মেডিকেল কলেজ হাসপাতাল" else "Diabetic Medical College Hospital",
                        address = if (isBengali) "ফরিদপুর" else "Faridpur",
                        visitingTime = if (isBengali) "সকাল ৯টা - দুপুর ১টা" else "9:00 AM - 1:00 PM"
                    )
                ),
                treatments = if (isBengali) "ইসিজি, ইকোকার্ডিওগ্রাফি, এনজিওগ্রাম" else "ECG, Echocardiography, Angiogram",
                latLng = "23.6061,89.8406",
                contactInfo = "01711-223344",
                zilla = if (isBengali) "ফরিদপুর" else "Faridpur"
            ),
            DoctorInfo(
                id = "dummy-2",
                name = if (isBengali) "ডাঃ মোঃ গোলাম সরোয়ার" else "Dr. Md. Golam Sarwar",
                specialization = if (isBengali) "বিশেষজ্ঞঃ হৃদরোগ বিশেষজ্ঞ" else "Specialist: Cardiologist",
                education = if (isBengali) "শিক্ষাগত যোগ্যতাঃ এম.বি.বি.এস, বি.সি.এস (স্বাস্থ্য), এফ.সি.পি.এস (মেডিসিন)" else "Education: MBBS, BCS (Health), FCPS (Medicine)",
                chambers = listOf(
                    ChamberDto(
                        chamberName = if (isBengali) "পিজি হাসপাতাল" else "PG Hospital",
                        address = if (isBengali) "শাহবাগ, ঢাকা" else "Shahbag, Dhaka",
                        visitingTime = if (isBengali) "শনি - বৃহস্পতি: সকাল ১০টা - দুপুর ২টা" else "Sat - Thu: 10:00 AM - 2:00 PM"
                    )
                ),
                treatments = if (isBengali) "হার্ট ফেইলিউর, উচ্চ রক্তচাপ, এনজাইনা" else "Heart Failure, Hypertension, Angina",
                latLng = "23.7383,90.3957",
                contactInfo = "01712-334455",
                zilla = if (isBengali) "ঢাকা" else "Dhaka"
            )
        )
    }

    val allDoctors = remember(firestoreDoctorsState, dummyDoctors) {
        if (firestoreDoctorsState.isNotEmpty()) {
            firestoreDoctorsState.map { dto ->
                DoctorInfo(
                    id = dto.id,
                    name = dto.name,
                    specialization = dto.specialization,
                    education = dto.education,
                    chambers = dto.chambers,
                    treatments = dto.treatments,
                    latLng = dto.latLng,
                    contactInfo = dto.contactInfo,
                    zilla = dto.zilla,
                    imageUrl = dto.imageUrl
                )
            }
        } else {
            dummyDoctors
        }
    }

    val filteredDoctors = remember(allDoctors, searchQuery, selectedZilla) {
        allDoctors.filter { doctor ->
            val matchesSearch = searchQuery.isBlank() ||
                    doctor.name.contains(searchQuery, ignoreCase = true) ||
                    doctor.specialization.contains(searchQuery, ignoreCase = true) ||
                    doctor.treatments.contains(searchQuery, ignoreCase = true) ||
                    doctor.education.contains(searchQuery, ignoreCase = true) ||
                    doctor.chambers.any { chamber ->
                        chamber.chamberName.contains(searchQuery, ignoreCase = true) ||
                                chamber.address.contains(searchQuery, ignoreCase = true) ||
                                chamber.visitingTime.contains(searchQuery, ignoreCase = true)
                    }

            val matchesZilla = selectedZilla == null || doctor.zilla.equals(selectedZilla, ignoreCase = true) || doctor.workplaceFormatted.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesZilla
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = categoryName,
                onBackClick = onBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter by Zilla", tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToPostDoctor(categoryName) },
                containerColor = Color(0xFF00897B),
                contentColor = Color.White,
                icon = { Icon(Icons.Default.Add, contentDescription = "Post Doctor") },
                text = { Text(if (isBengali) "ডাক্তার পোস্ট করুন" else "Post Doctor", fontWeight = FontWeight.Bold) }
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
                    items(items = bangladeshZillas) { zilla: String ->
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
                                textAlign = TextAlign.Start
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
                    placeholder = { Text(if (isBengali) "খুঁজুন (রোগের নাম, ডাক্তার, চেম্বার)" else "Search (Disease, Doctor, Chamber)") },
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
                        text = "${filteredDoctors.size}",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (filteredDoctors.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isBengali) "কোন ডাক্তার পাওয়া যায়নি" else "No doctors found",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredDoctors) { doctor ->
                        DoctorCard(doctor = doctor, isBengali = isBengali)
                    }
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

    val coilModel = remember(doctor.imageUrl) { doctor.imageUrl.toCoilModel() }

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
                // Doctor Image (Coil AsyncImage for Base64 or Fallback Icon)
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Doctor Image",
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
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
                    if (doctor.specialization.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = doctor.specialization,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                    if (doctor.education.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = doctor.education,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }

                    if (doctor.chambers.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isBengali) "কর্মস্থল ও সময়:" else "Workplace & Time:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        doctor.chambers.take(2).forEach { chamber ->
                            Text(
                                text = "• ${chamber.chamberName}${if (chamber.visitingTime.isNotBlank()) " (${chamber.visitingTime})" else ""}",
                                fontSize = 11.sp,
                                color = Color.Black
                            )
                        }
                    }
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
                                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${doctor.latLng}"))
                                context.startActivity(browserIntent)
                            }
                        } else {
                            // Fallback search map by chamber name
                            val firstChamberName = doctor.chambers.firstOrNull()?.chamberName ?: doctor.name
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/?q=${Uri.encode(firstChamberName)}"))
                            context.startActivity(browserIntent)
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
            Text(
                text = doctor.treatments.ifEmpty { if (isBengali) "কোন তথ্য নেই" else "No treatment info available" },
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDetailsDialog) {
        CustomDialog(
            onDismissRequest = { showDetailsDialog = false },
            title = if (isBengali) "ডাক্তারের বিস্তারিত তথ্য" else "Doctor Details",
            icon = Icons.Default.Info,
            confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
            usePlatformDefaultWidth = false,
            onConfirm = { showDetailsDialog = false }
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (coilModel != null) {
                        AsyncImage(
                            model = coilModel,
                            contentDescription = "Doctor Photo",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    Text(
                        text = doctor.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }

                if (doctor.specialization.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Star, contentDescription = "Specialization", tint = primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(doctor.specialization, color = Color.DarkGray, fontSize = 14.sp)
                    }
                }

                if (doctor.education.isNotBlank()) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.School, contentDescription = "Education", tint = primaryColor, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(doctor.education, color = Color.DarkGray, fontSize = 14.sp)
                    }
                }

                if (doctor.chambers.isNotEmpty()) {
                    Text(
                        text = if (isBengali) "কর্মস্থল ও চেম্বারসমূহ:" else "Workplaces & Chambers:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = primaryColor
                    )
                    doctor.chambers.forEachIndexed { index, chamber ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "${index + 1}. ${chamber.chamberName}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color.Black
                                )
                                if (chamber.address.isNotBlank()) {
                                    Text(
                                        text = "📍 ${chamber.address}",
                                        fontSize = 12.sp,
                                        color = Color.DarkGray
                                    )
                                }
                                if (chamber.visitingTime.isNotBlank()) {
                                    Text(
                                        text = "⏰ ${chamber.visitingTime}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0D9488)
                                    )
                                }
                            }
                        }
                    }
                }

                if (doctor.contactInfo.isNotBlank()) {
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
}
