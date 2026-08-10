package com.barisal.cityservice.feature.domestichelp

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.DomesticHelpDto
import com.barisal.cityservice.data.repository.DomesticHelpRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomesticHelpScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToPostDomesticHelp: () -> Unit = {},
    onNavigateToDetail: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val domesticHelpRepo = remember { DomesticHelpRepository() }

    val firestoreHelps by domesticHelpRepo.getApprovedDomesticHelps().collectAsState(initial = emptyList())
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "সব" else "All") }

    val primaryColor = Color(0xFFD97706) // Warm Amber / Orange

    val subCategories = remember(isBengali) {
        if (isBengali) {
            listOf("সব", "ফুল-টাইম গৃহকর্মী", "পার্ট-টাইম গৃহকর্মী", "রান্নার বুয়া", "শিশু দেখাশোনা", "বয়স্ক সেবা", "বাসা পরিষ্কার", "অন্যান্য")
        } else {
            listOf("All", "Full-Time Maid", "Part-Time Maid", "Cook / Chef", "Baby Sitter", "Elderly Care", "House Cleaner", "Others")
        }
    }

    val dummyHelps = remember(isBengali) {
        listOf(
            DomesticHelpDto(
                id = "preset_help_1",
                title = if (isBengali) "অভিজ্ঞ পার্ট-টাইম রান্নার বুয়া" else "Experienced Part-Time Cook",
                subCategory = if (isBengali) "রান্নার বুয়া" else "Cook / Chef",
                providerName = if (isBengali) "রহিমা বেগম" else "Rahima Begum",
                workType = if (isBengali) "দৈনিক (সকাল ও দুপুর)" else "Daily (Morning & Noon)",
                expectedSalary = if (isBengali) "৪,০০০ - ৫,০০০ টাকা/মাস" else "4,000 - 5,000 BDT/Month",
                experience = if (isBengali) "৫ বছরের অভিজ্ঞতা" else "5 Years Experience",
                contact = "01711-223344",
                location = if (isBengali) "সদর রোড ও চকবাজার, বরিশাল" else "Sadar Road & Chawkbazar, Barisal",
                description = if (isBengali) "বাংলা সব ধরনের খাবার ও বিরিয়ানি সুস্বাদু করে রান্না করতে পারেন।" else "Expert in authentic Bengali cuisine & biryani."
            ),
            DomesticHelpDto(
                id = "preset_help_2",
                title = if (isBengali) "ফুল-টাইম বিশ্বস্ত গৃহকর্মী প্রয়োজন" else "Full-Time Trusted House Maid Needed",
                subCategory = if (isBengali) "ফুল-টাইম গৃহকর্মী" else "Full-Time Maid",
                providerName = if (isBengali) "বরিশাল কেয়ার সার্ভিস" else "Barisal Care Service",
                workType = if (isBengali) "মাসিক (থাকা ও খাওয়া ফ্রি)" else "Monthly (Food & Stay Free)",
                expectedSalary = if (isBengali) "৮,০০০ - ১০,০০০ টাকা/মাস" else "8,000 - 10,000 BDT/Month",
                experience = if (isBengali) "৩ বছরের অভিজ্ঞতা" else "3 Years Experience",
                contact = "01712-334455",
                location = if (isBengali) "নথুল্লাবাদ, বরিশাল" else "Nathullabad, Barisal",
                description = if (isBengali) "বাসার কাপড়া ধোয়া, ঘর মোছা ও ঝাড়ু দেওয়ার কাজ।" else "Cleaning, laundry, and house maintenance."
            ),
            DomesticHelpDto(
                id = "preset_help_3",
                title = if (isBengali) "অভিজ্ঞ বেবি সিটার / শিশু যত্নকারী" else "Experienced Baby Sitter / Child Care",
                subCategory = if (isBengali) "শিশু দেখাশোনা" else "Baby Sitter",
                providerName = if (isBengali) "ফাতেমা বেগম" else "Fatema Begum",
                workType = if (isBengali) "পার্ট টাইম (বিকাল ৪টা - রাত ৮টা)" else "Part Time (4 PM - 8 PM)",
                expectedSalary = if (isBengali) "৬,০০০ টাকা/মাস" else "6,000 BDT/Month",
                experience = if (isBengali) "৪ বছরের অভিজ্ঞতা" else "4 Years Experience",
                contact = "01713-445566",
                location = if (isBengali) "বান্দ রোড, বরিশাল" else "Band Road, Barisal",
                description = if (isBengali) "ছোট বাচ্চাদের যত্ন নেওয়া ও খেলাধুলা করানো।" else "Caring for young toddlers and children."
            )
        )
    }

    val combinedHelps = remember(firestoreHelps, searchQuery, selectedSubCategory) {
        val approvedFirestore = firestoreHelps.filter { it.isApproved }
        val list = if (approvedFirestore.isNotEmpty()) approvedFirestore + dummyHelps else dummyHelps
        list.filter { help ->
            val matchesSearch = searchQuery.isBlank() ||
                    help.title.contains(searchQuery, ignoreCase = true) ||
                    help.providerName.contains(searchQuery, ignoreCase = true) ||
                    help.location.contains(searchQuery, ignoreCase = true) ||
                    help.subCategory.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedSubCategory == "সব" || selectedSubCategory == "All" ||
                    help.subCategory.contains(selectedSubCategory, ignoreCase = true) ||
                    selectedSubCategory.contains(help.subCategory, ignoreCase = true)

            matchesSearch && matchesCategory
        }
    }

    SetStatusBarColor()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "গৃহকর্মী ও বুয়া সার্ভিস" else "Domestic Help & Maid",
                onBackClick = onNavigateBack
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToPostDomesticHelp,
                containerColor = primaryColor,
                contentColor = Color.White,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Post Maid")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "গৃহকর্মী পোস্ট করুন" else "Post Maid / Help",
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
                placeholder = { Text(if (isBengali) "বুয়া বা গৃহকর্মী খুঁজুন..." else "Search maid or domestic help...", fontSize = 14.sp) },
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

            // Help List
            if (combinedHelps.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBengali) "কোনো গৃহকর্মী বা বুয়ার তথ্য পাওয়া যায়নি" else "No domestic help listings found",
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
                    items(combinedHelps) { help ->
                        DomesticHelpCard(
                            help = help,
                            isBengali = isBengali,
                            primaryColor = primaryColor,
                            onClick = { onNavigateToDetail(help.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DomesticHelpCard(
    help: DomesticHelpDto,
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
                    Icon(Icons.Default.CleaningServices, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = help.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                    Text(text = "${help.providerName} • ${help.subCategory}", fontSize = 13.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = "💵 ${if (isBengali) "বেতন/মজুরি:" else "Rate:"} ${help.expectedSalary}", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                if (help.workType.isNotBlank()) {
                    Text(text = "🕒 ${help.workType}", fontSize = 12.sp, color = Color(0xFF2563EB), fontWeight = FontWeight.Medium)
                }
            }

            if (help.location.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "📍 ${if (isBengali) "এলাকা:" else "Location:"} ${help.location}", fontSize = 12.sp, color = Color.Gray)
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

                if (help.contact.isNotBlank()) {
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${help.contact}"))
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
