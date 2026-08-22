package com.barisal.cityservice.feature.hajjtour

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
import com.barisal.cityservice.data.model.HajjTourDto
import com.barisal.cityservice.data.repository.HajjTourRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HajjTourScreen(
    categoryKey: String = "hajj", // "hajj" for Hajj & Umrah, "tour" for Tour & Travels
    onNavigateBack: () -> Unit = {},
    onNavigateToPostHajjTour: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repo = remember { HajjTourRepository() }

    val isHajj = categoryKey == "hajj"
    val themeColor = if (isHajj) Color(0xFF047857) else Color(0xFF0284C7) // Emerald for Hajj, Sky Blue for Tour

    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সবকটি" else "All") }
    var searchQuery by remember { mutableStateOf("") }

    val postsList by repo.getApprovedHajjTourPosts(categoryKey).collectAsState(initial = emptyList())

    val subCategories = remember(categoryKey, isBengali) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        if (isHajj) {
            listOf(allLabel) + if (isBengali) {
                listOf("হজ প্যাকেজ", "উমরাহ প্যাকেজ", "সৌদি ভিসা ও মেডিকেল", "মাক্কাহ-মদিনা হোটেল ও পরিবহন", "অন্যান্য")
            } else {
                listOf("Hajj Package", "Umrah Package", "Saudi Visa & Medical", "Hotel & Transport", "Others")
            }
        } else {
            listOf(allLabel) + if (isBengali) {
                listOf("বিমান টিকিট", "দেশীয় ট্যুর প্যাকেজ", "আন্তর্জাতিক ট্যুর", "ভিসা প্রসেসিং", "অন্যান্য")
            } else {
                listOf("Air Ticket", "Domestic Tour", "International Tour", "Visa Processing", "Others")
            }
        }
    }

    val filteredPosts = remember(postsList, selectedSubCategory, searchQuery) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        postsList.filter { item ->
            val matchesSubCategory = selectedSubCategory == allLabel || item.subCategory.equals(selectedSubCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.agencyName.contains(searchQuery, ignoreCase = true) ||
                    item.destination.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true)
            matchesSubCategory && matchesSearch
        }
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isHajj) {
                    if (isBengali) "হজ ও উমরাহ সেবা" else "Hajj & Umrah Services"
                } else {
                    if (isBengali) "ট্যুর ও ট্রাভেলস" else "Tour & Travels"
                },
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPostHajjTour,
                containerColor = themeColor,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Agency Offer")
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
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                placeholder = {
                    Text(
                        text = if (isHajj) {
                            if (isBengali) "খুঁজুন (হজ/উমরাহ প্যাকেজ, এজেন্সির নাম...)" else "Search (Hajj/Umrah, Agency...)"
                        } else {
                            if (isBengali) "খুঁজুন (ট্যুর প্যাকেজ, গন্তব্য, এজেন্সির নাম...)" else "Search (Tour Package, Destination...)"
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

            // SubCategory Filter Chips
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

            // List or Empty Box
            if (filteredPosts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = if (isHajj) Icons.Default.Mosque else Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isHajj) {
                                if (isBengali) "কোনো হজ বা উমরাহ অফার পাওয়া যায়নি" else "No Hajj/Umrah packages found"
                            } else {
                                if (isBengali) "কোনো ট্যুর বা ট্রাভেলস অফার পাওয়া যায়নি" else "No tour packages found"
                            },
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBengali) "নতুন প্যাকেজ জমা দিতে + বাটনে চাপ দিন" else "Tap + button to add a package",
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
                    items(filteredPosts, key = { it.id }) { item ->
                        HajjTourCard(
                            item = item,
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
fun HajjTourCard(
    item: HajjTourDto,
    isBengali: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isHajj = item.categoryKey == "hajj"
    val themeColor = if (isHajj) Color(0xFF047857) else Color(0xFF0284C7)

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Banner / Agency Image
                val coilModel = remember(item.bannerImage) { item.bannerImage.toCoilModel() }
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Banner",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, themeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(themeColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHajj) Icons.Default.Mosque else Icons.Default.FlightTakeoff,
                            contentDescription = null,
                            tint = themeColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 2
                    )
                    Text(
                        text = item.agencyName,
                        fontSize = 13.sp,
                        color = themeColor,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    if (item.subCategory.isNotBlank()) {
                        Surface(
                            color = themeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text(
                                text = item.subCategory,
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (item.packagePrice.isNotBlank()) {
                    Text(
                        text = "${if (isBengali) "মূল্য:" else "Price:"} ${item.packagePrice}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColor
                    )
                }
                if (item.duration.isNotBlank()) {
                    Text(
                        text = "${if (isBengali) "মেয়াদ:" else "Duration:"} ${item.duration}",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                }
            }

            if (item.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.location,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Call / WhatsApp buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (item.contact.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.contact}"))
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

                if (item.whatsapp.isNotBlank()) {
                    OutlinedButton(
                        onClick = {
                            val cleanNumber = item.whatsapp.replace("+", "").replace("-", "").replace(" ", "")
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
