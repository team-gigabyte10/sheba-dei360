package com.barisal.cityservice.feature.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.BloodDonorDto
import com.barisal.cityservice.data.model.BloodRequestDto
import com.barisal.cityservice.data.model.DoctorDto
import com.barisal.cityservice.data.model.EventProviderDto
import com.barisal.cityservice.data.model.HealthServiceDto
import com.barisal.cityservice.data.model.HouseRentDto
import com.barisal.cityservice.data.repository.BloodRepository
import com.barisal.cityservice.data.repository.DoctorRepository
import com.barisal.cityservice.data.repository.EventRepository
import com.barisal.cityservice.data.repository.HealthServiceRepository
import com.barisal.cityservice.data.repository.HouseRentRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApprovalScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()

    val doctorRepo = remember { DoctorRepository() }
    val healthRepo = remember { HealthServiceRepository() }
    val bloodRepo = remember { BloodRepository() }
    val houseRentRepo = remember { HouseRentRepository() }
    val eventRepo = remember { EventRepository() }

    val pendingDoctors by doctorRepo.getPendingDoctors().collectAsState(initial = emptyList())
    val pendingHealthServices by healthRepo.getPendingHealthServices().collectAsState(initial = emptyList())
    val pendingBloodDonors by bloodRepo.getPendingBloodDonors().collectAsState(initial = emptyList())
    val pendingBloodRequests by bloodRepo.getPendingBloodRequests().collectAsState(initial = emptyList())
    val pendingHouseRents by houseRentRepo.getPendingHouseRents().collectAsState(initial = emptyList())
    val pendingEventServices by eventRepo.getPendingEventServices().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    val tealColor = Color(0xFF0F766E)

    SetStatusBarColor()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                GlobalAppBar(
                    title = if (isBengali) "পোস্ট অনুমোদন প্যানেল" else "Post Approval Admin Panel",
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = tealColor,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = tealColor
                            )
                        }
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Text(
                                text = if (isBengali) "ডাক্তার (${pendingDoctors.size})" else "Doctors (${pendingDoctors.size})",
                                color = if (selectedTab == 0) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Text(
                                text = if (isBengali) "স্বাস্থ্য সেবা (${pendingHealthServices.size})" else "Health Services (${pendingHealthServices.size})",
                                color = if (selectedTab == 1) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Text(
                                text = if (isBengali) "রক্তদাতা (${pendingBloodDonors.size})" else "Donors (${pendingBloodDonors.size})",
                                color = if (selectedTab == 2) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = {
                            Text(
                                text = if (isBengali) "রক্তের প্রয়োজন (${pendingBloodRequests.size})" else "Blood Needs (${pendingBloodRequests.size})",
                                color = if (selectedTab == 3) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 4,
                        onClick = { selectedTab = 4 },
                        text = {
                            Text(
                                text = if (isBengali) "বাসা ভাড়া (${pendingHouseRents.size})" else "House Rent (${pendingHouseRents.size})",
                                color = if (selectedTab == 4) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 5,
                        onClick = { selectedTab = 5 },
                        text = {
                            Text(
                                text = if (isBengali) "ইভেন্ট সার্ভিস (${pendingEventServices.size})" else "Event Services (${pendingEventServices.size})",
                                color = if (selectedTab == 5) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    if (pendingDoctors.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ ডাক্তারের পোস্ট নেই" else "No pending doctor posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingDoctors) { doctor ->
                                PendingDoctorCard(
                                    doctor = doctor,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = doctorRepo.approveDoctor(doctor.categoryName, doctor.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = doctorRepo.rejectDoctor(doctor.categoryName, doctor.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    if (pendingHealthServices.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ স্বাস্থ্য সেবার পোস্ট নেই" else "No pending health service posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingHealthServices) { service ->
                                PendingHealthServiceCard(
                                    service = service,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = healthRepo.approveHealthService(service.categoryKey, service.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = healthRepo.rejectHealthService(service.categoryKey, service.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    if (pendingBloodDonors.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ রক্তদাতার পোস্ট নেই" else "No pending blood donor registrations")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingBloodDonors) { donor ->
                                PendingBloodDonorCard(
                                    donor = donor,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = bloodRepo.approveBloodDonor(donor.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = bloodRepo.rejectBloodDonor(donor.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                3 -> {
                    if (pendingBloodRequests.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ রক্তের পোস্ট নেই" else "No pending blood requests")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingBloodRequests) { req ->
                                PendingBloodRequestCard(
                                    request = req,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = bloodRepo.approveBloodRequest(req.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = bloodRepo.rejectBloodRequest(req.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                4 -> {
                    if (pendingHouseRents.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ বাসা ভাড়ার পোস্ট নেই" else "No pending house rent listings")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingHouseRents) { house ->
                                PendingHouseRentCard(
                                    house = house,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = houseRentRepo.approveHouseRent(house.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "বাসা ভাড়ার পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = houseRentRepo.rejectHouseRent(house.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                5 -> {
                    if (pendingEventServices.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ ইভেন্ট সার্ভিসের পোস্ট নেই" else "No pending event service posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingEventServices) { service ->
                                PendingEventServiceCard(
                                    eventProvider = service,
                                    isBengali = isBengali,
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = eventRepo.approveEventService(service.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "ইভেন্ট সার্ভিসের পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = eventRepo.rejectEventService(service.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি বাতিল করা হয়েছে" else "Rejected/Deleted", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyPendingBox(text: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircleOutline, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = text, color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun PendingDoctorCard(doctor: DoctorDto, isBengali: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val coilModel = remember(doctor.imageUrl) { doctor.imageUrl.toCoilModel() }
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Doctor Image",
                        modifier = Modifier.size(50.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(doctor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("${doctor.specialization} (${doctor.categoryName})", fontSize = 13.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Medium)
                    Text("📞 ${doctor.contactInfo} | 📍 ${doctor.zilla}", fontSize = 12.sp, color = Color.DarkGray)
                    val postedBy = doctor.userEmail.ifEmpty { doctor.userPhone.ifEmpty { doctor.userId } }
                    if (postedBy.isNotBlank()) {
                        Text("👤 পোস্টকারী: $postedBy", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingHealthServiceCard(service: HealthServiceDto, isBengali: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
            Text("${service.type} (${service.categoryKey})", fontSize = 13.sp, color = Color(0xFF0D9488), fontWeight = FontWeight.Medium)
            Text("📍 ${service.address}, ${service.zilla}", fontSize = 12.sp, color = Color.DarkGray)
            Text("📞 ${service.contactInfo}", fontSize = 12.sp, color = Color.DarkGray)
            val servicePostedBy = service.userEmail.ifEmpty { service.userPhone.ifEmpty { service.userId } }
            if (servicePostedBy.isNotBlank()) {
                Text("👤 পোস্টকারী: $servicePostedBy", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingBloodDonorCard(donor: BloodDonorDto, isBengali: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFFEE2E2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(donor.bloodGroup, fontWeight = FontWeight.Bold, color = Color(0xFFDC2626), fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(donor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("📞 ${donor.contactInfo} | 🏠 ${donor.thana}, ${donor.zilla}", fontSize = 12.sp, color = Color.DarkGray)
                    Text("সর্বশেষ দান: ${donor.lastDonation}", fontSize = 12.sp, color = Color.Gray)
                    val donorPostedBy = donor.userEmail.ifEmpty { donor.userPhone.ifEmpty { donor.userId } }
                    if (donorPostedBy.isNotBlank()) {
                        Text("👤 পোস্টকারী: $donorPostedBy", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingBloodRequestCard(request: BloodRequestDto, isBengali: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(Color(0xFFDC2626)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(request.bloodGroup, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("${request.patientName} (${request.bagsNeeded} ব্যাগ)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("📍 ${request.hospitalName}, ${request.zilla}", fontSize = 12.sp, color = Color.DarkGray)
                    Text("📞 ${request.contactInfo} | ⏰ ${request.requiredDate}", fontSize = 12.sp, color = Color(0xFFDC2626))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingHouseRentCard(house: HouseRentDto, isBengali: Boolean, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val firstPhoto = house.imageUrls.firstOrNull() ?: ""
                val coilModel = remember(firstPhoto) { firstPhoto.toCoilModel() }
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "House Photo",
                        modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(house.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("${house.houseType} - ${house.rentAmount}", fontSize = 13.sp, color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
                    Text("📍 ${house.address}, ${house.zilla}", fontSize = 12.sp, color = Color.DarkGray)
                    if (house.flatDetails.levelNo.isNotBlank() || house.flatDetails.flatNo.isNotBlank()) {
                        Text("🏢 ${house.flatDetails.levelNo} ${house.flatDetails.flatNo} | 🛏️ ${house.flatDetails.bedrooms}", fontSize = 11.sp, color = Color(0xFF0369A1))
                    }
                    Text("📞 ${house.contactInfo} | ⏱️ ${house.validityDays} ${if (isBengali) "দিন মেয়াদ" else "Days Validity"}", fontSize = 12.sp, color = Color.DarkGray)
                    val housePostedBy = house.userEmail.ifEmpty { house.userPhone.ifEmpty { house.userId } }
                    if (housePostedBy.isNotBlank()) {
                        Text("👤 পোস্টকারী: $housePostedBy", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PendingEventServiceCard(
    eventProvider: EventProviderDto,
    isBengali: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E3A8A).copy(alpha = 0.1f)
                ) {
                    Text(
                        text = eventProvider.categoryName.ifBlank { "ইভেন্ট সার্ভিস" },
                        color = Color(0xFF1E3A8A),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = if (eventProvider.startingPackage > 0) "${eventProvider.startingPackage} ৳ / ${eventProvider.priceUnit}" else "আলোচনা সাপেক্ষে",
                    color = Color(0xFF16A34A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = eventProvider.name.ifBlank { "Un-named Vendor" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            if (eventProvider.ownerName.isNotBlank()) {
                Text(
                    text = "মালিক/প্রতিনিধি: ${eventProvider.ownerName}",
                    fontSize = 13.sp,
                    color = Color(0xFF475569)
                )
            }

            Text(
                text = "ফোন: ${eventProvider.phone} ${if (eventProvider.whatsappPhone.isNotBlank()) "• WA: ${eventProvider.whatsappPhone}" else ""}",
                fontSize = 13.sp,
                color = Color(0xFF0284C7),
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "ঠিকানা: ${eventProvider.zilla}${if (eventProvider.thana.isNotBlank()) ", ${eventProvider.thana}" else ""}${if (eventProvider.addressBn.isNotBlank()) " (${eventProvider.addressBn})" else ""}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            if (eventProvider.categoryDetails.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                val detailsText = eventProvider.categoryDetails.entries.joinToString(" • ") { "${it.key}: ${it.value}" }
                Text(
                    text = "বিশেষত্ব: $detailsText",
                    fontSize = 12.sp,
                    color = Color(0xFF334155),
                    fontWeight = FontWeight.Medium
                )
            }

            if (eventProvider.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(eventProvider.imageUrls) { imgUrl ->
                        val coilModel = remember(imgUrl) { imgUrl.toCoilModel() }
                        if (coilModel != null) {
                            AsyncImage(
                                model = coilModel,
                                contentDescription = "Event Image",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            val postedBy = eventProvider.userEmail.ifEmpty { eventProvider.userPhone.ifEmpty { eventProvider.userId } }
            if (postedBy.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("👤 পোস্টকারী: $postedBy", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
