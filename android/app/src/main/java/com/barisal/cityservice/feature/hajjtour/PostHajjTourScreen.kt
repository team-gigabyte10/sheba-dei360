package com.barisal.cityservice.feature.hajjtour

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
import com.barisal.cityservice.data.model.HajjTourDto
import com.barisal.cityservice.data.repository.HajjTourRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostHajjTourScreen(
    initialCategoryKey: String = "hajj",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repo = remember { HajjTourRepository() }

    var categoryKey by remember { mutableStateOf(initialCategoryKey) }
    var title by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var agencyName by remember { mutableStateOf("") }
    var proprietorOrManager by remember { mutableStateOf("") }
    var licenseNo by remember { mutableStateOf("") }
    var packagePrice by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("") }
    var departureLocation by remember { mutableStateOf("") }
    var destination by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf("") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isHajj = categoryKey == "hajj"
    val themeColor = if (isHajj) Color(0xFF047857) else Color(0xFF0284C7)

    val subCategoryOptions = remember(categoryKey, isBengali) {
        if (isHajj) {
            if (isBengali) {
                listOf("হজ প্যাকেজ", "উমরাহ প্যাকেজ", "সৌদি ভিসা ও মেডিকেল", "মাক্কাহ-মদিনা হোটেল ও পরিবহন", "অন্যান্য")
            } else {
                listOf("Hajj Package", "Umrah Package", "Saudi Visa & Medical", "Hotel & Transport", "Others")
            }
        } else {
            if (isBengali) {
                listOf("বিমান টিকিট", "দেশীয় ট্যুর প্যাকেজ", "আন্তর্জাতিক ট্যুর", "ভিসা প্রসেসিং", "অন্যান্য")
            } else {
                listOf("Air Ticket", "Domestic Tour", "International Tour", "Visa Processing", "Others")
            }
        }
    }

    LaunchedEffect(categoryKey) {
        if (subCategory.isBlank() || !subCategoryOptions.contains(subCategory)) {
            subCategory = subCategoryOptions.firstOrNull() ?: ""
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
                title = if (isHajj) {
                    if (isBengali) "হজ ও উমরাহ প্যাকেজ পোস্ট করুন" else "Post Hajj & Umrah Package"
                } else {
                    if (isBengali) "ট্যুর ও ট্রাভেলস প্যাকেজ পোস্ট করুন" else "Post Tour & Travels Package"
                },
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
                        text = if (isBengali) "প্যাকেজ ও এজেন্সির বিবরণ" else "Package & Agency Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )

                    // SubCategory Dropdown
                    ExposedDropdownMenuBox(
                        expanded = subCategoryDropdownExpanded,
                        onExpandedChange = { subCategoryDropdownExpanded = !subCategoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = subCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "উপ-ক্যাটাগরি *" else "Sub Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subCategoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = themeColor,
                                focusedLabelColor = themeColor
                            )
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

                    // Title
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                if (isHajj) {
                                    if (isBengali) "প্যাকেজের নাম / শিরোনাম (যেমন: রাজকীয় উমরাহ প্যাকেজ) *" else "Package Title (e.g. Premium Umrah Package) *"
                                } else {
                                    if (isBengali) "প্যাকেজের নাম / শিরোনাম (যেমন: কক্সবাজার ৩ দিন ২ রাত) *" else "Package Title (e.g. Cox's Bazar 3D/2N) *"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Agency Name & License No
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = agencyName,
                            onValueChange = { agencyName = it },
                            label = { Text(if (isBengali) "এজেন্সির নাম *" else "Agency Name *") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = licenseNo,
                            onValueChange = { licenseNo = it },
                            label = { Text(if (isBengali) "লাইসেন্স নং (RL/Govt)" else "License No") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    // Proprietor / Manager
                    OutlinedTextField(
                        value = proprietorOrManager,
                        onValueChange = { proprietorOrManager = it },
                        label = { Text(if (isBengali) "প্রোপাইটর / পরিচালকের নাম" else "Proprietor / Manager Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Package Price & Duration
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = packagePrice,
                            onValueChange = { packagePrice = it },
                            label = { Text(if (isBengali) "প্যাকেজ মূল্য (যেমন: ১,৫০,০০০ টাকা) *" else "Price *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = duration,
                            onValueChange = { duration = it },
                            label = { Text(if (isBengali) "মেয়াদ (যেমন: ১৪ দিন)" else "Duration") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Departure & Destination
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = departureLocation,
                            onValueChange = { departureLocation = it },
                            label = { Text(if (isBengali) "যাত্রার স্থান (যেমন: ঢাকা/বরিশাল)" else "Departure") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = destination,
                            onValueChange = { destination = it },
                            label = { Text(if (isBengali) "গন্তব্য (যেমন: মক্কা/মদিনা/কক্সবাজার)" else "Destination") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Contact Phone & WhatsApp
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text(if (isBengali) "মোবাইল নম্বর *" else "Contact Phone *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = whatsapp,
                            onValueChange = { whatsapp = it },
                            label = { Text(if (isBengali) "হোয়াটসঅ্যাপ" else "WhatsApp Number") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Office Location
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "অফিসের এলাকা / জেলা (যেমন: সদর, বরিশাল) *" else "Office Location / District *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBengali) "প্যাকেজের বিস্তারিত সুবিধা (হোটেল, খাবার, পরিবহন...)" else "Package Details & Facilities") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4
                    )

                    // Map Location Picker Trigger
                    Card(
                        onClick = { showMapPicker = true },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedLat != null) Icons.Default.LocationOn else Icons.Default.AddLocation,
                                contentDescription = null,
                                tint = if (selectedLat != null) themeColor else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (selectedLat != null) {
                                    if (isBengali) "ম্যাপের স্থানাঙ্ক যুক্ত হয়েছে" else "Map location selected"
                                } else {
                                    if (isBengali) "ম্যাপে অফিসের অবস্থান নির্দিষ্ট করুন" else "Pick office location on Map"
                                },
                                fontSize = 13.sp,
                                color = if (selectedLat != null) themeColor else Color.DarkGray,
                                fontWeight = if (selectedLat != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Banner Photo Selector
                    Text(
                        text = if (isBengali) "প্যাকেজ / ব্যানার ছবি (ঐচ্ছিক)" else "Package / Banner Photo (Optional)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Banner Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(if (isBengali) "ছবি নির্বাচন করতে ট্যাপ করুন" else "Tap to choose photo", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (title.isBlank() || agencyName.isBlank() || packagePrice.isBlank() || contact.isBlank() || location.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill in required fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            coroutineScope.launch {
                                val bannerUrl = if (selectedImageUri != null) {
                                    repo.uploadImageToStorage(context, selectedImageUri!!, "hajj_tours").getOrDefault("")
                                } else ""

                                val itemDto = HajjTourDto(
                                    title = title.trim(),
                                    categoryKey = categoryKey,
                                    subCategory = subCategory,
                                    agencyName = agencyName.trim(),
                                    proprietorOrManager = proprietorOrManager.trim(),
                                    licenseNo = licenseNo.trim(),
                                    packagePrice = packagePrice.trim(),
                                    duration = duration.trim(),
                                    departureLocation = departureLocation.trim(),
                                    destination = destination.trim(),
                                    location = location.trim(),
                                    contact = contact.trim(),
                                    whatsapp = whatsapp.trim(),
                                    description = description.trim(),
                                    bannerImage = bannerUrl,
                                    latitude = selectedLat,
                                    longitude = selectedLng
                                )
                                val result = repo.saveHajjTourPost(itemDto)
                                isSubmitting = false
                                if (result.isSuccess) {
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, if (isBengali) "পোস্ট জমা দিতে ব্যর্থ হয়েছে" else "Failed to submit post", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isSubmitting,
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Text(if (isBengali) "পোস্ট জমা দিন" else "Submit for Approval", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                onBack()
            },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(48.dp)) },
            title = { Text(if (isBengali) "পোস্ট সফলভাবে জমা দেওয়া হয়েছে!" else "Post Submitted Successfully!") },
            text = { Text(if (isBengali) "আপনার প্যাকেজ তথ্য অ্যাডমিন পর্যালোচনার জন্য জমা রাখা হয়েছে। অনুমোদনের পর সেবা তালিকায় দেখাবে।" else "Your post is pending admin review. It will be published after approval.") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A))
                ) {
                    Text(if (isBengali) "ঠিক আছে" else "OK")
                }
            }
        )
    }
}
