package com.barisal.cityservice.feature.training

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.TrainingAcademyDto
import com.barisal.cityservice.data.repository.TrainingAcademyRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostTrainingAcademyScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { TrainingAcademyRepository() }

    val primaryColor = Color(0xFF6D28D9) // Purple theme for education & academies

    var title by remember { mutableStateOf("") }
    var academyName by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "কার ড্রাইভিং" else "Car Driving") }
    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val subCategoriesList = listOf(
        "কার ড্রাইভিং",
        "কম্পিউটার ট্রেনিং",
        "টেকনিক্যাল ট্রেনিং",
        "ভাষা শিক্ষা",
        "চাকরি ও ক্যারিয়ার",
        "অন্যান্য"
    )

    var courseFee by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var trainerName by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }

    var selectedCoverImage by remember { mutableStateOf<Uri?>(null) }

    var selectedLocation by remember { mutableStateOf(LatLng(22.7010, 90.3535)) }
    var showLocationPickerMap by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val coverPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) selectedCoverImage = uri
    }

    if (showLocationPickerMap) {
        LocationPickerMapScreen(
            initialLat = selectedLocation.latitude,
            initialLng = selectedLocation.longitude,
            onLocationSelected = { selectedLat, selectedLng ->
                selectedLocation = LatLng(selectedLat, selectedLng)
                showLocationPickerMap = false
            },
            onBack = { showLocationPickerMap = false }
        )
        return
    }

    if (showSuccessDialog) {
        CustomDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBack()
            },
            title = if (isBengali) "পোস্ট সফল হয়েছে!" else "Post Successful!",
            confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
            onConfirm = {
                showSuccessDialog = false
                onBack()
            }
        ) {
            Text(
                text = if (isBengali) "আপনার পোস্টটি ভেরিফিকেশনের জন্য এডমিনের নিকট জমা দেওয়া হয়েছে। এডমিন ভেরিফাই করার পর পোস্টটি অ্যাপে প্রদর্শিত হবে।" else "Your post has been submitted for admin verification. It will appear after approval.",
                fontSize = 14.sp
            )
        }
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "ট্রেনিং পোস্ট করুন" else "Post Training Course",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = primaryColor.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.School, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBengali) "ট্রেনিং ও কোর্সের বিজ্ঞাপন দিন" else "Post Training & Course Listing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = if (isBengali) "ড্রাইভিং, কম্পিউটার, ভাষা শিক্ষা ইত্যাদি কোর্সের তথ্য দিন" else "Add details and photo for your training course",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Course Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBengali) "কোর্সের নাম / শিরোনাম *" else "Course Title *") },
                    placeholder = { Text(if (isBengali) "যেমন: প্রফেশনাল কার ড্রাইভিং কোর্স" else "e.g. Professional Car Driving Course") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )

                // Academy Name
                OutlinedTextField(
                    value = academyName,
                    onValueChange = { academyName = it },
                    label = { Text(if (isBengali) "একাডেমি / প্রতিষ্ঠানের নাম *" else "Academy Name *") },
                    placeholder = { Text(if (isBengali) "যেমন: বরিশাল ড্রাইভ ফেয়ার একাডেমি" else "e.g. Barisal Driving Academy") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )

                // Sub-Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = subCategoryDropdownExpanded,
                    onExpandedChange = { subCategoryDropdownExpanded = !subCategoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedSubCategory,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "ট্রেনিং ক্যাটাগরি *" else "Training Category *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCategoryDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    ExposedDropdownMenu(
                        expanded = subCategoryDropdownExpanded,
                        onDismissRequest = { subCategoryDropdownExpanded = false }
                    ) {
                        subCategoriesList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(text = cat) },
                                onClick = {
                                    selectedSubCategory = cat
                                    subCategoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Fee & Duration
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = courseFee,
                        onValueChange = { courseFee = it },
                        label = { Text(if (isBengali) "কোর্স ফি (টাকা)" else "Course Fee (BDT)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("৫০০০") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text(if (isBengali) "কোর্সের মেয়াদ" else "Duration") },
                        placeholder = { Text(if (isBengali) "১ মাস" else "1 Month") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                }

                // Trainer Name & Contact Phone
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = trainerName,
                        onValueChange = { trainerName = it },
                        label = { Text(if (isBengali) "প্রশিক্ষক/প্রতিনিধি" else "Trainer Name") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text(if (isBengali) "মোবাইল নম্বর *" else "Contact Phone *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                }

                // Address & Thana
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBengali) "ঠিকানা *" else "Address *") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা/উপজেলা *" else "Thana *") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                }

                // Cover Image Picker
                Text(if (isBengali) "কোর্স/একাডেমির ছবি:" else "Course/Academy Photo:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable { coverPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedCoverImage != null) {
                        AsyncImage(
                            model = selectedCoverImage,
                            contentDescription = "Cover Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = primaryColor, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(if (isBengali) "ছবির কভার সিলেক্ট করুন" else "Tap to add course photo", color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }

                // Location Picker Button
                OutlinedButton(
                    onClick = { showLocationPickerMap = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBengali) "ম্যাপে লোকেশন নির্বাচন করুন" else "Select Location on Map", color = primaryColor)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank() || academyName.isBlank() || contact.isBlank() || address.isBlank()) {
                            Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSubmitting = true
                        coroutineScope.launch {
                            val coverUrl = selectedCoverImage?.let { uri ->
                                repository.uploadImageToStorage(context, uri, "training_academies/covers").getOrNull() ?: ""
                            } ?: ""

                            val dto = TrainingAcademyDto(
                                title = title,
                                academyName = academyName,
                                subCategory = selectedSubCategory,
                                courseFee = courseFee,
                                duration = duration,
                                trainerName = trainerName,
                                contact = contact,
                                address = address,
                                thana = thana,
                                coverImage = coverUrl,
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude
                            )

                            val result = repository.saveTrainingPostToFirestore(dto)
                            isSubmitting = false
                            if (result.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, "Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isBengali) "পোস্ট নিশ্চিত করুন" else "Confirm Post", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
