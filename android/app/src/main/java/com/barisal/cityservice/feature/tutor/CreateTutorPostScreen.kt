package com.barisal.cityservice.feature.tutor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Send
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
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import kotlinx.coroutines.launch

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
    val coroutineScope = rememberCoroutineScope()
    val tutorRepo = remember { TutorRepository() }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdPostTemp by remember { mutableStateOf<TutorProfile?>(null) }

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

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
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
                .imePadding()
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
                                fontSize = 14.sp
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
                                Text(if (isBengali) "👨‍🏫 পড়াতে চাই (Tutor)" else "👨‍🏫 Tutor Available", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
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
                                Text(if (isBengali) "🎓 শিক্ষক চাই (Student)" else "🎓 Tuition Wanted", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                            }
                        },
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
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
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
                                        modifier = Modifier.size(26.dp)
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
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
                    placeholder = { Text(if (isBengali) "যেমন: ঢাকা বিশ্ববিদ্যালয় পদার্থবিজ্ঞান ২য় বর্ষ..." else "e.g., DU Physics 2nd year student...", fontSize = 15.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 5,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Class / Sub-Category Selector
                Text(if (isBengali) "শ্রেণী / ক্যাটাগরি (Class / Category) *" else "Class / Category *", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
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
                        text = if (isBengali) "৯ম-১২দশ শ্রেণীর বিষয়সমূহ নির্বাচন করুন (Subject Checkboxes) *" else "Select Subjects for Class 9+ *",
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
                                (if (isBengali) "পড়ানোর বিষয়সমূহ *" else "Subjects *"),
                            fontSize = 14.sp
                        )
                    },
                    placeholder = { Text(if (isBengali) "যেমন: গণিত, ইংরেজি, পদার্থবিজ্ঞান..." else "e.g., Math, Physics, English...", fontSize = 15.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Days per week & Salary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = daysPerWeek,
                        onValueChange = { daysPerWeek = it },
                        label = { Text(if (isBengali) "দিন / সপ্তাহে" else "Days / Week", fontSize = 14.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    OutlinedTextField(
                        value = salary,
                        onValueChange = { salary = it },
                        label = { Text(if (isBengali) "বেতন *" else "Salary *", fontSize = 14.sp) },
                        placeholder = { Text("৳৪,০০০", fontSize = 15.sp) },
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
                        label = { Text(if (isBengali) "বর্তমান ঠিকানা *" else "Address *", fontSize = 14.sp) },
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা / উপজেলা *" else "Thana *", fontSize = 14.sp) },
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
                // Location Map Picker
                Button(
                    onClick = { showLocationPickerMap = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "আপনার লোকেশন ম্যাপ থেকে সেট করুন" else "Set your location from map",
                        fontSize = 14.sp,
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
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = String.format("লোকেশন সেট করা হয়েছে: %.5f, %.5f", selectedLat, selectedLng),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
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
                                        val uploadResult = tutorRepo.uploadImageToStorage(context, selectedImageUri!!, "tutors/profiles")
                                        profileImageUrl = uploadResult.getOrDefault("")
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
                                        createdPostTemp = newPost
                                        showSuccessDialog = true
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
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (isBengali) "পোস্ট নিশ্চিত করুন" else "Publish Post", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showSuccessDialog) {
            CustomDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    createdPostTemp?.let { onPostCreated(it) }
                    onBack()
                },
                title = if (isBengali) "পোস্ট সফলভাবে জমা হয়েছে!" else "Post Submitted Successfully!",
                icon = Icons.Default.Check,
                iconTint = Color(0xFF16A34A),
                iconBackgroundColor = Color(0xFFDCFCE7),
                confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
                onConfirm = {
                    showSuccessDialog = false
                    createdPostTemp?.let { onPostCreated(it) }
                    onBack()
                }
            ) {
                Text(
                    text = if (isBengali)
                        "আপনার টিউশন পোস্টটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর পোস্টটি প্রকাশিত হবে।"
                    else
                        "Your tutor post has been submitted for admin review. It will be published once approved.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
