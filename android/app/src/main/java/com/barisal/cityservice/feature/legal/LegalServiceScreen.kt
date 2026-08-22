package com.barisal.cityservice.feature.legal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.barisal.cityservice.data.model.LegalServiceDto
import com.barisal.cityservice.data.repository.LegalServiceRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalServiceScreen(
    categoryKey: String = "legal", // "legal" for Legal Services, "deed_amin" for Deed Writer & Amin
    onNavigateBack: () -> Unit = {},
    onNavigateToPostLegalService: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val legalRepo = remember { LegalServiceRepository() }

    val isLegal = categoryKey == "legal"
    val themeColor = if (isLegal) Color(0xFF1E293B) else Color(0xFFD97706)

    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সবকটি" else "All") }
    var searchQuery by remember { mutableStateOf("") }

    val servicesList by legalRepo.getApprovedLegalServices(categoryKey).collectAsState(initial = emptyList())

    val subCategories = remember(categoryKey, isBengali) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        if (isLegal) {
            listOf(allLabel) + if (isBengali) {
                listOf("অ্যাডভোকেট ও আইনজীবী", "দেওয়ানী মামলা", "ফৌজদারী মামলা", "ইনকাম ট্যাক্স ও ভ্যাট", "নোটারী পাবলিক", "অন্যান্য")
            } else {
                listOf("Advocate & Lawyer", "Civil Cases", "Criminal Cases", "Tax & VAT", "Notary Public", "Others")
            }
        } else {
            listOf(allLabel) + if (isBengali) {
                listOf("দলিল লেখক", "আমিন / জমি পরিমাপক", "জমি রেজিস্ট্রেশন কনসালট্যান্ট", "অন্যান্য")
            } else {
                listOf("Deed Writer", "Land Surveyor (Amin)", "Land Registry Consultant", "Others")
            }
        }
    }

    val filteredServices = remember(servicesList, selectedSubCategory, searchQuery) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        servicesList.filter { service ->
            val matchesSubCategory = selectedSubCategory == allLabel || service.subCategory.equals(selectedSubCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    service.name.contains(searchQuery, ignoreCase = true) ||
                    service.title.contains(searchQuery, ignoreCase = true) ||
                    service.designation.contains(searchQuery, ignoreCase = true) ||
                    service.chamberOrOffice.contains(searchQuery, ignoreCase = true) ||
                    service.location.contains(searchQuery, ignoreCase = true)
            matchesSubCategory && matchesSearch
        }
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isLegal) {
                    if (isBengali) "আইনি সেবা ও সহায়তা" else "Legal Services & Advice"
                } else {
                    if (isBengali) "দলিল লেখক ও আমিন সেবা" else "Deed Writer & Land Surveyor"
                },
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPostLegalService,
                containerColor = themeColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Service")
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                placeholder = {
                    Text(
                        text = if (isLegal) {
                            if (isBengali) "আইনজীবী/অফিস খুঁজুন (নাম, চেম্বার, বিষয়...)" else "Search Lawyer (Name, Chamber...)"
                        } else {
                            if (isBengali) "দলিল লেখক/আমিন খুঁজুন (নাম, অফিস...)" else "Search Deed Writer/Amin (Name, Office...)"
                        },
                        fontSize = 13.sp
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = themeColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // SubCategory Horizontal Filter Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(subCategories) { subCat ->
                    FilterChip(
                        selected = selectedSubCategory == subCat,
                        onClick = { selectedSubCategory = subCat },
                        label = { Text(subCat, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = themeColor,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            // Main Content List
            if (filteredServices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isLegal) Icons.Default.Gavel else Icons.Default.Assignment,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isLegal) {
                                if (isBengali) "কোনো আইনি সেবার তথ্য পাওয়া যায়নি" else "No legal service listings found"
                            } else {
                                if (isBengali) "কোনো দলিল লেখক বা আমিন এর তথ্য পাওয়া যায়নি" else "No deed writer or surveyor listings found"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBengali) "নতুন পোস্ট করতে + বাটনে চাপ দিন" else "Tap + button to add a listing",
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredServices, key = { it.id }) { item ->
                        LegalServiceCard(
                            service = item,
                            isBengali = isBengali,
                            onClick = { onNavigateToDetail(item.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LegalServiceCard(
    service: LegalServiceDto,
    isBengali: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isLegal = service.categoryKey == "legal"
    val themeColor = if (isLegal) Color(0xFF1E293B) else Color(0xFFD97706)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Profile Image or Fallback Icon
                val coilModel = remember(service.profileImage) { service.profileImage.toCoilModel() }
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(1.dp, themeColor, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(themeColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isLegal) Icons.Default.Gavel else Icons.Default.Assignment,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = service.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )
                    if (service.designation.isNotBlank()) {
                        Text(
                            text = service.designation,
                            fontSize = 13.sp,
                            color = themeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (service.subCategory.isNotBlank()) {
                        Surface(
                            color = themeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = service.subCategory,
                                color = themeColor,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            if (service.chamberOrOffice.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isBengali) "চেম্বার/অফিস:" else "Office:"} ${service.chamberOrOffice}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            if (service.experience.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (isBengali) "অভিজ্ঞতা:" else "Experience:"} ${service.experience}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            if (service.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = service.location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contact Action Buttons (Call / WhatsApp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (service.contact.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${service.contact}"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "কল করুন" else "Call", fontSize = 12.sp)
                    }
                }

                if (service.whatsapp.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val cleanNumber = service.whatsapp.replace("+", "").replace("-", "").replace(" ", "")
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber"))
                            context.startActivity(intent)
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF16A34A)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
