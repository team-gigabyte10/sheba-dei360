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
import com.barisal.cityservice.data.repository.JobRepository
import com.barisal.cityservice.data.repository.DomesticHelpRepository
import com.barisal.cityservice.data.repository.HotelRepository
import com.barisal.cityservice.data.repository.RestaurantRepository
import com.barisal.cityservice.data.repository.RentCarRepository
import com.barisal.cityservice.data.repository.TrainingAcademyRepository
import com.barisal.cityservice.data.repository.LegalServiceRepository
import com.barisal.cityservice.data.repository.HajjTourRepository
import com.barisal.cityservice.data.repository.MoneyExchangeRepository
import com.barisal.cityservice.data.repository.MissingFoundRepository
import com.barisal.cityservice.data.repository.NoticeRepository
import com.barisal.cityservice.data.model.JobDto
import com.barisal.cityservice.data.model.DomesticHelpDto
import com.barisal.cityservice.data.model.HotelDto
import com.barisal.cityservice.data.model.RestaurantDto
import com.barisal.cityservice.data.model.RentCarDto
import com.barisal.cityservice.data.model.TrainingAcademyDto
import com.barisal.cityservice.data.model.LegalServiceDto
import com.barisal.cityservice.data.model.HajjTourDto
import com.barisal.cityservice.data.model.MoneyExchangeDto
import com.barisal.cityservice.data.model.MissingFoundDto
import com.barisal.cityservice.data.model.NoticeDto
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
data class AdminCategoryApprovalItem(
    val key: String,
    val titleBn: String,
    val titleEn: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val pendingCount: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApprovalScreen(
    initialCategoryKey: String? = null,
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
    val hotelRepo = remember { HotelRepository() }
    val restaurantRepo = remember { RestaurantRepository() }
    val rentCarRepo = remember { RentCarRepository() }
    val trainingRepo = remember { TrainingAcademyRepository() }
    val jobRepo = remember { JobRepository() }
    val domesticHelpRepo = remember { DomesticHelpRepository() }
    val legalRepo = remember { LegalServiceRepository() }
    val hajjTourRepo = remember { HajjTourRepository() }
    val moneyExchangeRepo = remember { MoneyExchangeRepository() }
    val missingFoundRepo = remember { MissingFoundRepository() }
    val noticeRepo = remember { NoticeRepository() }

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
    val pendingHotels by hotelRepo.getPendingHotels().collectAsState(initial = emptyList())
    val pendingRestaurants by restaurantRepo.getPendingRestaurants().collectAsState(initial = emptyList())
    val pendingRentCars by rentCarRepo.getPendingRentCars().collectAsState(initial = emptyList())
    val pendingTrainingPosts by trainingRepo.getPendingTrainingPosts().collectAsState(initial = emptyList())
    val pendingJobs by jobRepo.getPendingJobs().collectAsState(initial = emptyList())
    val pendingDomesticHelps by domesticHelpRepo.getPendingDomesticHelps().collectAsState(initial = emptyList())
    val pendingLegalServices by legalRepo.getPendingLegalServices().collectAsState(initial = emptyList())
    val pendingHajjTourPosts by hajjTourRepo.getPendingHajjTourPosts().collectAsState(initial = emptyList())
    val pendingMoneyExchanges by moneyExchangeRepo.getPendingMoneyExchangePosts().collectAsState(initial = emptyList())
    val pendingMissingFounds by missingFoundRepo.getPendingMissingFoundPosts().collectAsState(initial = emptyList())
    val allNotices by noticeRepo.getAllNotices().collectAsState(initial = emptyList())

    var selectedCategoryKey by remember { mutableStateOf<String?>(initialCategoryKey) }
    var selectedItemForDetail by remember { mutableStateOf<Any?>(null) }

    var showNewNoticeDialog by remember { mutableStateOf(false) }
    var noticeTitleInput by remember { mutableStateOf("") }
    var noticeContentInput by remember { mutableStateOf("") }
    var noticeDateInput by remember { mutableStateOf("") }
    var isNoticeUrgent by remember { mutableStateOf(false) }
    var isSubmittingNotice by remember { mutableStateOf(false) }

    val tealColor = Color(0xFF0F766E)

    val categoryList = remember(
        pendingDoctors, pendingHealthServices, pendingBloodDonors, pendingBloodRequests,
        pendingHouseRents, pendingEventServices, pendingProducts, pendingMistriProviders,
        pendingDrivers, pendingTutorPosts, pendingPropertyPosts, pendingHotels,
        pendingRestaurants, pendingRentCars, pendingTrainingPosts, pendingJobs, pendingDomesticHelps,
        pendingLegalServices, pendingHajjTourPosts, pendingMoneyExchanges, pendingMissingFounds, allNotices
    ) {
        listOf(
            AdminCategoryApprovalItem("doctor", "ডাক্তার", "Doctors", Icons.Default.MedicalServices, pendingDoctors.size, Color(0xFF0F766E)),
            AdminCategoryApprovalItem("health", "স্বাস্থ্য সেবা", "Health Services", Icons.Default.LocalHospital, pendingHealthServices.size, Color(0xFF0284C7)),
            AdminCategoryApprovalItem("donor", "রক্তদাতা", "Donors", Icons.Default.Bloodtype, pendingBloodDonors.size, Color(0xFFDC2626)),
            AdminCategoryApprovalItem("blood", "রক্তের প্রয়োজন", "Blood Requests", Icons.Default.Emergency, pendingBloodRequests.size, Color(0xFFB91C1C)),
            AdminCategoryApprovalItem("houserent", "বাসা ভাড়া", "House Rent", Icons.Default.House, pendingHouseRents.size, Color(0xFFD97706)),
            AdminCategoryApprovalItem("event", "ইভেন্ট সার্ভিস", "Event Services", Icons.Default.Event, pendingEventServices.size, Color(0xFF9333EA)),
            AdminCategoryApprovalItem("shopping", "পণ্য সমূহ", "Products", Icons.Default.ShoppingCart, pendingProducts.size, Color(0xFFEA580C)),
            AdminCategoryApprovalItem("mistri", "মিস্ত্রি সার্ভিস", "Mistri Services", Icons.Default.Construction, pendingMistriProviders.size, Color(0xFF475569)),
            AdminCategoryApprovalItem("driver", "রাইড/ড্রাইভার", "Drivers", Icons.Default.TwoWheeler, pendingDrivers.size, Color(0xFF2563EB)),
            AdminCategoryApprovalItem("tutor", "টিউটর", "Tutors", Icons.Default.School, pendingTutorPosts.size, Color(0xFF059669)),
            AdminCategoryApprovalItem("property", "জমি ও ফ্ল্যাট", "Property", Icons.Default.Landscape, pendingPropertyPosts.size, Color(0xFF65A30D)),
            AdminCategoryApprovalItem("hotel", "হোটেল", "Hotels", Icons.Default.Hotel, pendingHotels.size, Color(0xFF0891B2)),
            AdminCategoryApprovalItem("restaurant", "রেস্টুরেন্ট", "Restaurants", Icons.Default.Restaurant, pendingRestaurants.size, Color(0xFFD97706)),
            AdminCategoryApprovalItem("rentcar", "গাড়ি ভাড়া", "Rent a Car", Icons.Default.DirectionsCar, pendingRentCars.size, Color(0xFF1D4ED8)),
            AdminCategoryApprovalItem("training", "ট্রেনিং একাডেমি", "Training Academy", Icons.Default.School, pendingTrainingPosts.size, Color(0xFF6D28D9)),
            AdminCategoryApprovalItem("job", "চাকরি ও নিয়োগ", "Jobs Circular", Icons.Default.Work, pendingJobs.size, Color(0xFF0F766E)),
            AdminCategoryApprovalItem("domestic_help", "গৃহকর্মী ও বুয়া", "Domestic Help", Icons.Default.CleaningServices, pendingDomesticHelps.size, Color(0xFFD97706)),
            AdminCategoryApprovalItem("legal", "আইনি সেবা", "Legal Services", Icons.Default.Gavel, pendingLegalServices.filter { it.categoryKey == "legal" }.size, Color(0xFF1E293B)),
            AdminCategoryApprovalItem("deed_amin", "দলিল লেখক/আমিন", "Deed Writer & Amin", Icons.Default.Assignment, pendingLegalServices.filter { it.categoryKey == "deed_amin" }.size, Color(0xFFD97706)),
            AdminCategoryApprovalItem("hajj", "হজ ও উমরাহ সেবা", "Hajj & Umrah", Icons.Default.Mosque, pendingHajjTourPosts.filter { it.categoryKey == "hajj" }.size, Color(0xFF047857)),
            AdminCategoryApprovalItem("tour", "ট্যুর ও ট্রাভেলস", "Tour & Travels", Icons.Default.FlightTakeoff, pendingHajjTourPosts.filter { it.categoryKey == "tour" }.size, Color(0xFF0284C7)),
            AdminCategoryApprovalItem("money_exchange", "মানি এক্সচেঞ্জ", "Money Exchange", Icons.Default.CurrencyExchange, pendingMoneyExchanges.size, Color(0xFF0F766E)),
            AdminCategoryApprovalItem("missing_found", "নিখোজ বিজ্ঞপ্তি", "Missing & Found", Icons.Default.Search, pendingMissingFounds.size, Color(0xFFDC2626)),
            AdminCategoryApprovalItem("admin_notice", "নোটিশ বোর্ড", "Notice Board", Icons.Default.Campaign, allNotices.size, Color(0xFFDC2626))
        )
    }

    val totalPendingCount = categoryList.sumOf { it.pendingCount }
    val currentCategoryItem = categoryList.find { it.key == selectedCategoryKey }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .statusBarsPadding()
            ) {
                GlobalAppBar(
                    title = if (selectedCategoryKey == null) {
                        if (isBengali) "ক্যাটাগরি ভিত্তিক পোস্ট অনুমোদন" else "Category Admin Approval"
                    } else {
                        val titleText = if (isBengali) currentCategoryItem?.titleBn ?: "" else currentCategoryItem?.titleEn ?: ""
                        if (isBengali) "$titleText অনুমোদন" else "$titleText Approval"
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                if (selectedCategoryKey != null && initialCategoryKey == null) {
                                    selectedCategoryKey = null
                                } else {
                                    onBack()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.Black
                            )
                        }
                    }
                )

                if (selectedCategoryKey != null) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryKey == null,
                                onClick = { selectedCategoryKey = null },
                                label = { Text(if (isBengali) "সকল ক্যাটাগরি ⊞" else "All Categories ⊞", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = tealColor, selectedLabelColor = Color.White)
                            )
                        }
                        items(categoryList) { item ->
                            val isSelected = selectedCategoryKey == item.key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategoryKey = item.key },
                                label = {
                                    Text(
                                        text = "${if (isBengali) item.titleBn else item.titleEn} (${item.pendingCount})",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = item.color,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
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
            if (selectedCategoryKey == null) {
                // Category Selection Grid
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = tealColor),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (isBengali) "অপেক্ষমাণ পোস্টের পরিসংখ্যান" else "Pending Posts Overview",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = if (isBengali) "মোট $totalPendingCount টি পোস্ট অনুমোদনের জন্য অপেক্ষমাণ" else "Total $totalPendingCount posts awaiting approval",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(42.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }

                    Text(
                        text = if (isBengali) "ক্যাটাগরি নির্বাচন করে পোস্টসমূহ অনুমোদন করুন:" else "Select Category to Approve Posts:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF1E293B)
                    )

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        items(categoryList.chunked(2)) { pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                pair.forEach { catItem ->
                                    Card(
                                        onClick = { selectedCategoryKey = catItem.key },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(14.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(40.dp)
                                                        .clip(CircleShape)
                                                        .background(catItem.color.copy(alpha = 0.12f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(catItem.icon, contentDescription = null, tint = catItem.color, modifier = Modifier.size(22.dp))
                                                }

                                                Surface(
                                                    shape = RoundedCornerShape(12.dp),
                                                    color = if (catItem.pendingCount > 0) Color(0xFFEF4444) else Color(0xFF22C55E)
                                                ) {
                                                    Text(
                                                        text = if (catItem.pendingCount > 0) "${catItem.pendingCount} টি" else (if (isBengali) "০" else "0"),
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                                    )
                                                }
                                            }

                                            Text(
                                                text = if (isBengali) catItem.titleBn else catItem.titleEn,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = Color(0xFF0F172A)
                                            )

                                            Text(
                                                text = if (catItem.pendingCount > 0) {
                                                    if (isBengali) "${catItem.pendingCount}টি পোস্ট অপেক্ষমাণ" else "${catItem.pendingCount} Pending"
                                                } else {
                                                    if (isBengali) "কোনো অপেক্ষমাণ নেই" else "All Approved"
                                                },
                                                fontSize = 12.sp,
                                                color = if (catItem.pendingCount > 0) Color(0xFFDC2626) else Color.Gray
                                            )
                                        }
                                    }
                                }
                                if (pair.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            } else {
                when (selectedCategoryKey) {
                    "doctor" -> {
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
                "health" -> {
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
                "donor" -> {
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
                "blood" -> {
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
                "houserent" -> {
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
                "event" -> {
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
                "shopping" -> {
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
                "driver" -> {
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
                "mistri" -> {
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
                "tutor" -> {
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
                "property" -> {
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
                "hotel" -> {
                    if (pendingHotels.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ হোটেলের পোস্ট নেই" else "No pending hotel posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingHotels) { hotel ->
                                PendingHotelCard(
                                    hotel = hotel,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = hotel },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = hotelRepo.approveHotel(hotel.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = hotelRepo.rejectHotel(hotel.id)
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
                "restaurant" -> {
                    if (pendingRestaurants.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ রেস্টুরেন্টের পোস্ট নেই" else "No pending restaurant posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingRestaurants) { rest ->
                                PendingRestaurantCard(
                                    restaurant = rest,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = rest },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = restaurantRepo.approveRestaurant(rest.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = restaurantRepo.rejectRestaurant(rest.id)
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
                "rentcar" -> {
                    if (pendingRentCars.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ গাড়ি ভাড়ার পোস্ট নেই" else "No pending rent car posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingRentCars) { car ->
                                PendingRentCarCard(
                                    car = car,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = car },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = rentCarRepo.approveRentCar(car.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = rentCarRepo.rejectRentCar(car.id)
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
                "training" -> {
                    if (pendingTrainingPosts.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ ট্রেনিং একাডেমি পোস্ট নেই" else "No pending training academy posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingTrainingPosts) { course ->
                                PendingTrainingCard(
                                    course = course,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = course },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = trainingRepo.approveTrainingPost(course.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = trainingRepo.rejectTrainingPost(course.id)
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
                "legal", "deed_amin" -> {
                    val currentPendingList = pendingLegalServices.filter { it.categoryKey == selectedCategoryKey }
                    if (currentPendingList.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ আইনি/দলিল লেখক পোস্ট নেই" else "No pending legal/deed writer posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(currentPendingList) { item ->
                                PendingLegalCard(
                                    service = item,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = item },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = legalRepo.approveLegalService(item.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = legalRepo.rejectLegalService(item.id)
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
                "hajj", "tour" -> {
                    val currentPendingList = pendingHajjTourPosts.filter { it.categoryKey == selectedCategoryKey }
                    if (currentPendingList.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ হজ/উমরাহ বা ট্যুর পোস্ট নেই" else "No pending Hajj/Tour posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(currentPendingList) { item ->
                                PendingHajjTourCard(
                                    item = item,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = item },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = hajjTourRepo.approveHajjTourPost(item.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = hajjTourRepo.rejectHajjTourPost(item.id)
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
                "money_exchange" -> {
                    if (pendingMoneyExchanges.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ মানি এক্সচেঞ্জ পোস্ট নেই" else "No pending money exchange posts")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingMoneyExchanges) { item ->
                                PendingMoneyExchangeCard(
                                    item = item,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = item },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = moneyExchangeRepo.approveMoneyExchangePost(item.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = moneyExchangeRepo.rejectMoneyExchangePost(item.id)
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
                "missing_found" -> {
                    if (pendingMissingFounds.isEmpty()) {
                        EmptyPendingBox(if (isBengali) "কোনো অপেক্ষমাণ নিখোঁজ/প্রাপ্তি বিজ্ঞপ্তি নেই" else "No pending missing/found notices")
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(pendingMissingFounds) { item ->
                                PendingMissingFoundCard(
                                    item = item,
                                    isBengali = isBengali,
                                    onItemClick = { selectedItemForDetail = item },
                                    onApprove = {
                                        coroutineScope.launch {
                                            val res = missingFoundRepo.approveMissingFoundPost(item.id)
                                            if (res.isSuccess) {
                                                Toast.makeText(context, if (isBengali) "পোস্টটি অনুমোদিত হয়েছে!" else "Approved successfully!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    },
                                    onReject = {
                                        coroutineScope.launch {
                                            val res = missingFoundRepo.rejectMissingFoundPost(item.id)
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
                "admin_notice" -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                noticeTitleInput = ""
                                noticeContentInput = ""
                                noticeDateInput = ""
                                isNoticeUrgent = false
                                showNewNoticeDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Icon(Icons.Default.Campaign, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBengali) "+ নতুন নোটিশ প্রকাশ করুন" else "+ Publish New Notice", fontWeight = FontWeight.Bold)
                        }

                        if (allNotices.isEmpty()) {
                            EmptyPendingBox(if (isBengali) "কোনো প্রকাশিত নোটিশ নেই" else "No published notices found")
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(allNotices, key = { it.id }) { notice ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color.White),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Surface(
                                                    color = if (notice.priority == "urgent") Color(0xFFDC2626) else Color(0xFF0F766E),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = if (notice.priority == "urgent") (if (isBengali) "জরুরী" else "URGENT") else (if (isBengali) "সাধারণ" else "NORMAL"),
                                                        color = Color.White,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                                IconButton(
                                                    onClick = {
                                                        coroutineScope.launch {
                                                            val res = noticeRepo.deleteNotice(notice.id)
                                                            if (res.isSuccess) {
                                                                Toast.makeText(context, if (isBengali) "নোটিশ মুছে ফেলা হয়েছে" else "Notice deleted", Toast.LENGTH_SHORT).show()
                                                            }
                                                        }
                                                    }
                                                ) {
                                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFDC2626))
                                                }
                                            }
                                            Text(text = notice.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(text = notice.content, fontSize = 13.sp, color = Color.DarkGray, maxLines = 3)
                                            if (notice.date.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(6.dp))
                                                Text(text = "${if (isBengali) "তারিখ:" else "Date:"} ${notice.date}", fontSize = 11.sp, color = Color.Gray)
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
}

// Publish New Notice Dialog
if (showNewNoticeDialog) {
    AlertDialog(
        onDismissRequest = { showNewNoticeDialog = false },
        icon = { Icon(Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(36.dp)) },
        title = { Text(if (isBengali) "নতুন নোটিশ প্রকাশ করুন" else "Publish New Notice", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = noticeTitleInput,
                    onValueChange = { noticeTitleInput = it },
                    label = { Text(if (isBengali) "নোটিশের শিরোনাম *" else "Notice Title *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = noticeDateInput,
                    onValueChange = { noticeDateInput = it },
                    label = { Text(if (isBengali) "তারিখ (যেমন: ১১ আগস্ট ২০২৬)" else "Date (e.g. 11 Aug 2026)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = noticeContentInput,
                    onValueChange = { noticeContentInput = it },
                    label = { Text(if (isBengali) "নোটিশের বিস্তারিত বিবরণ *" else "Notice Description *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    maxLines = 4
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = isNoticeUrgent,
                        onCheckedChange = { isNoticeUrgent = it }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "জরুরী নোটিশ হিসেবে চিহ্ণিত করুন" else "Mark as Urgent Notice", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (noticeTitleInput.isBlank() || noticeContentInput.isBlank()) {
                        Toast.makeText(context, if (isBengali) "শিরোনাম ও বিবরণ দিন" else "Title and content required", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmittingNotice = true
                    coroutineScope.launch {
                        val newNotice = NoticeDto(
                            title = noticeTitleInput.trim(),
                            content = noticeContentInput.trim(),
                            date = noticeDateInput.trim(),
                            priority = if (isNoticeUrgent) "urgent" else "normal",
                            isPinned = isNoticeUrgent
                        )
                        val res = noticeRepo.saveNotice(newNotice)
                        isSubmittingNotice = false
                        if (res.isSuccess) {
                            showNewNoticeDialog = false
                            Toast.makeText(context, if (isBengali) "নোটিশ সফলভাবে প্রকাশিত হয়েছে!" else "Notice published successfully!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "ত্রুটি: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                enabled = !isSubmittingNotice,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
            ) {
                if (isSubmittingNotice) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Text(if (isBengali) "প্রকাশ করুন" else "Publish")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = { showNewNoticeDialog = false }) {
                Text(if (isBengali) "বাতিল" else "Cancel")
            }
        }
    )
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

                    is LegalServiceDto -> {
                        Text(item.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "পদবী / ডিগ্রি" else "Designation", item.designation)
                        DetailRow(if (isBengali) "ক্যাটাগরি" else "Category", if (item.categoryKey == "legal") "আইনি সেবা" else "দলিল লেখক/আমিন")
                        DetailRow(if (isBengali) "উপ-ক্যাটাগরি" else "Sub Category", item.subCategory)
                        DetailRow(if (isBengali) "শিরোনাম" else "Title", item.title)
                        if (item.chamberOrOffice.isNotBlank()) DetailRow(if (isBengali) "চেম্বার / অফিস" else "Chamber / Office", item.chamberOrOffice)
                        if (item.experience.isNotBlank()) DetailRow(if (isBengali) "অভিজ্ঞতা" else "Experience", item.experience)
                        if (item.feeInfo.isNotBlank()) DetailRow(if (isBengali) "ফি" else "Fee", item.feeInfo)
                        DetailRow(if (isBengali) "অবস্থান / এলাকা" else "Location", item.location)
                        DetailRow(if (isBengali) "মোবাইল" else "Contact", item.contact)
                        if (item.whatsapp.isNotBlank()) DetailRow("WhatsApp", item.whatsapp)
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Description", item.description)
                    }

                    is HajjTourDto -> {
                        Text(item.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "এজেন্সির নাম" else "Agency Name", item.agencyName)
                        DetailRow(if (isBengali) "ক্যাটাগরি" else "Category", if (item.categoryKey == "hajj") "হজ ও উমরাহ সেবা" else "ট্যুর ও ট্রাভেলস")
                        DetailRow(if (isBengali) "উপ-ক্যাটাগরি" else "Sub Category", item.subCategory)
                        if (item.licenseNo.isNotBlank()) DetailRow(if (isBengali) "লাইসেন্স নং" else "License No", item.licenseNo)
                        if (item.packagePrice.isNotBlank()) DetailRow(if (isBengali) "প্যাকেজ মূল্য" else "Price", item.packagePrice)
                        if (item.duration.isNotBlank()) DetailRow(if (isBengali) "মেয়াদ" else "Duration", item.duration)
                        if (item.proprietorOrManager.isNotBlank()) DetailRow(if (isBengali) "পরিচালক/প্রোপাইটর" else "Proprietor/Manager", item.proprietorOrManager)
                        DetailRow(if (isBengali) "অফিসের স্থান" else "Office Location", item.location)
                        DetailRow(if (isBengali) "মোবাইল" else "Contact Phone", item.contact)
                        if (item.whatsapp.isNotBlank()) DetailRow("WhatsApp", item.whatsapp)
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Description", item.description)
                    }

                    is MoneyExchangeDto -> {
                        Text(item.agencyName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Black)
                        DetailRow(if (isBengali) "উপ-ক্যাটাগরি" else "Sub Category", item.subCategory)
                        if (item.availableCurrencies.isNotBlank()) DetailRow(if (isBengali) "মুদ্রা" else "Currencies", item.availableCurrencies)
                        if (item.licenseNo.isNotBlank()) DetailRow(if (isBengali) "লাইসেন্স নং" else "License No", item.licenseNo)
                        if (item.address.isNotBlank()) DetailRow(if (isBengali) "ঠিকানা" else "Address", item.address)
                        DetailRow(if (isBengali) "অবস্থান" else "Location", item.location)
                        DetailRow(if (isBengali) "মোবাইল" else "Contact Phone", item.contact)
                        if (item.whatsapp.isNotBlank()) DetailRow("WhatsApp", item.whatsapp)
                        if (item.description.isNotBlank()) DetailRow(if (isBengali) "বিবরণ" else "Description", item.description)
                    }

                    is MissingFoundDto -> {
                        Text(
                            text = if (item.noticeType == "missing") "নিখোঁজ: ${item.title}" else "পাওয়া গেছে: ${item.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (item.noticeType == "missing") Color(0xFFDC2626) else Color(0xFF16A34A)
                        )
                        DetailRow(if (isBengali) "উপ-ক্যাটাগরি" else "Sub Category", item.subCategory)
                        if (item.nameOrItem.isNotBlank()) DetailRow(if (isBengali) "নাম/জিনিস" else "Name/Item", item.nameOrItem)
                        if (item.ageOrDetails.isNotBlank()) DetailRow(if (isBengali) "বয়স/বর্ণনা" else "Age/Details", item.ageOrDetails)
                        if (item.incidentDate.isNotBlank()) DetailRow(if (isBengali) "ঘটনার তারিখ" else "Date", item.incidentDate)
                        DetailRow(if (isBengali) "স্থান" else "Location", item.location)
                        DetailRow(if (isBengali) "মোবাইল" else "Contact Phone", item.contact)
                        if (item.rewardOrNote.isNotBlank()) DetailRow(if (isBengali) "পুরস্কার/নোট" else "Reward/Note", item.rewardOrNote)
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

@Composable
fun PendingHotelCard(
    hotel: com.barisal.cityservice.data.model.HotelDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Hotel, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = hotel.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = hotel.type, fontSize = 13.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "ঠিকানা:" else "Address:"} ${hotel.address}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${hotel.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingRestaurantCard(
    restaurant: com.barisal.cityservice.data.model.RestaurantDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Restaurant, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = restaurant.cuisineType, fontSize = 13.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "ঠিকানা:" else "Address:"} ${restaurant.address}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${restaurant.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingRentCarCard(
    car: com.barisal.cityservice.data.model.RentCarDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Color(0xFF1D4ED8), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = car.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = "${car.subCategory} • ৳${car.price}", fontSize = 13.sp, color = Color(0xFF1D4ED8), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "ড্রাইভার/মালিক:" else "Driver/Owner:"} ${car.driverName} (${car.contact})", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ঠিকানা:" else "Address:"} ${car.address}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingTrainingCard(
    course: com.barisal.cityservice.data.model.TrainingAcademyDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = "${course.academyName} • ৳${course.courseFee}", fontSize = 13.sp, color = Color(0xFF6D28D9), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "ঠিকানা:" else "Address:"} ${course.address}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${course.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingJobCard(
    job: com.barisal.cityservice.data.model.JobDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Work, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = "${job.organizationName} • ${job.subCategory}", fontSize = 13.sp, color = Color(0xFF0F766E), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "বেতন:" else "Salary:"} ${job.salary}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "কর্মস্থল:" else "Location:"} ${job.location}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${job.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingDomesticHelpCard(
    help: com.barisal.cityservice.data.model.DomesticHelpDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = help.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(text = "${help.providerName} • ${help.subCategory}", fontSize = 13.sp, color = Color(0xFFD97706), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "প্রত্যাশিত বেতন:" else "Rate:"} ${help.expectedSalary}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "এলাকা:" else "Location:"} ${help.location}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${help.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingLegalCard(
    service: LegalServiceDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isLegal = service.categoryKey == "legal"
    val themeColor = if (isLegal) Color(0xFF1E293B) else Color(0xFFD97706)

    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isLegal) Icons.Default.Gavel else Icons.Default.Assignment,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = service.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                    Text(
                        text = "${service.designation} • ${service.subCategory}",
                        fontSize = 13.sp,
                        color = themeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${if (isBengali) "শিরোনাম:" else "Title:"} ${service.title}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "চেম্বার/অফিস:" else "Chamber:"} ${service.chamberOrOffice}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${service.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingHajjTourCard(
    item: HajjTourDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isHajj = item.categoryKey == "hajj"
    val themeColor = if (isHajj) Color(0xFF047857) else Color(0xFF0284C7)

    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isHajj) Icons.Default.Mosque else Icons.Default.FlightTakeoff,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Text(
                        text = "${item.agencyName} • ${item.subCategory}",
                        fontSize = 13.sp,
                        color = themeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (item.packagePrice.isNotBlank()) Text(text = "${if (isBengali) "মূল্য:" else "Price:"} ${item.packagePrice}", fontSize = 12.sp, color = Color.Gray)
            if (item.licenseNo.isNotBlank()) Text(text = "${if (isBengali) "লাইসেন্স নং:" else "License:"} ${item.licenseNo}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${item.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingMoneyExchangeCard(
    item: MoneyExchangeDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val themeColor = Color(0xFF0F766E)

    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CurrencyExchange,
                    contentDescription = null,
                    tint = themeColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = item.agencyName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                    Text(
                        text = "${item.subCategory} • ${item.availableCurrencies}",
                        fontSize = 12.sp,
                        color = themeColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (item.address.isNotBlank()) Text(text = "${if (isBengali) "ঠিকানা:" else "Address:"} ${item.address}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${item.contact}", fontSize = 12.sp, color = Color.Gray)
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
fun PendingMissingFoundCard(
    item: MissingFoundDto,
    isBengali: Boolean,
    onItemClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    val isMissing = item.noticeType == "missing"
    val typeColor = if (isMissing) Color(0xFFDC2626) else Color(0xFF16A34A)
    val typeText = if (isMissing) (if (isBengali) "নিখোঁজ" else "MISSING") else (if (isBengali) "পাওয়া গেছে" else "FOUND")

    Card(
        onClick = onItemClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isMissing) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = typeColor,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(color = typeColor, shape = RoundedCornerShape(4.dp)) {
                            Text(text = typeText, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = item.subCategory, fontSize = 11.sp, color = Color.Gray)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (item.nameOrItem.isNotBlank()) Text(text = "${if (isBengali) "নাম/জিনিস:" else "Name/Item:"} ${item.nameOrItem}", fontSize = 12.sp, color = Color.Gray)
            Text(text = "${if (isBengali) "ফোন:" else "Contact:"} ${item.contact}", fontSize = 12.sp, color = Color.Gray)
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
