package com.barisal.cityservice.feature.domestichelp

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
import com.barisal.cityservice.data.model.DomesticHelpDto
import com.barisal.cityservice.data.repository.DomesticHelpRepository
import com.barisal.cityservice.feature.location.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDomesticHelpScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val domesticHelpRepo = remember { DomesticHelpRepository() }

    var title by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf(if (isBengali) "ফুল-টাইম গৃহকর্মী" else "Full-Time Maid") }
    var providerName by remember { mutableStateOf("") }
    var workType by remember { mutableStateOf(if (isBengali) "মাসিক" else "Monthly") }
    var expectedSalary by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf("") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFFD97706)

    val subCategoryOptions = remember(isBengali) {
        if (isBengali) {
            listOf("ফুল-টাইম গৃহকর্মী", "পার্ট-টাইম গৃহকর্মী", "রান্নার বুয়া", "শিশু দেখাশোনা", "বয়স্ক সেবা", "বাসা পরিষ্কার", "অন্যান্য")
        } else {
            listOf("Full-Time Maid", "Part-Time Maid", "Cook / Chef", "Baby Sitter", "Elderly Care", "House Cleaner", "Others")
        }
    }

    val workTypeOptions = remember(isBengali) {
        if (isBengali) {
            listOf("মাসিক", "দৈনিক", "ঘণ্টা চুক্তি")
        } else {
            listOf("Monthly", "Daily", "Hourly")
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            val compResult = domesticHelpRepo.compressImageToBase64(context, uri)
            if (compResult.isSuccess) {
                base64Image = compResult.getOrDefault("")
            }
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
                title = if (isBengali) "গৃহকর্মী/বুয়া পোস্ট করুন" else "Post Domestic Help",
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
                        text = if (isBengali) "গৃহকর্মী/বুয়ার তথ্য দিন" else "Enter Domestic Help Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text(if (isBengali) "পোস্টের শিরোনাম (যেমন: অভিজ্ঞ রান্নার বুয়া চাই/প্রাপ্য) *" else "Title *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Sub Category Chips / Selection
                    Text(text = if (isBengali) "ক্যাটাগরি *" else "Service Type *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        subCategoryOptions.chunked(3).forEach { chunk ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                chunk.forEach { option ->
                                    FilterChip(
                                        selected = subCategory == option,
                                        onClick = { subCategory = option },
                                        label = { Text(option, fontSize = 11.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = primaryColor,
                                            selectedLabelColor = Color.White
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Provider / Worker Name
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text(if (isBengali) "কর্মীর নাম / এজেন্সির নাম *" else "Worker Name / Agency Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Work Type Chips
                    Text(text = if (isBengali) "কাজের ধরন *" else "Work Type *", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        workTypeOptions.forEach { option ->
                            FilterChip(
                                selected = workType == option,
                                onClick = { workType = option },
                                label = { Text(option, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    // Expected Salary & Contact
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = expectedSalary,
                            onValueChange = { expectedSalary = it },
                            label = { Text(if (isBengali) "প্রত্যাশিত বেতন/মজুরি *" else "Expected Rate *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text(if (isBengali) "যোগাযোগের নম্বর *" else "Contact Number *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Experience & Location
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text(if (isBengali) "অভিজ্ঞতা" else "Experience") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (isBengali) "এলাকা/ঠিকানা *" else "Location *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

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
                        label = { Text(if (isBengali) "কাজের বিবরণ ও বিশেষ দক্ষতা" else "Service Description & Skills") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        maxLines = 4
                    )

                    // Profile / Service Image Attachment
                    Text(text = if (isBengali) "ছবি আপলোড করুন (ঐচ্ছিক)" else "Upload Image (Optional)", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color.Gray)
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
                                contentDescription = "Maid Image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(if (isBengali) "ছবি আপলোড করতে ট্যাপ করুন" else "Tap to upload image", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (title.isBlank() || providerName.isBlank() || contact.isBlank() || expectedSalary.isBlank() || location.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সব জরুরি তথ্য দিন" else "Please fill mandatory fields", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    isSubmitting = true
                    coroutineScope.launch {
                        val help = DomesticHelpDto(
                            title = title,
                            subCategory = subCategory,
                            providerName = providerName,
                            workType = workType,
                            expectedSalary = expectedSalary,
                            contact = contact,
                            experience = experience,
                            location = location,
                            description = description,
                            coverImage = base64Image,
                            latitude = selectedLat,
                            longitude = selectedLng,
                            isApproved = false
                        )
                        val res = domesticHelpRepo.saveDomesticHelp(help)
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
                    Text(if (isBengali) "পোস্ট জমা দিন" else "Submit Domestic Help", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
