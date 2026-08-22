package com.barisal.cityservice.feature.missingfound

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.barisal.cityservice.data.model.MissingFoundDto
import com.barisal.cityservice.data.repository.MissingFoundRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissingFoundScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToPostMissingFound: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repo = remember { MissingFoundRepository() }

    var selectedTypeTab by remember { mutableStateOf("all") } // "all", "missing", "found"
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সবকটি" else "All") }
    var searchQuery by remember { mutableStateOf("") }

    val postsList by repo.getApprovedMissingFoundPosts(selectedTypeTab).collectAsState(initial = emptyList())

    val subCategories = remember(isBengali) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        listOf(allLabel) + if (isBengali) {
            listOf("নিখোঁজ ব্যক্তি", "নিখোঁজ শিশু", "নিখোঁজ বয়স্ক", "হারানো কাগজপত্র / আইডি", "হারানো মানিব্যাগ / টাকা", "হারানো মোবাইল / ডিভাইস", "পাওয়া গেছে (ব্যক্তি/জিনিস)")
        } else {
            listOf("Missing Person", "Missing Child", "Missing Elderly", "Lost Documents / ID", "Lost Wallet / Cash", "Lost Electronics", "Found Item / Person")
        }
    }

    val filteredPosts = remember(postsList, selectedSubCategory, searchQuery) {
        val allLabel = if (isBengali) "সবকটি" else "All"
        postsList.filter { item ->
            val matchesSubCategory = selectedSubCategory == allLabel || item.subCategory.equals(selectedSubCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.nameOrItem.contains(searchQuery, ignoreCase = true) ||
                    item.location.contains(searchQuery, ignoreCase = true)
            matchesSubCategory && matchesSearch
        }
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "নিখোঁজ ও প্রাপ্তি বিজ্ঞপ্তি" else "Missing & Found Notices",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToPostMissingFound,
                containerColor = Color(0xFFDC2626), // Red accent for missing/found notice
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Notice")
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Type Switcher Row ("সবকটি", "নিখোঁজ", "পাওয়া গেছে")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedTypeTab == "all",
                    onClick = { selectedTypeTab = "all" },
                    label = { Text(if (isBengali) "সকল বিজ্ঞপ্তি" else "All Notices", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF334155),
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTypeTab == "missing",
                    onClick = { selectedTypeTab = "missing" },
                    label = { Text(if (isBengali) "নিখোঁজ" else "Missing", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDC2626), // Red
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    selected = selectedTypeTab == "found",
                    onClick = { selectedTypeTab = "found" },
                    label = { Text(if (isBengali) "পাওয়া গেছে" else "Found", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF16A34A), // Green
                        selectedLabelColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = {
                    Text(
                        text = if (isBengali) "খুঁজুন (নাম/জিনিস, এলাকা, বিষয়...)" else "Search (Name, Object, Area...)",
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
                    focusedBorderColor = Color(0xFFDC2626),
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
                            selectedContainerColor = Color(0xFF475569),
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
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isBengali) "কোনো বিজ্ঞপ্তি পাওয়া যায়নি" else "No notices found",
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isBengali) "নতুন বিজ্ঞপ্তি পোস্ট করতে + বাটনে চাপ দিন" else "Tap + button to post a missing/found notice",
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
                        MissingFoundCard(
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
fun MissingFoundCard(
    item: MissingFoundDto,
    isBengali: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val isMissing = item.noticeType == "missing"
    val typeColor = if (isMissing) Color(0xFFDC2626) else Color(0xFF16A34A)
    val typeText = if (isMissing) {
        if (isBengali) "নিখোঁজ" else "MISSING"
    } else {
        if (isBengali) "পাওয়া গেছে" else "FOUND"
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                // Photo
                val coilModel = remember(item.photo) { item.photo.toCoilModel() }
                if (coilModel != null) {
                    AsyncImage(
                        model = coilModel,
                        contentDescription = "Notice Photo",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, typeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(typeColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isMissing) Icons.Default.Warning else Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = typeColor,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = typeColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = typeText,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (item.incidentDate.isNotBlank()) {
                            Text(
                                text = item.incidentDate,
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = item.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color(0xFF0F172A),
                        maxLines = 2
                    )

                    if (item.nameOrItem.isNotBlank()) {
                        Text(
                            text = "${if (isBengali) "নাম/জিনিস:" else "Name/Item:"} ${item.nameOrItem}",
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(10.dp))

            if (item.location.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${if (isBengali) "স্থান:" else "Location:"} ${item.location}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }

            if (item.rewardOrNote.isNotBlank()) {
                Text(
                    text = "${if (isBengali) "পুরস্কার/নোট:" else "Reward/Note:"} ${item.rewardOrNote}",
                    fontSize = 12.sp,
                    color = Color(0xFFB91C1C),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Call / WhatsApp Buttons
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
                        colors = ButtonDefaults.buttonColors(containerColor = typeColor),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "যোগাযোগ করুন" else "Contact", fontSize = 12.sp)
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
