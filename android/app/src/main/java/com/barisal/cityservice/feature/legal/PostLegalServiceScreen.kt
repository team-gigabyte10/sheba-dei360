package com.barisal.cityservice.feature.legal

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
import com.barisal.cityservice.data.model.LegalServiceDto
import com.barisal.cityservice.data.repository.LegalServiceRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostLegalServiceScreen(
    initialCategoryKey: String = "legal",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val legalRepo = remember { LegalServiceRepository() }

    var categoryKey by remember { mutableStateOf(initialCategoryKey) } // "legal" or "deed_amin"
    var title by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var chamberOrOffice by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("") }
    var feeInfo by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf("") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isLegal = categoryKey == "legal"
    val themeColor = if (isLegal) Color(0xFF1E293B) else Color(0xFFD97706)

    val subCategoryOptions = remember(categoryKey, isBengali) {
        if (isLegal) {
            if (isBengali) {
                listOf("অ্যাডভোকেট ও আইনজীবী", "দেওয়ানী মামলা", "ফৌজদারী মামলা", "ইনকাম ট্যাক্স ও ভ্যাট", "নোটারী পাবলিক", "অন্যান্য")
            } else {
                listOf("Advocate & Lawyer", "Civil Cases", "Criminal Cases", "Tax & VAT", "Notary Public", "Others")
            }
        } else {
            if (isBengali) {
                listOf("দলিল লেখক", "আমিন / জমি পরিমাপক", "জমি রেজিস্ট্রেশন কনসালট্যান্ট", "অন্যান্য")
            } else {
                listOf("Deed Writer", "Land Surveyor (Amin)", "Land Registry Consultant", "Others")
            }
        }
    }

    // Set default subCategory on load or category switch
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
                title = if (isLegal) {
                    if (isBengali) "আইনি সেবার তথ্য জমা দিন" else "Post Legal Service"
                } else {
                    if (isBengali) "দলিল লেখক ও আমিন সেবার তথ্য জমা দিন" else "Post Deed Writer / Amin Service"
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
                        text = if (isBengali) "সেবার বিস্তারিত তথ্য দিন" else "Enter Service & Profile Details",
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

                    // Title / Headline
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = {
                            Text(
                                if (isLegal) {
                                    if (isBengali) "শিরোনাম (যেমন: হাইকোর্টের অভিজ্ঞ আইনজীবী) *" else "Title (e.g. High Court Advocate) *"
                                } else {
                                    if (isBengali) "শিরোনাম (যেমন: রেজিস্ট্রি সনদপ্রাপ্ত দলিল লেখক) *" else "Title (e.g. Certified Deed Writer) *"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (isBengali) "আপনার/প্রতিষ্ঠানের নাম *" else "Practitioner / Full Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Designation
                    OutlinedTextField(
                        value = designation,
                        onValueChange = { designation = it },
                        label = {
                            Text(
                                if (isLegal) {
                                    if (isBengali) "পদবী / ডিগ্রি (যেমন: অ্যাডভোকেট, জজ কোর্ট) *" else "Designation (e.g. Advocate, Judge Court) *"
                                } else {
                                    if (isBengali) "পদবী (যেমন: দলিল লেখক / জমি পরিমাপক) *" else "Designation (e.g. Deed Writer / Amin) *"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Chamber / Office Address
                    OutlinedTextField(
                        value = chamberOrOffice,
                        onValueChange = { chamberOrOffice = it },
                        label = { Text(if (isBengali) "চেম্বার / অফিসের ঠিকানা *" else "Chamber / Office Address *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Experience & Fee Info
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = experience,
                            onValueChange = { experience = it },
                            label = { Text(if (isBengali) "অভিজ্ঞতা (যেমন: ১০ বছর)" else "Experience") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = feeInfo,
                            onValueChange = { feeInfo = it },
                            label = { Text(if (isBengali) "ফি / খরচ" else "Fee Information") },
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

                    // Location
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "এলাকা / জেলা (যেমন: সদর, বরিশাল) *" else "Area / District *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Description / Details
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBengali) "বিস্তারিত বিবরণ ও বিশেষ সেবা" else "Detailed Description & Expertise") },
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
                                    if (isBengali) "ম্যাপে চেম্বার/অফিসের অবস্থান নির্দিষ্ট করুন" else "Pick chamber/office location on Map"
                                },
                                fontSize = 13.sp,
                                color = if (selectedLat != null) themeColor else Color.DarkGray,
                                fontWeight = if (selectedLat != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Profile Photo Selector
                    Text(
                        text = if (isBengali) "প্রোফাইল/চেম্বার ছবি (ঐচ্ছিক)" else "Profile / Chamber Photo (Optional)",
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
                                contentDescription = "Profile Photo",
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
                            if (title.isBlank() || name.isBlank() || designation.isBlank() || chamberOrOffice.isBlank() || contact.isBlank() || location.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill in required fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            coroutineScope.launch {
                                val profileImageUrl = if (selectedImageUri != null) {
                                    legalRepo.uploadImageToStorage(context, selectedImageUri!!, "legal_services").getOrDefault("")
                                } else ""

                                val serviceDto = LegalServiceDto(
                                    title = title.trim(),
                                    categoryKey = categoryKey,
                                    subCategory = subCategory,
                                    name = name.trim(),
                                    designation = designation.trim(),
                                    chamberOrOffice = chamberOrOffice.trim(),
                                    experience = experience.trim(),
                                    feeInfo = feeInfo.trim(),
                                    location = location.trim(),
                                    contact = contact.trim(),
                                    whatsapp = whatsapp.trim(),
                                    description = description.trim(),
                                    profileImage = profileImageUrl,
                                    latitude = selectedLat,
                                    longitude = selectedLng
                                )
                                val result = legalRepo.saveLegalService(serviceDto)
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
            text = { Text(if (isBengali) "আপনার তথ্য অ্যাডমিন পর্যালোচনার জন্য জমা রাখা হয়েছে। অনুমোদনের পর সেবা তালিকায় দেখাবে।" else "Your post is pending admin review. It will be published after approval.") },
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
