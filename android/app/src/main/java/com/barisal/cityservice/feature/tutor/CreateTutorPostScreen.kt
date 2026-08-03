package com.barisal.cityservice.feature.tutor

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTutorPostScreen(
    onBack: () -> Unit,
    onPostCreated: (TutorProfile) -> Unit
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    var postType by remember { mutableStateOf("tutor") } // "tutor" = পড়াতে চাই, "student" = শিক্ষক চাই
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var classRange by remember { mutableStateOf("৬ষ্ঠ–১০ম") }
    var daysPerWeek by remember { mutableStateOf("৪ দিন/সপ্তাহে") }
    var subject by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "নতুন পোস্ট তৈরি করুন" else "Create New Post",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Banner
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFF0F766E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        Column {
                            Text(
                                text = if (isBengali) "টিউশন বা শিক্ষক বিজ্ঞাপন দিন" else "Post Tutor or Tuition Needed Ad",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBengali) "সঠিক ও নির্ভুল তথ্য প্রদান করে ফর্মটি পূরণ করুন" else "Fill in the form with accurate details",
                                color = Color(0xFFCCFBF1),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Post Type Selector
                Text(if (isBengali) "পোস্টের ধরন (Post Type) *" else "Post Type *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = postType == "tutor",
                        onClick = { postType = "tutor" },
                        modifier = Modifier.weight(1f),
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(if (isBengali) "👨‍🏫 পড়াতে চাই (Tutor)" else "👨‍🏫 Tutor Available", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F766E),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = postType == "student",
                        onClick = { postType = "student" },
                        modifier = Modifier.weight(1f),
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(if (isBengali) "🎓 শিক্ষক চাই (Student)" else "🎓 Tuition Wanted", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "আপনার নাম / শিক্ষার্থীর নাম *" else "Name / Student Name *", fontSize = 14.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Bio / Qualification
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(if (isBengali) "যোগ্যতা / সংক্ষিপ্ত বিবরণ *" else "Qualification / Details *", fontSize = 14.sp) },
                    placeholder = { Text(if (isBengali) "যেমন: ঢাকা বিশ্ববিদ্যালয় পদার্থবিজ্ঞান ২য় বর্ষ..." else "e.g., DU Physics 2nd year student...", fontSize = 13.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Subject
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(if (isBengali) "পড়ানোর বিষয়সমূহ *" else "Subjects *", fontSize = 14.sp) },
                    placeholder = { Text(if (isBengali) "যেমন: গণিত, ইংরেজি, পদার্থবিজ্ঞান, রসায়ন..." else "e.g., Math, Physics, English...", fontSize = 13.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Class Range Selector
                Text(if (isBengali) "শ্রেণী (Class Range) *" else "Class Range *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("১ম–৫ম", "৬ষ্ঠ–১০ম", "এইচএসসি", "আরবি/কুরআন").forEach { cls ->
                        FilterChip(
                            selected = classRange == cls,
                            onClick = { classRange = cls },
                            label = { Text(cls, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                        )
                    }
                }

                // Days per week & Salary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text(if (isBengali) "দিন / সপ্তাহে" else "Days / Week", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text(if (isBengali) "বেতন *" else "Salary *", fontSize = 13.sp) },
                        placeholder = { Text("৳৪,০০০", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                // Address & Thana
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBengali) "বর্তমান ঠিকানা *" else "Address *", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা / উপজেলা *" else "Thana *", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBengali) "যোগাযোগের মোবাইল নম্বর *" else "Contact Mobile Number *", fontSize = 14.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )
            }

            // Bottom Fixed Action Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (isBengali) "বাতিল" else "Cancel", fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            if (name.isBlank() || phone.isBlank() || subject.isBlank() || salary.isBlank()) {
                                Toast.makeText(context, if (isBengali) "দয়া করে নাম, বিষয়, বেতন ও ফোন নম্বর লিখুন" else "Please fill required fields", Toast.LENGTH_SHORT).show()
                            } else {
                                val newPost = TutorProfile(
                                    name = name,
                                    date = "আজ",
                                    bio = bio,
                                    classRange = classRange,
                                    daysPerWeek = daysPerWeek,
                                    subject = subject,
                                    salary = if (salary.startsWith("৳")) salary else "৳$salary",
                                    gender = if (postType == "tutor") "ছেলে/মেয়ে" else "শিক্ষক",
                                    address = address,
                                    thana = thana,
                                    phone = phone
                                )
                                onPostCreated(newPost)
                                Toast.makeText(context, if (isBengali) "পোস্ট সফলভাবে প্রকাশিত হয়েছে!" else "Post published successfully!", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBengali) "পোস্ট নিশ্চিত করুন" else "Publish Post", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
