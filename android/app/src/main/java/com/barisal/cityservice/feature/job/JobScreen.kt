package com.barisal.cityservice.feature.job

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.JobDto
import com.barisal.cityservice.data.repository.JobRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToPostJob: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val jobRepo = remember { JobRepository() }

    val firestoreJobs by jobRepo.getApprovedJobs().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }

    val primaryColor = Color(0xFF0F766E)

    val subCategories = remember(isBengali) {
        if (isBengali) {
            listOf("সব", "প্রতিষ্ঠানে চাকরি", "শো-রুমে চাকরি", "দোকানে চাকরি", "অন্যান্য চাকরি")
        } else {
            listOf("All", "Protisthan", "Shoroom", "Dokan", "Others")
        }
    }

    val dummyJobs = remember(isBengali) {
        listOf(
            JobDto(
                id = "preset_job_1",
                title = if (isBengali) "কম্পিউটার ও ক্যাশ অপারেটর" else "Computer & Cash Operator",
                subCategory = if (isBengali) "প্রতিষ্ঠানে চাকরি" else "Protisthan",
                organizationName = if (isBengali) "বরিশাল ডিজিটাল হাব" else "Barisal Digital Hub",
                jobType = if (isBengali) "ফুল টাইম" else "Full Time",
                salary = if (isBengali) "১৫,০০০ - ১৮,০০০ টাকা" else "15,000 - 18,000 BDT",
                experience = if (isBengali) "১ বছর" else "1 Year",
                educationalRequirement = if (isBengali) "এইচএসসি / ডিগ্রি" else "HSC / Degree",
                contact = "01711-889900",
                location = if (isBengali) "সদর রোড, বরিশাল" else "Sadar Road, Barisal",
                description = if (isBengali) "কম্পিউটার টাইপিং ও ক্যাশ ব্যবস্থাপনার অভিজ্ঞতা সম্পন্ন কর্মী প্রয়োজন।" else "Computer typing and cash handling experienced candidate needed."
            ),
            JobDto(
                id = "preset_job_2",
                title = if (isBengali) "সেলস এক্সিকিউটিভ (শো-রুম)" else "Sales Executive (Showroom)",
                subCategory = if (isBengali) "শো-রুমে চাকরি" else "Shoroom",
                organizationName = if (isBengali) "এপেক্স ফুটওয়্যার শো-রুম" else "Apex Footwear Showroom",
                jobType = if (isBengali) "ফুল টাইম" else "Full Time",
                salary = if (isBengali) "১২,০০০ - ১৫,০০০ টাকা" else "12,000 - 15,000 BDT",
                experience = if (isBengali) "অভিজ্ঞতা শিথিলযোগ্য" else "Freshers Welcome",
                educationalRequirement = if (isBengali) "এসএসসি / এইচএসসি" else "SSC / HSC",
                contact = "01712-990011",
                location = if (isBengali) "চকবাজার, বরিশাল" else "Chawkbazar, Barisal",
                description = if (isBengali) "কাস্টমার সার্ভিস ও সেলসে দক্ষ পুরুষ/মহিলা কর্মী আবশ্যক।" else "Customer service skilled staff required."
            ),
            JobDto(
                id = "preset_job_3",
                title = if (isBengali) "দোকান অ্যাসিস্ট্যান্ট ও ডেলিভারি" else "Shop Assistant & Delivery",
                subCategory = if (isBengali) "দোকানে চাকরি" else "Dokan",
                organizationName = if (isBengali) "বিসমিল্লাহ ভ্যারাইটি স্টোর" else "Bismillah Variety Store",
                jobType = if (isBengali) "পার্ট টাইম" else "Part Time",
                salary = if (isBengali) "৮,০০০ - ১০,০০০ টাকা" else "8,000 - 10,000 BDT",
                experience = if (isBengali) "প্রযোজ্য নয়" else "N/A",
                educationalRequirement = if (isBengali) "অষ্টম শ্রেণী পাস" else "Class 8 Pass",
                contact = "01713-112233",
                location = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal",
                description = if (isBengali) "দোকানের মালামাল সাজানো ও কাস্টমার সামলানোর কাজ।" else "Shop product organization and customer assistance."
            )
        )
    }

    val combinedJobs = remember(firestoreJobs, searchQuery, selectedSubCategory) {
        val approvedFirestore = firestoreJobs.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyJobs else dummyJobs
        list.filter { job ->
            val matchesSearch = searchQuery.isBlank() ||
                    job.title.contains(searchQuery, ignoreCase = true) ||
                    job.organizationName.contains(searchQuery, ignoreCase = true) ||
                    job.location.contains(searchQuery, ignoreCase = true) ||
                    job.subCategory.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    job.subCategory.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(job.subCategory, ignoreCase = true)

            matchesSearch && matchesCategory
        }
    }

    SetStatusBarColor()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "চাকরি ও নিয়োগ বিজ্ঞাপ‌নি" else "Jobs Circular",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostJob,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Job")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "চাকরি পোস্ট করুন" else "Post Job",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text(if (isBengali) "চাকরি বা প্রতিষ্ঠানের নাম খুঁজুন..." else "Search jobs or companies...", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = primaryColor) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            // Sub-category filter chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subCategories) { cat ->
                    val isSelected = selectedSubCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubCategory = cat },
                        label = { Text(text = cat, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White,
                            containerColor = Color.White,
                            labelColor = Color(0xFF334155)
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) primaryColor else Color(0xFFCBD5E1),
                            borderWidth = 1.dp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Job List
            if (combinedJobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.WorkOff, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBengali) "কোনো চাকরি পাওয়া যায়নি" else "No job listings found",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(combinedJobs) { job ->
                        JobCard(
                            job = job,
                            isBengali = isBengali,
                            primaryColor = primaryColor,
                            onClick = { onNavigateToDetail(job.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun JobCard(
    job: JobDto,
    isBengali: Boolean,
    primaryColor: Color,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Work, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = job.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                    Text(text = "${job.organizationName} • ${job.subCategory}", fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "💰 ${if (isBengali) "বেতন:" else "Salary:"} ${job.salary}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                if (job.jobType.isNotBlank()) {
                    Text(text = "⏳ ${job.jobType}", fontSize = 12.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
                }
            }

            if (job.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📍 ${if (isBengali) "ঠিকানা:" else "Location:"} ${job.location}", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onClick) {
                    Text(text = if (isBengali) "বিস্তারিত দেখুন ➔" else "View Details ➔", color = primaryColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                if (job.contact.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${job.contact}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "কল করুন" else "Call", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
