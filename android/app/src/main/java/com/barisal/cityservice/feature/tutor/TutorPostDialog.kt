package com.barisal.cityservice.feature.tutor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.TutorDto
import com.barisal.cityservice.data.repository.TutorRepository
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorPostDialog(
    onDismissRequest: () -> Unit,
    onPostCreated: (TutorProfile) -> Unit
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val tutorRepo = remember { TutorRepository() }

    var isSubmitting by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    val selectedSubjectCheckboxes = remember { mutableStateListOf<String>() }
    val availableSubjects = remember {
        listOf(
            "পদার্থবিজ্ঞান",
            "রসায়ন",
            "উচ্চতর গণিত",
            "সাধারণ গণিত",
            "জীববিজ্ঞান",
            "আইসিটি",
            "ইংরেজি",
            "বাংলা",
            "হিসাববিজ্ঞান",
            "ফিন্যান্স ও ব্যাংকিং",
            "অর্থনীতি",
            "নৃত্য শিক্ষক",
            "সঙ্গীত শিক্ষক"
        )
    }

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
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showLocationPickerMap by remember { mutableStateOf(false) }

    if (showLocationPickerMap) {
        LocationPickerMapScreen(
            initialLat = selectedLat,
            initialLng = selectedLng,
            onLocationSelected = { lat, lng ->
                selectedLat = lat
                selectedLng = lng
                showLocationPickerMap = false
            },
            onBack = { showLocationPickerMap = false }
        )
        return
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.imePadding(),
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
                        label = { Text(if (isBengali) "👨‍🏫 পড়াতে চাই (Tutor)" else "👨‍🏫 Tutor Available", fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F766E),
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = postType == "student",
                        onClick = { postType = "student" },
                        label = { Text(if (isBengali) "🎓 শিক্ষক চাই (Student)" else "🎓 Tuition Wanted", fontSize = 15.sp, fontWeight = FontWeight.Medium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF2563EB),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Profile Picture Picker (Tutor Only)
                if (postType == "tutor") {
                    Text(
                        text = if (isBengali) "প্রোফাইল ছবি (Profile Picture)" else "Profile Picture",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE2E8F0))
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (selectedImageUri != null) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Select Photo",
                                        tint = Color(0xFF0F766E),
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "ছবি যোগ করুন" else "Add Photo",
                                        fontSize = 13.sp,
                                        color = Color(0xFF0F766E),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
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
                    placeholder = { Text(if (isBengali) "যেমন: ঢাকা বিশ্ববিদ্যালয় পদার্থবিজ্ঞান ২য় বর্ষ..." else "e.g., DU Physics 2nd year...", fontSize = 15.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4
                )

                // Class / Sub-Category Selector
                Text(if (isBengali) "শ্রেণী / ক্যাটাগরি (Class / Category):" else "Class / Category:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                @OptIn(ExperimentalLayoutApi::class)
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    listOf("১ম–৫ম", "৬ষ্ঠ–৮ম", "৯ম–১০ম", "এইচএসসি", "আরবি/কুরআন", "নৃত্য শিক্ষক", "সঙ্গীত শিক্ষক").forEach { cls ->
                        FilterChip(
                            selected = classRange == cls,
                            onClick = { classRange = cls },
                            label = { Text(cls, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF0F766E),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                // Subject Selection for Class 9+ (৯ম–১০ম & এইচএসসি)
                if (classRange == "৯ম–১০ম" || classRange == "এইচএসসি") {
                    Text(
                        text = if (isBengali) "৯ম-১২দশ শ্রেণীর বিষয়সমূহ (Subject Checkboxes) *" else "Select Subjects for Class 9+ *",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F766E)
                    )
                    @OptIn(ExperimentalLayoutApi::class)
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        availableSubjects.forEach { sub ->
                            val isSelected = selectedSubjectCheckboxes.contains(sub)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    if (isSelected) {
                                        selectedSubjectCheckboxes.remove(sub)
                                    } else {
                                        selectedSubjectCheckboxes.add(sub)
                                    }
                                },
                                label = { Text(sub, fontSize = 14.sp, fontWeight = FontWeight.Medium) },
                                leadingIcon = if (isSelected) {
                                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                                } else null,
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF0F766E),
                                    selectedLabelColor = Color.White,
                                    selectedLeadingIconColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Subject / Additional Subject
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = {
                        Text(
                            if (classRange == "৯ম–১০ম" || classRange == "এইচএসসি")
                                (if (isBengali) "অন্যান্য বিষয় (যদি থাকে)" else "Additional Subjects")
                            else
                                (if (isBengali) "বিষয়সমূহ *" else "Subjects *"),
                            fontSize = 14.sp
                        )
                    },
                    placeholder = { Text(if (isBengali) "যেমন: গণিত, ইংরেজি, পদার্থবিজ্ঞান..." else "e.g., Math, Physics...", fontSize = 15.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Days per week & Salary
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text(if (isBengali) "দিন/সপ্তাহ" else "Days/Wk", fontSize = 14.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text(if (isBengali) "বেতন *" else "Salary *", fontSize = 14.sp) },
                        placeholder = { Text("৳৪,০০০", fontSize = 15.sp) },
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
                        label = { Text(if (isBengali) "ঠিকানা *" else "Address *", fontSize = 14.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা *" else "Thana *", fontSize = 14.sp) },
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

                // Location Map Picker
                Button(
                    onClick = { showLocationPickerMap = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "আপনার লোকেশন ম্যাপ থেকে সেট করুন" else "Set location from map",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (selectedLat != null && selectedLng != null) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFDCFCE7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = String.format("লোকেশন সেট: %.5f, %.5f", selectedLat, selectedLng),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }
        },
        confirmButton = {
            Button(
                enabled = !isSubmitting,
                onClick = {
                    val effectiveSubjectList = if (classRange == "৯ম–১০ম" || classRange == "এইচএসসি") {
                        val list = selectedSubjectCheckboxes.toList().toMutableList()
                        if (subject.isNotBlank() && !list.contains(subject.trim())) {
                            list.add(subject.trim())
                        }
                        list
                    } else {
                        if (subject.isNotBlank()) listOf(subject.trim()) else emptyList()
                    }

                    val finalSubjectString = if (effectiveSubjectList.isNotEmpty()) {
                        effectiveSubjectList.joinToString(", ")
                    } else {
                        if (subject.isNotBlank()) subject else "সকল বিষয়"
                    }

                    if (name.isBlank() || phone.isBlank() || finalSubjectString.isBlank() || salary.isBlank()) {
                        Toast.makeText(context, if (isBengali) "দয়া করে নাম, বিষয়, বেতন ও ফোন নম্বর লিখুন" else "Please fill required fields", Toast.LENGTH_SHORT).show()
                    } else {
                        isSubmitting = true
                        val formattedSalary = if (salary.startsWith("৳")) salary else "৳$salary"

                        coroutineScope.launch {
                            var profileImageUrl = ""
                            if (postType == "tutor" && selectedImageUri != null) {
                                val compressResult = tutorRepo.compressImageToBase64(context, selectedImageUri!!)
                                profileImageUrl = compressResult.getOrDefault("")
                            }

                            val newPost = TutorProfile(
                                name = name,
                                date = "আজ",
                                bio = bio,
                                classRange = classRange,
                                daysPerWeek = daysPerWeek,
                                subject = finalSubjectString,
                                selectedSubjects = effectiveSubjectList,
                                salary = formattedSalary,
                                gender = if (postType == "tutor") "ছেলে/মেয়ে" else "শিক্ষক",
                                address = address,
                                thana = thana,
                                phone = phone,
                                profileImageUrl = profileImageUrl,
                                postType = postType
                            )

                            val dto = TutorDto(
                                name = name,
                                date = "আজ",
                                bio = bio,
                                classRange = classRange,
                                daysPerWeek = daysPerWeek,
                                subject = finalSubjectString,
                                selectedSubjects = effectiveSubjectList,
                                salary = formattedSalary,
                                gender = if (postType == "tutor") "ছেলে/মেয়ে" else "শিক্ষক",
                                address = address,
                                thana = thana,
                                phone = phone,
                                profileImageUrl = profileImageUrl,
                                postType = postType
                            )

                            val result = tutorRepo.saveTutorPostToFirestore(dto)
                            isSubmitting = false
                            if (result.isSuccess) {
                                onPostCreated(newPost)
                                Toast.makeText(
                                    context,
                                    if (isBengali) "পোস্ট জমা দেওয়া হয়েছে! এডমিন অনুমোদনের পর পোস্টটি প্রকাশিত হবে।" else "Post submitted! It will be published after admin approval.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onDismissRequest()
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "পোস্ট প্রকাশ করতে সমস্যা হয়েছে: ${result.exceptionOrNull()?.localizedMessage}" else "Failed to publish post",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (isBengali) "পোস্ট নিশ্চিত করুন" else "Publish Post", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(if (isBengali) "বাতিল" else "Cancel", fontSize = 14.sp)
            }
        }
    )
}
