package com.barisal.cityservice.feature.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.barisal.cityservice.data.model.MistriProviderDto
import com.barisal.cityservice.data.model.ProductDto
import com.barisal.cityservice.data.model.PropertyDto
import com.barisal.cityservice.data.model.RideDriverDto
import com.barisal.cityservice.data.model.TutorDto
import com.barisal.cityservice.data.repository.BloodRepository
import com.barisal.cityservice.data.repository.DoctorRepository
import com.barisal.cityservice.data.repository.EventRepository
import com.barisal.cityservice.data.repository.HealthServiceRepository
import com.barisal.cityservice.data.repository.HouseRentRepository
import com.barisal.cityservice.data.repository.MistriRepository
import com.barisal.cityservice.data.repository.PropertyRepository
import com.barisal.cityservice.data.repository.RideRepository
import com.barisal.cityservice.data.repository.ShoppingRepository
import com.barisal.cityservice.data.repository.TutorRepository
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
    val shoppingRepo = remember { ShoppingRepository() }
    val mistriRepo = remember { MistriRepository() }
    val rideRepo = remember { RideRepository() }
    val tutorRepo = remember { TutorRepository() }
    val propertyRepo = remember { PropertyRepository() }

    val pendingDoctors by doctorRepo.getPendingDoctors().collectAsState(initial = emptyList())
    val pendingHealthServices by healthRepo.getPendingHealthServices().collectAsState(initial = emptyList())
    val pendingBloodDonors by bloodRepo.getPendingBloodDonors().collectAsState(initial = emptyList())
    val pendingBloodRequests by bloodRepo.getPendingBloodRequests().collectAsState(initial = emptyList())
    val pendingHouseRents by houseRentRepo.getPendingHouseRents().collectAsState(initial = emptyList())
    val pendingEventServices by eventRepo.getPendingEventServices().collectAsState(initial = emptyList())
    val pendingProducts by shoppingRepo.getPendingProducts().collectAsState(initial = emptyList())
    val pendingMistriProviders by mistriRepo.getPendingMistriProviders().collectAsState(initial = emptyList())
    val pendingDrivers by rideRepo.getPendingDrivers().collectAsState(initial = emptyList())
    val pendingTutorPosts by tutorRepo.getPendingTutorPosts().collectAsState(initial = emptyList())
    val pendingPropertyPosts by propertyRepo.getPendingPropertyPosts().collectAsState(initial = emptyList())

    var selectedTab by remember { mutableStateOf(0) }
    var selectedItemForDetail by remember { mutableStateOf<Any?>(null) }
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
                    Tab(
                        selected = selectedTab == 6,
                        onClick = { selectedTab = 6 },
                        text = {
                            Text(
                                text = if (isBengali) "পণ্য সমূহ (${pendingProducts.size})" else "Products (${pendingProducts.size})",
                                color = if (selectedTab == 6) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 7,
                        onClick = { selectedTab = 7 },
                        text = {
                            Text(
                                text = if (isBengali) "মিস্ত্রি সার্ভিস (${pendingMistriProviders.size})" else "Mistri Services (${pendingMistriProviders.size})",
                                color = if (selectedTab == 7) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 8,
                        onClick = { selectedTab = 8 },
                        text = {
                            Text(
                                text = if (isBengali) "ড্রাইভার (${pendingDrivers.size})" else "Drivers (${pendingDrivers.size})",
                                color = if (selectedTab == 8) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 9,
                        onClick = { selectedTab = 9 },
                        text = {
                            Text(
                                text = if (isBengali) "টিউটর (${pendingTutorPosts.size})" else "Tutors (${pendingTutorPosts.size})",
                                color = if (selectedTab == 9) tealColor else Color.Gray,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == 10,
                        onClick = { selectedTab = 10 },
                        text = {
                            Text(
                                text = if (isBengali) "জমি ও ফ্ল্যাট (${pendingPropertyPosts.size})" else "Property (${pendingPropertyPosts.size})",
                                color = if (selectedTab == 10) tealColor else Color.Gray,
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
                                    onItemClick = { selectedItemForDetail = doctor },
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
                                    onItemClick = { selectedItemForDetail = service },
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
                                    onItemClick = { selectedItemForDetail = donor },
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
                                    onItemClick = { selectedItemForDetail = req },
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
                            items(pendingHouseRents) { rent ->
                                PendingHouseRentCard(
                                    houseRent = rent,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = rent },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = houseRentRepo.approveHouseRent(rent.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "বাসা ভাড়ার পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = houseRentRepo.rejectHouseRent(rent.id)
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
                                PendingEventCard(
                                    eventProvider = service,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = service },
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
                6 -> {
                    if (pendingProducts.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ পণ্যের পোস্ট নেই" else "No pending product posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingProducts) { product ->
                                PendingProductCard(
                                    product = product,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = product },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = shoppingRepo.approveProduct(product.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পণ্য পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = shoppingRepo.rejectProduct(product.id)
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
                8 -> {
                    if (pendingDrivers.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ ড্রাইভারের আবেদন নেই" else "No pending driver applications")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingDrivers) { driver ->
                                PendingDriverCard(
                                    driver = driver,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = driver },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = rideRepo.approveDriver(driver.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "ড্রাইভার অনুমোদিত হয়েছে!" else "Driver approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = rideRepo.rejectDriver(driver.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "আবেদনটি বাতিল করা হয়েছে" else "Driver application rejected", Toast.LENGTH_SHORT).show()
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
                7 -> {
                    if (pendingMistriProviders.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ মিস্ত্রি সেবার পোস্ট নেই" else "No pending mistri service posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingMistriProviders) { mistri ->
                                PendingMistriCard(
                                    mistri = mistri,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = mistri },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = mistriRepo.approveMistriProvider(mistri.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "মিস্ত্রি সার্ভিস অনুমোদিত হয়েছে!" else "Mistri service approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = mistriRepo.rejectMistriProvider(mistri.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "মিস্ত্রি সার্ভিস বাতিল করা হয়েছে" else "Mistri service rejected", Toast.LENGTH_SHORT).show()
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
                9 -> {
                    if (pendingTutorPosts.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ টিউটর পোস্ট নেই" else "No pending tutor posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingTutorPosts) { tutor ->
                                PendingTutorCard(
                                    tutor = tutor,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = tutor },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = tutorRepo.approveTutorPost(tutor.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = tutorRepo.deleteTutorPost(tutor.id)
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
                10 -> {
                    if (pendingPropertyPosts.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ জমি ও ফ্ল্যাটের পোস্ট নেই" else "No pending property posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingPropertyPosts) { prop ->
                                PendingPropertyCard(
                                    property = prop,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = prop },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = propertyRepo.approvePropertyPost(prop.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = propertyRepo.deletePropertyPost(prop.id)
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
fun PendingDoctorCard(doctor: DoctorDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
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

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingHealthServiceCard(service: HealthServiceDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
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

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingBloodDonorCard(donor: BloodDonorDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
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

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingBloodRequestCard(request: BloodRequestDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
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

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingHouseRentCard(houseRent: HouseRentDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val firstPhoto = houseRent.imageUrls.firstOrNull() ?: ""
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
                    Text(houseRent.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("${houseRent.houseType} - ${houseRent.rentAmount}", fontSize = 13.sp, color = Color(0xFF00897B), fontWeight = FontWeight.Bold)
                    Text("📍 ${houseRent.address}, ${houseRent.zilla}", fontSize = 12.sp, color = Color.DarkGray)
                    if (houseRent.flatDetails.levelNo.isNotBlank() || houseRent.flatDetails.flatNo.isNotBlank()) {
                        Text("🏢 ${houseRent.flatDetails.levelNo} ${houseRent.flatDetails.flatNo} | 🛏️ ${houseRent.flatDetails.bedrooms}", fontSize = 11.sp, color = Color(0xFF0369A1))
                    }
                    Text("📞 ${houseRent.contactInfo} | ⏱️ ${houseRent.validityDays} ${if (isBengali) "দিন মেয়াদ" else "Days Validity"}", fontSize = 12.sp, color = Color.DarkGray)
                    val housePostedBy = houseRent.userEmail.ifEmpty { houseRent.userPhone.ifEmpty { houseRent.userId } }
                    if (housePostedBy.isNotBlank()) {
                        Text("👤 পোস্টকারী: $housePostedBy", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingEventCard(
    eventProvider: EventProviderDto,
    isBengali: Boolean,
    onItemClick: () -> Unit = {},
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
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

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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

@Composable
fun PendingProductCard(product: ProductDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(44.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFEDD5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = Color(0xFFE65100), modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(product.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
                        Surface(
                            color = Color(0xFFE0F2FE),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = product.condition,
                                color = Color(0xFF0369A1),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text("💰 ৳${product.price} | 🏷️ ${product.category}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE65100))
                    if (product.address.isNotBlank()) {
                        Text("📍 ${product.address}", fontSize = 12.sp, color = Color.DarkGray)
                    }
                    Text("📞 ${product.contactInfo}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (product.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("📝 ${product.description}", fontSize = 12.sp, color = Color.DarkGray, maxLines = 2)
            }

            if (product.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(product.imageUrls) { imgUrl ->
                        val coilModel = remember(imgUrl) { imgUrl.toCoilModel() }
                        if (coilModel != null) {
                            AsyncImage(
                                model = coilModel,
                                contentDescription = "Product Image",
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }

            val postedBy = product.userEmail.ifEmpty { product.userPhone.ifEmpty { product.userId } }
            if (postedBy.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("👤 পোস্টকারী: $postedBy", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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

@Composable
fun PendingMistriCard(mistri: MistriProviderDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFDBEAFE)),
                    contentAlignment = Alignment.Center
                ) {
                    val coilModel = remember(mistri.profileImageUrl) { mistri.profileImageUrl.toCoilModel() }
                    if (coilModel != null) {
                        AsyncImage(
                            model = coilModel,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Construction, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(mistri.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black, modifier = Modifier.weight(1f))
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = mistri.categoryName,
                                color = Color(0xFF1D4ED8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    val chargeText = if (mistri.minCharge > 0) " | 💰 ৳${mistri.minCharge} (${mistri.pricingUnit})" else ""
                    val expText = if (mistri.experienceYears > 0) "⭐ ${mistri.experienceYears} বছর অভিজ্ঞতা" else "⭐ মিস্ত্রি সার্ভিস"
                    Text("$expText$chargeText", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF2563EB))
                    if (mistri.addressBn.isNotBlank()) {
                        Text("📍 ${mistri.addressBn} (${mistri.zilla})", fontSize = 12.sp, color = Color.DarkGray)
                    }
                    Text("📞 ${mistri.phone}" + if (mistri.whatsapp.isNotBlank()) " | 💬 WA: ${mistri.whatsapp}" else "", fontSize = 12.sp, color = Color.Gray)
                }
            }

            if (mistri.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text("📝 ${mistri.description}", fontSize = 12.sp, color = Color.DarkGray, maxLines = 3)
            }

            if (mistri.categorySpecificFields.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                val detailsStr = mistri.categorySpecificFields.entries.joinToString(" • ") { "${it.key}: ${it.value}" }
                Text("⚙️ $detailsStr", fontSize = 11.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.Medium)
            }

            val postedBy = mistri.userEmail.ifEmpty { mistri.userPhone.ifEmpty { mistri.userId } }
            if (postedBy.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("👤 পোস্টকারী: $postedBy", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun PendingDriverCard(driver: RideDriverDto, isBengali: Boolean, onItemClick: () -> Unit = {}, onApprove: () -> Unit, onReject: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(54.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFEF3C7)),
                    contentAlignment = Alignment.Center
                ) {
                    val coilModel = remember(driver.profileImageUrl) { driver.profileImageUrl.toCoilModel() }
                    if (coilModel != null) {
                        AsyncImage(
                            model = coilModel,
                            contentDescription = "Profile Photo",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.DriveEta, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(driver.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text("🚗 ${driver.vehicleType.uppercase()} | ${driver.vehicleModel}", fontSize = 13.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                    Text("🔢 প্লেট: ${driver.plateNumber} | 🆔 লাইসেন্স: ${driver.licenseNumber}", fontSize = 12.sp, color = Color.DarkGray)
                    Text("📞 ${driver.phone}", fontSize = 12.sp, color = Color.Gray)
                }
            }

            val postedBy = driver.userEmail.ifEmpty { driver.userPhone.ifEmpty { driver.userId } }
            if (postedBy.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text("👤 পোস্টকারী: $postedBy", fontSize = 11.sp, color = Color(0xFF64748B))
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text("👆 বিস্তারিত দেখতে ট্যাপ করুন", fontSize = 11.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Bold)

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
fun AdminPostDetailModal(
    item: Any,
    isBengali: Boolean,
    onDismiss: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isBengali) "পোস্টের বিস্তারিত তথ্য" else "Post Details",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F766E)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when (item) {
                    is MistriProviderDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        Surface(color = Color(0xFFDBEAFE), shape = RoundedCornerShape(6.dp)) {
                            Text(item.categoryName, color = Color(0xFF1D4ED8), fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        
                        val profileModel = remember(item.profileImageUrl) { item.profileImageUrl.toCoilModel() }
                        if (profileModel != null) {
                            AsyncImage(
                                model = profileModel,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }

                        DetailRow(if (isBengali) "অভিজ্ঞতা" else "Experience", if (item.experienceYears > 0) "${item.experienceYears} বছর" else "উল্লেখ নেই")
                        DetailRow(if (isBengali) "চার্জ / ফি" else "Visiting Fee", if (item.minCharge > 0) "৳${item.minCharge} (${item.pricingUnit})" else "আলোচনা সাপেক্ষে")
                        DetailRow(if (isBengali) "মোবাইল" else "Phone", item.phone)
                        if (item.whatsapp.isNotBlank()) DetailRow("WhatsApp", item.whatsapp)
                        DetailRow(if (isBengali) "জেলা" else "Zilla", item.zilla)
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", item.addressBn)
                        if (item.latLng.isNotBlank()) DetailRow(if (isBengali) "জিপিএস স্থানাঙ্ক" else "GPS LatLng", item.latLng)
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিস্তারিত বিবরণ" else "Description", item.description)

                        if (item.categorySpecificFields.isNotEmpty()) {
                            HorizontalDivider(color = Color(0xFFE2E8F0), modifier = Modifier.padding(vertical = 4.dp))
                            Text(if (isBengali) "ক্যাটাগরি অনুযায়ী বিশেষ তথ্য:" else "Category Specific Details:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F766E))
                            item.categorySpecificFields.forEach { (key, value) ->
                                DetailRow(key, value)
                            }
                        }

                        val postedBy = item.userEmail.ifEmpty { item.userPhone.ifEmpty { item.userId } }
                        if (postedBy.isNotBlank()) DetailRow(if (isBengali) "পোস্টকারী" else "Posted By", postedBy)
                    }

                    is ProductDto -> {
                        Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "মূল্য" else "Price", "৳${item.price}")
                        DetailRow(if (isBengali) "ক্যাটাগরি" else "Category", item.category)
                        DetailRow(if (isBengali) "অবস্থা" else "Condition", item.condition)
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", item.address)
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Description", item.description)

                        if (item.imageUrls.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(item.imageUrls) { img ->
                                    val coilModel = remember(img) { img.toCoilModel() }
                                    if (coilModel != null) {
                                        AsyncImage(model = coilModel, contentDescription = null, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                        }
                    }

                    is HouseRentDto -> {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "ভাড়া" else "Rent", "৳${item.rentAmount}")
                        DetailRow(if (isBengali) "ধরনের" else "Type", item.houseType)
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.address}, ${item.thana}, ${item.zilla}")
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        if (item.flatDetails.bedrooms.isNotBlank()) DetailRow("বেডরুম", item.flatDetails.bedrooms)
                        if (item.flatDetails.bathrooms.isNotBlank()) DetailRow("বাথরুম", item.flatDetails.bathrooms)
                        if (item.flatDetails.balconies.isNotBlank()) DetailRow("বারান্দা", item.flatDetails.balconies)
                        if (item.flatDetails.levelNo.isNotBlank()) DetailRow("তলা/লেভেল", item.flatDetails.levelNo)
                        if (item.details.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Details", item.details)

                        if (item.imageUrls.isNotEmpty()) {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(item.imageUrls) { img ->
                                    val coilModel = remember(img) { img.toCoilModel() }
                                    if (coilModel != null) {
                                        AsyncImage(model = coilModel, contentDescription = null, modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                        }
                    }

                    is DoctorDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "বিশেষজ্ঞতা" else "Specialty", "${item.specialization} (${item.categoryName})")
                        if (item.education.isNotBlank()) DetailRow(if (isBengali) "ডিগ্রি/শিক্ষা" else "Education", item.education)
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        if (item.zilla.isNotBlank()) DetailRow(if (isBengali) "জেলা" else "Zilla", item.zilla)
                        if (item.treatments.isNotBlank()) DetailRow(if (isBengali) "চিকিৎসা সমূহ" else "Treatments", item.treatments)
                        if (item.chambers.isNotEmpty()) {
                            item.chambers.forEach { chamber ->
                                if (chamber.chamberName.isNotBlank()) {
                                    DetailRow(if (isBengali) "চেম্বার" else "Chamber", "${chamber.chamberName} - ${chamber.address} (${chamber.visitingTime})")
                                }
                            }
                        }
                    }

                    is HealthServiceDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "ধরন" else "Type", "${item.type} (${item.categoryKey})")
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.address}, ${item.zilla}")
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        if (item.details.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Details", item.details)
                    }

                    is BloodDonorDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "রক্তের গ্রুপ" else "Blood Group", item.bloodGroup)
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        DetailRow(if (isBengali) "জেলা" else "Zilla", item.zilla)
                        if (item.address.isNotBlank() || item.thana.isNotBlank()) DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.address} ${item.thana}".trim())
                        DetailRow(if (isBengali) "সর্বশেষ রক্তদান" else "Last Donation", item.lastDonation.ifEmpty { "কখনো দেননি / জানা নেই" })
                        if (item.details.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Details", item.details)
                    }

                    is BloodRequestDto -> {
                        Text(item.patientName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "রক্তের গ্রুপ" else "Blood Group", item.bloodGroup)
                        DetailRow(if (isBengali) "ব্যাগের সংখ্যা" else "Bags Needed", "${item.bagsNeeded} ব্যাগ")
                        if (item.requiredDate.isNotBlank()) DetailRow(if (isBengali) "প্রয়োজনের তারিখ" else "Required Date", item.requiredDate)
                        DetailRow(if (isBengali) "হাসপাতাল" else "Hospital", item.hospitalName)
                        if (item.zilla.isNotBlank() || item.thana.isNotBlank()) DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.thana}, ${item.zilla}")
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", item.contactInfo)
                        if (item.details.isNotBlank()) DetailRow(if (isBengali) "বিবরণ/সমস্যা" else "Details", item.details)
                    }

                    is EventProviderDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "সার্ভিস ক্যাটাগরি" else "Category", item.categoryName)
                        DetailRow(if (isBengali) "শুরুর প্যাকেজ" else "Starting Package", "৳${item.startingPackage} / ${item.priceUnit}")
                        DetailRow(if (isBengali) "যোগাযোগ" else "Contact", "${item.phone} ${if (item.whatsappPhone.isNotBlank()) "• WA: ${item.whatsappPhone}" else ""}")
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.addressBn}, ${item.zilla}")
                        if (item.details.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Details", item.details)
                    }

                    is RideDriverDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "যানবাহনের ধরন" else "Vehicle Type", item.vehicleType.uppercase())
                        DetailRow(if (isBengali) "যানবাহনের মডেল" else "Vehicle Model", item.vehicleModel)
                        DetailRow(if (isBengali) "প্লেট নম্বর" else "Plate Number", item.plateNumber)
                        if (item.licenseNumber.isNotBlank()) DetailRow(if (isBengali) "লাইসেন্স নম্বর" else "License Number", item.licenseNumber)
                        DetailRow(if (isBengali) "মোবাইল" else "Phone", item.phone)
                        val postedBy = item.userEmail.ifEmpty { item.userPhone.ifEmpty { item.userId } }
                        if (postedBy.isNotBlank()) DetailRow(if (isBengali) "ইউজার আইডি/ইমেইল" else "User Identity", postedBy)
                    }

                    is TutorDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "পোস্টের ধরন" else "Post Type", if (item.postType == "tutor") "পড়াতে চাই (Tutor)" else "শিক্ষক চাই (Tuition Wanted)")
                        DetailRow(if (isBengali) "বিষয়" else "Subject", item.subject)
                        DetailRow(if (isBengali) "শ্রেণী" else "Class Range", item.classRange)
                        DetailRow(if (isBengali) "দিন/সপ্তাহে" else "Days/Week", item.daysPerWeek)
                        DetailRow(if (isBengali) "বেতন" else "Salary", item.salary)
                        DetailRow(if (isBengali) "ঠিকানা" else "Address", "${item.address}, ${item.thana}".trim(',', ' '))
                        DetailRow(if (isBengali) "মোবাইল নম্বর" else "Contact Phone", item.phone)
                        if (item.bio.isNotBlank()) DetailRow(if (isBengali) "যোগ্যতা / বিবরণ" else "Qualification / Bio", item.bio)
                    }

                    is PropertyDto -> {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "প্রপার্টির ধরন" else "Property Type", if (item.propertyType == "flat") "ফ্ল্যাট বিক্রি (Flat)" else "জমি বিক্রি (Land)")
                        DetailRow(if (isBengali) "মূল্য" else "Price", item.price)
                        DetailRow(if (isBengali) "অবস্থান" else "Location", "${item.location}, ${item.thana}".trim(',', ' '))
                        DetailRow(if (isBengali) "মোবাইল নম্বর" else "Contact Phone", item.phone)
                        if (item.propertyType == "flat") {
                            if (item.roomSize.isNotBlank()) DetailRow(if (isBengali) "আয়তন" else "Size", item.roomSize)
                            if (item.floor.isNotBlank()) DetailRow(if (isBengali) "তলা" else "Floor", item.floor)
                            DetailRow(if (isBengali) "রুম" else "Rooms", "${item.beds} Bed, ${item.baths} Bath, ${item.balcony} Balcony")
                        } else {
                            if (item.area.isNotBlank()) DetailRow(if (isBengali) "জমির পরিমাণ" else "Area", item.area)
                            if (item.roadWidth.isNotBlank()) DetailRow(if (isBengali) "সংলগ্ন রাস্তা" else "Road Width", item.roadWidth)
                            if (item.landType.isNotBlank()) DetailRow(if (isBengali) "জমির ধরন" else "Land Type", item.landType)
                            if (item.registrationStatus.isNotBlank()) DetailRow(if (isBengali) "কাগজপত্র" else "Paper Status", item.registrationStatus)
                        }
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Description", item.description)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onApprove()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
            ) {
                Text(if (isBengali) "অনুমোদন করুন" else "Approve", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = {
                        onReject()
                        onDismiss()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626))
                ) {
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontWeight = FontWeight.Bold)
                }
                TextButton(onClick = onDismiss) {
                    Text(if (isBengali) "বন্ধ করুন" else "Close", color = Color.Gray)
                }
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium, modifier = Modifier.weight(0.4f))
        Text(text = value, fontSize = 13.sp, color = Color.Black, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.6f))
    }
}

@Composable
fun PendingTutorCard(
    tutor: TutorDto,
    isBengali: Boolean,
    onItemClick: () -> Unit = {},
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val profileModel = remember(tutor.profileImageUrl) { tutor.profileImageUrl.toCoilModel() }
                if (profileModel != null) {
                    AsyncImage(
                        model = profileModel,
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0F766E).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = tutor.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(
                        text = if (tutor.postType == "tutor") (if (isBengali) "পড়াতে চাই (${tutor.subject})" else "Tutor Available (${tutor.subject})") else (if (isBengali) "শিক্ষক চাই (${tutor.subject})" else "Tuition Wanted (${tutor.subject})"),
                        fontSize = 13.sp,
                        color = Color(0xFF0F766E),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (tutor.bio.isNotBlank()) {
                Text(text = tutor.bio, fontSize = 12.sp, color = Color.DarkGray, maxLines = 2)
                Spacer(modifier = Modifier.height(4.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "${if (isBengali) "শ্রেণী:" else "Class:"} ${tutor.classRange}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "${if (isBengali) "বেতন:" else "Salary:"} ${tutor.salary}", fontSize = 12.sp, color = Color.Gray)
            }
            if (tutor.phone.isNotBlank()) {
                Text(text = "${if (isBengali) "ফোন:" else "Phone:"} ${tutor.phone}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun PendingPropertyCard(
    property: PropertyDto,
    isBengali: Boolean,
    onItemClick: () -> Unit = {},
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onItemClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val profileModel = remember(property.images.firstOrNull()) { property.images.firstOrNull()?.toCoilModel() }
                if (profileModel != null) {
                    AsyncImage(
                        model = profileModel,
                        contentDescription = "Property Image",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF334155).copy(alpha = 0.15f),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Home, contentDescription = null, tint = Color(0xFF334155), modifier = Modifier.size(24.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = property.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(
                        text = if (property.propertyType == "flat") (if (isBengali) "🏢 ফ্ল্যাট বিক্রি" else "🏢 Flat Sale") else (if (isBengali) "🏞️ জমি বিক্রি" else "🏞️ Land Sale"),
                        fontSize = 13.sp,
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "${if (isBengali) "মূল্য:" else "Price:"} ${property.price}", fontSize = 12.sp, color = Color.Gray)
                Text(text = "${if (isBengali) "ঠিকানা:" else "Location:"} ${property.location}", fontSize = 12.sp, color = Color.Gray)
            }
            if (property.phone.isNotBlank()) {
                Text(text = "${if (isBengali) "ফোন:" else "Phone:"} ${property.phone}", fontSize = 12.sp, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "অনুমোদন করুন" else "Approve", fontSize = 12.sp)
                }
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFDC2626)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(38.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "বাতিল করুন" else "Reject", fontSize = 12.sp)
                }
            }
        }
    }
}
