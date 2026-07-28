package com.nayem.sheba_dei.feature.tutor

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PostAdd
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
import com.nayem.sheba_dei.core.language.LocalAppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorPostDialog(
    onDismissRequest: () -> Unit,
    onPostCreated: (TutorProfile) -> Unit
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    var postType by remember { mutableStateOf("tutor") } // "tutor" = পড়া তে চাই, "student" = শিক্ষক চাই
    var name by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var classRange by remember { mutableStateOf("৬ষ্ঠ–১০ম") }
    var daysPerWeek by remember { mutableStateOf("৪ দিন/সপ্তাহে") }
    var subject by remember { mutableStateOf("") }
    var salary by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = Color(0xFF0F766E), modifier = Modifier.size(26.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "নতুন পোস্ট তৈরি করুন" else "Create New Post",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Post Type Selector
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = postType == "tutor",
                        onClick = { postType = "tutor" },
                        label = { Text(if (isBengali) "👨‍🏫 পড়াতে চাই (Tutor)" else "👨‍🏫 Tutor Available", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F766E),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = postType == "student",
                        onClick = { postType = "student" },
                        label = { Text(if (isBengali) "🎓 শিক্ষক চাই (Student)" else "🎓 Tuition Wanted", fontSize = 13.sp, fontWeight = FontWeight.Medium) },
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
                    label = { Text(if (isBengali) "আপনার নাম/শিক্ষার্থীর নাম *" else "Name *", fontSize = 14.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Bio / Qualification
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text(if (isBengali) "যোগ্যতা/সংক্ষিপ্ত বিবরণ *" else "Bio/Qualification *", fontSize = 14.sp) },
                    placeholder = { Text(if (isBengali) "যেমন: ঢাকা বিশ্ববিদ্যালয় পদার্থবিজ্ঞান ২য় বর্ষ..." else "e.g., DU Physics 2nd year...", fontSize = 13.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Subject
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text(if (isBengali) "বিষয়সমূহ *" else "Subjects *", fontSize = 14.sp) },
                    placeholder = { Text(if (isBengali) "যেমন: গণিত, ইংরেজি, সায়েন্স..." else "e.g., Math, Physics...", fontSize = 13.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Class Range Selector
                Text(if (isBengali) "শ্রেণী (Class):" else "Class:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("১ম–৫ম", "৬ষ্ঠ–১০ম", "এইচএসসি", "আরবি").forEach { cls ->
                        FilterChip(
                            selected = classRange == cls,
                            onClick = { classRange = cls },
                            label = { Text(cls, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                        )
                    }
                }

                // Days per week & Salary
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text(if (isBengali) "দিন/সপ্তাহ" else "Days/Wk", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text(if (isBengali) "বেতন *" else "Salary *", fontSize = 13.sp) },
                        placeholder = { Text("৳৪,০০০", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Address & Thana
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBengali) "ঠিকানা *" else "Address *", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা *" else "Thana *", fontSize = 13.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBengali) "যোগাযোগের ফোন নম্বর *" else "Contact Phone *", fontSize = 14.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )
            }
        },
        confirmButton = {
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
                        Toast.makeText(context, if (isBengali) "পোস্ট সফলভাবে প্রকাশিত হয়েছে!" else "Post created successfully!", Toast.LENGTH_LONG).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
            ) {
                Text(if (isBengali) "পোস্ট নিশ্চিত করুন" else "Publish Post", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(if (isBengali) "বাতিল" else "Cancel", fontSize = 14.sp)
            }
        }
    )
}
