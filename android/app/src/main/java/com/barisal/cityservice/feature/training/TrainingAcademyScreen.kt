package com.barisal.cityservice.feature.training

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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.TrainingAcademyDto
import com.barisal.cityservice.data.repository.TrainingAcademyRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingAcademyScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPostTrainingAcademy: () -> Unit = {},
    onNavigateToTrainingAcademyDetail: (String) -> Unit = {}
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val repository = remember { TrainingAcademyRepository() }
    val firestoreCourses by repository.getTrainingPostsFlow().collectAsState(initial = emptyList())

    SetStatusBarColor()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    
    val primaryColor = Color(0xFF6D28D9) // Purple theme for education & academies

    val subCategories = listOf(
        if (isBengali) "সব" else "All",
        if (isBengali) "কার ড্রাইভিং" else "Car Driving",
        if (isBengali) "কম্পিউটার ট্রেনিং" else "Computer Training",
        if (isBengali) "টেকনিক্যাল ট্রেনিং" else "Technical Training",
        if (isBengali) "ভাষা শিক্ষা" else "Language Learning",
        if (isBengali) "চাকরি ও ক্যারিয়ার" else "Job & Career",
        if (isBengali) "অন্যান্য" else "Others"
    )

    val dummyCourses = listOf(
        TrainingAcademyDto(
            id = "preset_1",
            title = if (isBengali) "প্রফেশনাল কার ড্রাইভিং কোর্স" else "Professional Car Driving Course",
            subCategory = if (isBengali) "কার ড্রাইভিং" else "Car Driving",
            academyName = if (isBengali) "বরিশাল ড্রাইভ ফেয়ার একাডেমি" else "Barisal Drive Fair Academy",
            courseFee = "5000",
            duration = if (isBengali) "১ মাস (২০টি ক্লাস)" else "1 Month (20 Classes)",
            contact = "01711-445566",
            address = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal"
        ),
        TrainingAcademyDto(
            id = "preset_2",
            title = if (isBengali) "কম্পিউটার অফিস অ্যাপ্লিকেশন ও গ্রাফিক্স" else "Computer Office & Graphics",
            subCategory = if (isBengali) "কম্পিউটার ট্রেনিং" else "Computer Training",
            academyName = if (isBengali) "সিটি আইটি ইনস্টিটিউট" else "City IT Institute",
            courseFee = "3500",
            duration = if (isBengali) "৩ মাস" else "3 Months",
            contact = "01712-556677",
            address = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal"
        ),
        TrainingAcademyDto(
            id = "preset_3",
            title = if (isBengali) "স্পোকেন ইংলিশ ও আইইএলটিএস" else "Spoken English & IELTS",
            subCategory = if (isBengali) "ভাষা শিক্ষা" else "Language Learning",
            academyName = if (isBengali) "গ্লোবাল লিঙ্গুয়া একাডেমি" else "Global Lingua Academy",
            courseFee = "4000",
            duration = if (isBengali) "২ মাস" else "2 Months",
            contact = "01713-667788",
            address = if (isBengali) "বান্দ রোড, বরিশাল" else "Band Road, Barisal"
        )
    )

    val combinedCourses = remember(firestoreCourses, searchQuery, selectedSubCategory, selectedZilla) {
        val approvedFirestore = firestoreCourses.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyCourses else dummyCourses
        list.filter { course ->
            val matchesSearch = searchQuery.isBlank() ||
                    course.title.contains(searchQuery, ignoreCase = true) ||
                    course.academyName.contains(searchQuery, ignoreCase = true) ||
                    course.address.contains(searchQuery, ignoreCase = true) ||
                    course.subCategory.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    course.subCategory.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(course.subCategory, ignoreCase = true)
            val matchesZilla = selectedZilla == null || course.zilla.contains(selectedZilla!!, ignoreCase = true) || course.address.contains(selectedZilla!!, ignoreCase = true)

            matchesSearch && matchesCategory && matchesZilla
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "ট্রেনিং একাডেমি" else "Training Academy",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Filter", tint = Color.Black)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostTrainingAcademy,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Training")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "ট্রেনিং পোস্ট করুন" else "Post Training",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
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
                    items(com.barisal.cityservice.core.utils.bangladeshZillas) { zilla ->
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
                .background(Color(0xFFF8FAFC))
        ) {
            // Search Bar Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { 
                        Text(
                            text = if (isBengali) "কোর্স বা একাডেমি খুঁজুন..." else "Search Course or Academy...",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                    ),
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    },
                    singleLine = true
                )
            }

            // Sub-Category Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subCategories) { cat ->
                    val isSelected = selectedSubCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubCategory = cat },
                        label = { Text(cat, fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Training Course Posts List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(bottom = 88.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(combinedCourses) { course ->
                    TrainingAcademyCard(
                        course = course,
                        primaryColor = primaryColor,
                        isBengali = isBengali,
                        onClick = { onNavigateToTrainingAcademyDetail(course.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingAcademyCard(
    course: TrainingAcademyDto,
    primaryColor: Color,
    isBengali: Boolean,
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                if (course.coverImage.isNotBlank()) {
                    AsyncImage(
                        model = course.coverImage,
                        contentDescription = course.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(75.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(primaryColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = primaryColor, modifier = Modifier.size(38.dp))
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = course.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    Text(text = course.academyName.ifBlank { course.subCategory }, fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = course.address, fontSize = 13.sp, color = Color.Gray)
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (course.duration.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = course.duration, fontSize = 12.sp, color = Color.DarkGray)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }

                if (course.courseFee.isNotBlank()) {
                    Text(
                        text = "৳${course.courseFee}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                } else {
                    Text(
                        text = if (isBengali) "বিস্তারিত দেখুন ➔" else "View Details ➔",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        }
    }
}
