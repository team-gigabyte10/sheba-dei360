package com.barisal.cityservice.feature.moneyexchange

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
import com.barisal.cityservice.data.model.MoneyExchangeDto
import com.barisal.cityservice.data.repository.MoneyExchangeRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMoneyExchangeScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repo = remember { MoneyExchangeRepository() }

    var agencyName by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf(if (isBengali) "কারেন্সি এক্সচেঞ্জ কাউন্টার" else "Currency Exchange Counter") }
    var proprietorOrManager by remember { mutableStateOf("") }
    var licenseNo by remember { mutableStateOf("") }
    var availableCurrencies by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
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

    val themeColor = Color(0xFF0F766E)

    val subCategoryOptions = remember(isBengali) {
        if (isBengali) {
            listOf("কারেন্সি এক্সচেঞ্জ কাউন্টার", "ওয়েস্টার্ন ইউনিয়ন ও মানিগ্রাম", "রেমিট্যান্স ব্যাংক সেবা", "অন্যান্য")
        } else {
            listOf("Currency Exchange Counter", "Western Union & MoneyGram", "Remittance Bank Service", "Others")
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
                title = if (isBengali) "মানি এক্সচেঞ্জ তথ্য পোস্ট করুন" else "Post Money Exchange Counter",
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
                        text = if (isBengali) "এজেন্সি ও কাউন্টার তথ্য" else "Agency & Counter Details",
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

                    // Agency Name
                    OutlinedTextField(
                        value = agencyName,
                        onValueChange = { agencyName = it },
                        label = { Text(if (isBengali) "এজেন্সির নাম *" else "Agency / Counter Name *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // License No & Proprietor
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = licenseNo,
                            onValueChange = { licenseNo = it },
                            label = { Text(if (isBengali) "লাইসেন্স নং (বাংলাদেশ ব্যাংক)" else "Govt License No") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = proprietorOrManager,
                            onValueChange = { proprietorOrManager = it },
                            label = { Text(if (isBengali) "পরিচালক/ম্যানেজার" else "Proprietor/Manager") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Available Currencies
                    OutlinedTextField(
                        value = availableCurrencies,
                        onValueChange = { availableCurrencies = it },
                        label = { Text(if (isBengali) "লেনদেনকৃত মুদ্রা (যেমন: USD, SAR, MYR, EUR, AED) *" else "Available Currencies (e.g. USD, SAR, MYR) *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Counter Address
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text(if (isBengali) "কাউন্টার / অফিসের ঠিকানা *" else "Office / Counter Address *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Location / District
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "এলাকা / জেলা (যেমন: সদর, বরিশাল) *" else "Area / District *") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

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

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBengali) "বিস্তারিত বিবরণ ও সেবা (রেমিট্যান্স ক্যাশ ইন, রেট ইত্যাদি)" else "Description & Services (Remittance, Exchange rates...)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3
                    )

                    // Map Location Picker
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
                                    if (isBengali) "ম্যাপে কাউন্টারের অবস্থান নির্দিষ্ট করুন" else "Pick counter location on Map"
                                },
                                fontSize = 13.sp,
                                color = if (selectedLat != null) themeColor else Color.DarkGray,
                                fontWeight = if (selectedLat != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Counter Photo Selector
                    Text(
                        text = if (isBengali) "কাউন্টার / ব্যানার ছবি (ঐচ্ছিক)" else "Counter / Banner Photo (Optional)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = Color.Gray
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF8FAFC))
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Photo",
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
                            if (agencyName.isBlank() || availableCurrencies.isBlank() || address.isBlank() || contact.isBlank() || location.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill in required fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            coroutineScope.launch {
                                val imageUrl = if (selectedImageUri != null) {
                                    repo.uploadImageToStorage(context, selectedImageUri!!, "money_exchange").getOrDefault("")
                                } else ""

                                val itemDto = MoneyExchangeDto(
                                    agencyName = agencyName.trim(),
                                    subCategory = subCategory,
                                    proprietorOrManager = proprietorOrManager.trim(),
                                    licenseNo = licenseNo.trim(),
                                    availableCurrencies = availableCurrencies.trim(),
                                    address = address.trim(),
                                    location = location.trim(),
                                    contact = contact.trim(),
                                    whatsapp = whatsapp.trim(),
                                    description = description.trim(),
                                    image = imageUrl,
                                    latitude = selectedLat,
                                    longitude = selectedLng
                                )
                                val result = repo.saveMoneyExchangePost(itemDto)
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
            text = { Text(if (isBengali) "আপনার তথ্য অ্যাডমিন পর্যালোচনার জন্য জমা রাখা হয়েছে। অনুমোদনের পর তালিকাভুক্ত হবে।" else "Your post is pending admin review. It will be published after approval.") },
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
