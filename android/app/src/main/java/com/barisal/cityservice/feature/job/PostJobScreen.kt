package com.barisal.cityservice.feature.job

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.JobDto
import com.barisal.cityservice.data.repository.JobRepository
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostJobScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val jobRepo = remember { JobRepository() }

    var title by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf(if (isBengali) "প্রতিষ্ঠানে চাকরি" else "Protisthan") }
    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var organizationName by remember { mutableStateOf("") }
    var jobType by remember { mutableStateOf(if (isBengali) "ফুল টাইম" else "Full Time") }
    var salary by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var educationalRequirement by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf("") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF0F766E)

    val subCategoryOptions = remember(isBengali) {
        if (isBengali) {
            listOf("প্রতিষ্ঠানে চাকরি", "শো-রুমে চাকরি", "দোকানে চাকরি", "অন্যান্য চাকরি")
        } else {
            listOf("Protisthan", "Shoroom", "Dokan", "Others")
        }
    }

    val jobTypeOptions = remember(isBengali) {
        if (isBengali) {
            listOf("ফুল টাইম", "পার্ট টাইম", "চুক্তিভিত্তিক")
        } else {
            listOf("Full Time", "Part Time", "Contractual")
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    if (showMapPicker) {
        LocationPickerMapScreen(
            initialLat = selectedLat,
            initialLng = selectedLng,
            onLocationSelected = { lat, lng ->
                selectedLat = lat
                selectedLng = lng
                showMapPicker = false
                Toast.makeText(context, if (isBengali) "অবস্থান নির্বাচন করা হয়েছে" else "Location selected", Toast.LENGTH_SHORT).show()
            },
            onBack = { showMapPicker = false }
        )
        return
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "চাকরি পোস্ট করুন" else "Post Job Circular",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isBengali) "চাকরির বিস্তারিত তথ্য দিন" else "Enter Job Circular Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Job Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isBengali) "পদের নাম (যেমন: কম্পিউটার অপারেটর) *" else "Job Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Sub Category Dropdown
                    ExposedDropdownMenuBox(
                        expanded = subCategoryDropdownExpanded,
                        onExpandedChange = { subCategoryDropdownExpanded = !subCategoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = subCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "ক্যাটাগরি *" else "Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCategoryDropdownExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                        )
                        ExposedDropdownMenu(
                            expanded = subCategoryDropdownExpanded,
                            onDismissRequest = { subCategoryDropdownExpanded = false }
                        ) {
                            subCategoryOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(text = option) },
                                    onClick = {
                                        subCategory = option
                                        subCategoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Organization Name
                    OutlinedTextField(
                        value = organizationName,
                        onValueChange = { organizationName = it },
                        label = { Text(if (isBengali) "প্রতিষ্ঠানের নাম *" else "Company / Organization Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Job Type Chips
                    Text(text = if (isBengali) "চাকরির ধরন *" else "Job Type *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        jobTypeOptions.forEach { option ->
                            FilterChip(
                                selected = jobType == option,
                                onClick = { jobType = option },
                                label = { Text(option, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Salary & Contact
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = salary,
                            onValueChange = { salary = it },
                            label = { Text(if (isBengali) "বেতন (যেমন: ১৫০০০)" else "Salary") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text(if (isBengali) "যোগাযোগের ফোন *" else "Contact Phone *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Experience & Qualification
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text(if (isBengali) "অভিজ্ঞতা" else "Experience") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = educationalRequirement,
                            onValueChange = { educationalRequirement = it },
                            label = { Text(if (isBengali) "শিক্ষাগত যোগ্যতা" else "Education") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Location & Deadline
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "কর্মস্থল / ঠিকানা *" else "Work Location *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = deadline,
                        onValueChange = { deadline = it },
                        label = { Text(if (isBengali) "আবেদনের শেষ তারিখ" else "Application Deadline") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Map location picker button
                    OutlinedButton(
                        onClick = { showMapPicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedLat != null) (if (isBengali) "ম্যাপে অবস্থান নির্বাচিত 📍" else "Location Selected 📍") else (if (isBengali) "ম্যাপ থেকে নিখুঁত অবস্থান নির্বাচন করুন" else "Pick Map Location"),
                            color = primaryColor
                        )
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBengali) "চাকরির বিস্তারিত শর্তাবলী ও বিবরণ" else "Job Requirements & Description") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )

                    // Cover Image Attachment
                    Text(text = if (isBengali) "ছবি সংযুক্ত করুন (ঐচ্ছিক)" else "Attach Banner Image (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Job Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(if (isBengali) "ছবি আপলোড করতে ট্যাপ করুন" else "Tap to upload banner image", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (title.isBlank() || organizationName.isBlank() || contact.isBlank() || location.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সব তথ্য দিন" else "Please fill mandatory fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmitting = true
                    coroutineScope.launch {
                        val coverUrl = if (selectedImageUri != null) {
                            jobRepo.uploadImageToStorage(context, selectedImageUri!!, "jobs/covers").getOrDefault("")
                        } else ""

                        val job = JobDto(
                            title = title,
                            subCategory = subCategory,
                            organizationName = organizationName,
                            jobType = jobType,
                            salary = salary,
                            contact = contact,
                            experience = experience,
                            educationalRequirement = educationalRequirement,
                            location = location,
                            description = description,
                            deadline = deadline,
                            coverImage = coverUrl,
                            latitude = selectedLat,
                            longitude = selectedLng,
                            isApproved = false
                        )
                        val res = jobRepo.saveJob(job)
                        isSubmitting = false
                        if (res.isSuccess) {
                            showSuccessDialog = true
                        } else {
                            Toast.makeText(context, "Error: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(10.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                } else {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBengali) "পোস্ট জমা দিন" else "Submit Job", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            icon = { Icon(Icons.Default.HourglassTop, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp)) },
            title = { Text(if (isBengali) "পোস্ট জমা হয়েছে" else "Post Submitted", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (isBengali) "আপনার পোস্টটি এডমিন ভেরিফিকেশনের জন্য জমা দেওয়া হয়েছে। এডমিন ভেরিফাই করার পর পোস্টটি অ্যাপে প্রদর্শিত হবে।"
                    else "Your post has been submitted for admin verification. It will appear after approval."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Text(if (isBengali) "ঠিক আছে" else "OK")
                }
            }
        )
    }
}
