package com.barisal.cityservice.feature.ride

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
import com.barisal.cityservice.data.model.RentCarDto
import com.barisal.cityservice.data.repository.RentCarRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRentCarScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { RentCarRepository() }

    val primaryColor = Color(0xFF1D4ED8) // Deep Blue theme for vehicles

    var title by remember { mutableStateOf("") }
    var selectedSubCategory by remember { mutableStateOf(if (isBengali) "প্রাইভেট কার" else "Private Car") }
    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }

    val subCategoriesList = listOf(
        "প্রাইভেট কার",
        "মাইক্রোবাস",
        "পিকআপ",
        "ট্রাক",
        "রাইড শেয়ারিং",
        "ভ্যান ভাড়া",
        "অটো ভাড়া"
    )

    var price by remember { mutableStateOf("") }
    var selectedPriceUnit by remember { mutableStateOf("per_day") } // per_day, per_hour, trip
    var vehicleModel by remember { mutableStateOf("") }
    var seatingCapacity by remember { mutableStateOf("") }
    var hasAC by remember { mutableStateOf(true) }

    var driverName by remember { mutableStateOf("") }
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

    com.barisal.cityservice.ui.components.SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "গাড়ি ভাড়া পোস্ট করুন" else "Post Rent a Car",
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
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBengali) "গাড়ি ভাড়ার বিজ্ঞাপন দিন" else "Post Vehicle Rental Listing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = if (isBengali) "কার, পিকআপ, মাইক্রোবাস ইত্যাদি ভাড়ার জন্য পোস্ট করুন" else "Add details and photo for your rental vehicle",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Vehicle Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBengali) "গাড়ির নাম / শিরোনাম *" else "Vehicle Title *") },
                    placeholder = { Text(if (isBengali) "যেমন: টয়োটা নোয়া মাইক্রোবাস" else "e.g. Toyota Noah Microbus") },
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
                        label = { Text(if (isBengali) "গাড়ির ক্যাটাগরি *" else "Vehicle Category *") },
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

                // Rental Rate & Price Unit
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text(if (isBengali) "ভাড়া (টাকা) *" else "Rental Rate (BDT) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("৩৫০০") },
                        modifier = Modifier.weight(1.2f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isBengali) "হিসাব:" else "Rate Unit:", fontSize = 12.sp, color = Color.Gray)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPriceUnit == "per_day",
                                onClick = { selectedPriceUnit = "per_day" }
                            )
                            Text(if (isBengali) "প্রতি দিন" else "Per Day", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = selectedPriceUnit == "trip",
                                onClick = { selectedPriceUnit = "trip" }
                            )
                            Text(if (isBengali) "প্রতি ট্রিপ" else "Trip", fontSize = 12.sp)
                        }
                    }
                }

                // Model & Seating Capacity
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = vehicleModel,
                        onValueChange = { vehicleModel = it },
                        label = { Text(if (isBengali) "গাড়ির মডেল" else "Vehicle Model") },
                        placeholder = { Text("Noah 2018") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    OutlinedTextField(
                        value = seatingCapacity,
                        onValueChange = { seatingCapacity = it },
                        label = { Text(if (isBengali) "আসন সংখ্যা" else "Seating Capacity") },
                        placeholder = { Text("৭ জন") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                }

                // AC Toggle Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = if (isBengali) "এসি ব্যবস্থা (Air Conditioned)" else "Air Conditioned (AC)", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Switch(
                        checked = hasAC,
                        onCheckedChange = { hasAC = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.5f))
                    )
                }

                // Driver/Owner Name & Contact Phone
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = driverName,
                        onValueChange = { driverName = it },
                        label = { Text(if (isBengali) "ড্রাইভার/মালিকের নাম *" else "Driver/Owner Name *") },
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
                Text(if (isBengali) "গাড়ির ছবি:" else "Vehicle Photo:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
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
                            Text(if (isBengali) "গাড়ির কভার ছবি যোগ করুন" else "Tap to add vehicle photo", color = Color.Gray, fontSize = 13.sp)
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
                        if (title.isBlank() || contact.isBlank() || address.isBlank()) {
                            Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isSubmitting = true
                        coroutineScope.launch {
                            val coverBase64 = selectedCoverImage?.let { uri ->
                                repository.compressImageToBase64(context, uri).getOrNull() ?: ""
                            } ?: ""

                            val carDto = RentCarDto(
                                title = title,
                                subCategory = selectedSubCategory,
                                price = price,
                                priceUnit = selectedPriceUnit,
                                vehicleModel = vehicleModel,
                                seatingCapacity = seatingCapacity,
                                hasAC = hasAC,
                                driverName = driverName,
                                contact = contact,
                                address = address,
                                thana = thana,
                                coverImage = coverBase64,
                                latitude = selectedLocation.latitude,
                                longitude = selectedLocation.longitude
                            )

                            val result = repository.saveRentCarToFirestore(carDto)
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
