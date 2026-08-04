package com.barisal.cityservice.feature.matrimony

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatrimonyProfileScreen(
    profileId: String,
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    val profile = mockProfiles.find { it.id == profileId } ?: mockProfiles.first()
    
    // Rich Crimson Rose Maroon Palette
    val matrimonyPrimary = Color(0xFFBE123C)
    val matrimonyDark = Color(0xFF881337)
    val matrimonyLightBg = Color(0xFFFFF1F2)
    val matrimonyBorder = Color(0xFFFECDD3)

    var isBookmarked by remember { mutableStateOf(false) }
    var interestSent by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বায়োডাটা বিবরণ" else "Biodata Profile",
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                tonalElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "বায়োডাটা: ${profile.name}, ${profile.profession}, ${profile.location}. ShebaDEI 360 অ্যাপে দেখুন।")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "শেয়ার করুন"))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, matrimonyPrimary),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = matrimonyPrimary)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isBengali) "শেয়ার করুন" else "Share",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = { interestSent = !interestSent },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (interestSent) Color(0xFF15803D) else matrimonyPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (interestSent) Icons.Default.Check else Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (interestSent)
                                (if (isBengali) "আগ্রহ পাঠানো হয়েছে" else "Interest Sent")
                            else
                                (if (isBengali) "আগ্রহ জানান" else "Express Interest"),
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .verticalScroll(rememberScrollState())
        ) {
            // Header Hero Banner Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(matrimonyDark)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(104.dp)
                            .clip(CircleShape)
                            .border(3.dp, Color.White, CircleShape)
                            .background(matrimonyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (profile.gender == "Female") Icons.Default.Female else Icons.Default.Male,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = profile.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = "Verified",
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${profile.profession} | ${profile.location}",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (isBengali) "${profile.matchPercentage}% ম্যাচ" else "${profile.matchPercentage}% Match",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            )
                        }

                        Surface(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.clickable { isBookmarked = !isBookmarked }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBookmarked) (if (isBengali) "সংরক্ষিত" else "Saved") else (if (isBengali) "সংরক্ষণ" else "Save"),
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Section 1: Personal Profile
                BiodataSectionCard(
                    title = if (isBengali) "ব্যক্তিগত ও শারীরিক তথ্য" else "Personal & Physical Details",
                    icon = Icons.Default.Person,
                    headerColor = matrimonyPrimary
                ) {
                    BiodataDetailGrid(
                        items = listOf(
                            BiodataItem(if (isBengali) "বয়স" else "Age", "${profile.age} ${if (isBengali) "বছর" else "yrs"}"),
                            BiodataItem(if (isBengali) "উচ্চতা" else "Height", profile.height),
                            BiodataItem(if (isBengali) "ওজন" else "Weight", profile.weight),
                            BiodataItem(if (isBengali) "গাত্রবর্ণ" else "Complexion", profile.complexion),
                            BiodataItem(if (isBengali) "রক্তের গ্রুপ" else "Blood Group", profile.bloodGroup),
                            BiodataItem(if (isBengali) "বৈবাহিক অবস্থা" else "Marital Status", profile.maritalStatus),
                            BiodataItem(if (isBengali) "ধর্ম" else "Religion", profile.religion),
                            BiodataItem(if (isBengali) "উপদল/মাযহাব" else "Sect", profile.sect)
                        ),
                        themeColor = matrimonyPrimary,
                        bgColor = matrimonyLightBg,
                        borderColor = matrimonyBorder
                    )
                }

                // Section 2: Education & Career
                BiodataSectionCard(
                    title = if (isBengali) "শিক্ষাগত ও পেশাগত তথ্য" else "Education & Profession",
                    icon = Icons.Default.School,
                    headerColor = matrimonyPrimary
                ) {
                    BiodataDetailGrid(
                        items = listOf(
                            BiodataItem(if (isBengali) "শিক্ষাগত যোগ্যতা" else "Education", profile.education),
                            BiodataItem(if (isBengali) "শিক্ষা প্রতিষ্ঠান" else "Institute", profile.institute),
                            BiodataItem(if (isBengali) "পেশা" else "Profession", profile.profession),
                            BiodataItem(if (isBengali) "মাসিক আয়" else "Monthly Income", profile.monthlyIncome)
                        ),
                        themeColor = matrimonyPrimary,
                        bgColor = matrimonyLightBg,
                        borderColor = matrimonyBorder
                    )
                }

                // Section 3: Family & Address
                BiodataSectionCard(
                    title = if (isBengali) "পারিবারিক ও ঠিকানার তথ্য" else "Family & Address Details",
                    icon = Icons.Default.HomeWork,
                    headerColor = matrimonyPrimary
                ) {
                    BiodataDetailGrid(
                        items = listOf(
                            BiodataItem(if (isBengali) "বর্তমান ঠিকানা" else "Present Address", profile.location),
                            BiodataItem(if (isBengali) "স্থায়ী ঠিকানা" else "Permanent Address", profile.permanentAddress),
                            BiodataItem(if (isBengali) "পিতার পেশা" else "Father's Profession", profile.fatherOccupation),
                            BiodataItem(if (isBengali) "মাতার পেশা" else "Mother's Profession", profile.motherOccupation),
                            BiodataItem(if (isBengali) "ভাই-বোন" else "Siblings", profile.siblings)
                        ),
                        themeColor = matrimonyPrimary,
                        bgColor = matrimonyLightBg,
                        borderColor = matrimonyBorder
                    )
                }

                // Section 4: Partner Expectation
                BiodataSectionCard(
                    title = if (isBengali) "জীবনসঙ্গী সম্পর্কিত প্রত্যাশা" else "Partner Expectations",
                    icon = Icons.Default.Favorite,
                    headerColor = matrimonyPrimary
                ) {
                    BiodataDetailGrid(
                        items = listOf(
                            BiodataItem(if (isBengali) "প্রত্যাশিত বয়স" else "Expected Age", profile.partnerExpectationAge),
                            BiodataItem(if (isBengali) "প্রত্যাশিত উচ্চতা" else "Expected Height", profile.partnerExpectationHeight),
                            BiodataItem(if (isBengali) "প্রত্যাশিত শিক্ষা" else "Expected Education", profile.partnerExpectationEducation),
                            BiodataItem(if (isBengali) "প্রত্যাশিত পেশা" else "Expected Profession", profile.partnerExpectationProfession),
                            BiodataItem(if (isBengali) "প্রত্যাশিত এলাকা" else "Expected Location", profile.partnerExpectationLocation)
                        ),
                        themeColor = matrimonyPrimary,
                        bgColor = matrimonyLightBg,
                        borderColor = matrimonyBorder
                    )
                }

                // Section 5: Personal Statement
                BiodataSectionCard(
                    title = if (isBengali) "নিজের সম্পর্কে ও জীবনদর্শন" else "About Myself",
                    icon = Icons.Default.FormatQuote,
                    headerColor = matrimonyPrimary
                ) {
                    Text(
                        text = profile.about,
                        color = Color.DarkGray,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(matrimonyLightBg)
                            .border(1.dp, matrimonyBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

data class BiodataItem(val label: String, val value: String)

@Composable
fun BiodataSectionCard(
    title: String,
    icon: ImageVector,
    headerColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(headerColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = headerColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
fun BiodataDetailGrid(
    items: List<BiodataItem>,
    themeColor: Color,
    bgColor: Color,
    borderColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bgColor)
                            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = item.label,
                                color = Color.Gray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.value,
                                color = Color.Black,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}
