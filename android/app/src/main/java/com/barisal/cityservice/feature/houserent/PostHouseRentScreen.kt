package com.barisal.cityservice.feature.houserent

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.rememberLocationPermissionState
import com.barisal.cityservice.data.model.FlatDetailsDto
import com.barisal.cityservice.data.model.HouseRentDto
import com.barisal.cityservice.data.repository.HouseRentRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostHouseRentScreen(
    initialSubCategory: String? = null,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { HouseRentRepository() }

    val houseTypeList = listOf("ফ্ল্যাট ভাড়া", "ব্যাচেলর রুম/সিট", "মেয়েদের মেস", "সাবলেট", "হোস্টেল", "অফিস স্পেস", "দোকান", "গ্যারেজ")
    val defaultHouseType = remember(initialSubCategory) {
        if (initialSubCategory.isNullOrBlank()) return@remember "ফ্ল্যাট ভাড়া"
        val decoded = try {
            java.net.URLDecoder.decode(initialSubCategory, "UTF-8").replace("+", " ").trim()
        } catch (e: Exception) {
            initialSubCategory.replace("+", " ").trim()
        }
        houseTypeList.find { type ->
            type.equals(decoded, ignoreCase = true) ||
            type.replace(" ", "").equals(decoded.replace(" ", ""), ignoreCase = true)
        } ?: "ফ্ল্যাট ভাড়া"
    }

    var title by remember { mutableStateOf("") }
    var selectedHouseType by remember(defaultHouseType) { mutableStateOf(defaultHouseType) }
    var houseTypeDropdownExpanded by remember { mutableStateOf(false) }

    var rentAmount by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var latLng by remember { mutableStateOf("22.7010,90.3535") }

    // Flat Details
    var houseNo by remember { mutableStateOf("") }
    var levelNo by remember { mutableStateOf("") }
    var flatNo by remember { mutableStateOf("") }
    var bedrooms by remember { mutableStateOf("") }
    var bathrooms by remember { mutableStateOf("") }
    var balconies by remember { mutableStateOf("") }

    // Validity Days (Default 30 Days)
    var selectedValidityDays by remember { mutableStateOf(30) }
    val validityOptions = listOf(7, 15, 30, 60, 90)

    // Multiple Image State
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val multiImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris = (selectedImageUris + uris).take(5) // Limit up to 5 photos
        }
    }

    var selectedLocation by remember { mutableStateOf(LatLng(22.7010, 90.3535)) }
    val markerState = rememberMarkerState(position = selectedLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f)
    }

    LaunchedEffect(markerState.position) {
        selectedLocation = markerState.position
        latLng = String.format(java.util.Locale.US, "%.5f,%.5f", markerState.position.latitude, markerState.position.longitude)
    }

    val hasLocationPermission = rememberLocationPermissionState()

    var selectedZilla by remember { mutableStateOf(bangladeshZillas.firstOrNull { it == "বরিশাল" || it == "Barishal" } ?: bangladeshZillas.first()) }
    var zillaDropdownExpanded by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLocationPickerMap by remember { mutableStateOf(false) }

    if (showLocationPickerMap) {
        val latLngParts = latLng.split(",")
        val pLat = latLngParts.getOrNull(0)?.trim()?.toDoubleOrNull()
        val pLng = latLngParts.getOrNull(1)?.trim()?.toDoubleOrNull()
        LocationPickerMapScreen(
            initialLat = pLat,
            initialLng = pLng,
            onLocationSelected = { selectedLat, selectedLng ->
                latLng = String.format("%.5f,%.5f", selectedLat, selectedLng)
                showLocationPickerMap = false
            },
            onBack = { showLocationPickerMap = false }
        )
        return
    }

    SetStatusBarColor()

    val primaryColor = Color(0xFF00897B)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বাসা ভাড়ার পোস্ট করুন" else "Post House Rent",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = if (isBengali) "বাসা ভাড়ার বিস্তারিত তথ্য দিন" else "Enter House Rent Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            item {
                // Multiple Photos Upload Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedImageUris.isNotEmpty()) {
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(selectedImageUris) { uri ->
                                    Box(modifier = Modifier.size(100.dp)) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = "Selected House Image",
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .clip(RoundedCornerShape(8.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        IconButton(
                                            onClick = { selectedImageUris = selectedImageUris - uri },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .size(26.dp)
                                                .offset(x = 4.dp, y = (-4).dp)
                                                .clip(CircleShape)
                                                .background(Color.Red)
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedButton(
                            onClick = { multiImagePickerLauncher.launch("image/*") },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor),
                            enabled = selectedImageUris.size < 5
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (selectedImageUris.isEmpty()) {
                                    if (isBengali) "বাসার একাধিক ছবি যুক্ত করুন (সর্বোচ্চ ৫টি)" else "Upload Photos (Max 5)"
                                } else {
                                    if (isBengali) "আরও ছবি যুক্ত করুন (${selectedImageUris.size}/৫)" else "Add More Photos (${selectedImageUris.size}/5)"
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            item {
                // Post Title
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBengali) "পোস্টের শিরোনাম *" else "Post Title *") },
                    placeholder = { Text(if (isBengali) "যেমন: ৩ রুমের সুন্দর ফ্ল্যাট ভাড়া" else "e.g. 3 Bed Apartment for Rent") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                // Sub-category Dropdown
                ExposedDropdownMenuBox(
                    expanded = houseTypeDropdownExpanded,
                    onExpandedChange = { houseTypeDropdownExpanded = !houseTypeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedHouseType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "ভাড়ার ধরন *" else "Rent Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = houseTypeDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Category, contentDescription = null, tint = primaryColor) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = houseTypeDropdownExpanded,
                        onDismissRequest = { houseTypeDropdownExpanded = false }
                    ) {
                        houseTypeList.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type) },
                                onClick = {
                                    selectedHouseType = type
                                    houseTypeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                // Rent Amount
                OutlinedTextField(
                    value = rentAmount,
                    onValueChange = { rentAmount = it },
                    label = { Text(if (isBengali) "ভাড়ার পরিমাণ *" else "Rent Amount *") },
                    placeholder = { Text(if (isBengali) "যেমন: ৳১২,০০০/মাস" else "e.g. ৳12,000/month") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                // Flat / House Specific Details Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isBengali) "ফ্ল্যাট / বাসার সুনির্দিষ্ট তথ্য" else "Flat / Property Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = primaryColor
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = houseNo,
                                onValueChange = { houseNo = it },
                                label = { Text(if (isBengali) "হাউজ নং" else "House No") },
                                placeholder = { Text("৪৫/এ") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = levelNo,
                                onValueChange = { levelNo = it },
                                label = { Text(if (isBengali) "তলা / লেভেল" else "Floor/Level") },
                                placeholder = { Text("৩য় তলা") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = flatNo,
                                onValueChange = { flatNo = it },
                                label = { Text(if (isBengali) "ফ্ল্যাট নং" else "Flat No") },
                                placeholder = { Text("B-3") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = bedrooms,
                                onValueChange = { bedrooms = it },
                                label = { Text(if (isBengali) "বেডরুম" else "Bedrooms") },
                                placeholder = { Text("৩টি") },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = bathrooms,
                                onValueChange = { bathrooms = it },
                                label = { Text(if (isBengali) "বাথরুম" else "Bathrooms") },
                                placeholder = { Text(" ২টি") },
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = balconies,
                                onValueChange = { balconies = it },
                                label = { Text(if (isBengali) "বারান্দা" else "Balconies") },
                                placeholder = { Text("২টি") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            item {
                // Post Validity Duration Section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "পোস্টের স্থায়িত্বের মেয়াদ (Validity Days)" else "Post Validity Duration",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = primaryColor
                        )
                        Text(
                            text = if (isBengali) "মেয়াদ শেষ হলে পোস্টটি স্বয়ংক্রিয়ভাবে তালিকা থেকে সরে যাবে" else "Post will automatically expire after selected duration",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            validityOptions.forEach { days ->
                                val isSelected = selectedValidityDays == days
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedValidityDays = days },
                                    label = { Text("${days} ${if (isBengali) "দিন" else "Days"}", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = primaryColor,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Address
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBengali) "পূর্ণাঙ্গ ঠিকানা *" else "Full Address *") },
                    placeholder = { Text(if (isBengali) "যেমন: রোড নং ৩, নবগ্রাম রোড" else "Road 3, Nobogram Road") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                // Zilla Dropdown
                ExposedDropdownMenuBox(
                    expanded = zillaDropdownExpanded,
                    onExpandedChange = { zillaDropdownExpanded = !zillaDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedZilla,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "জেলা *" else "District (Zilla) *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zillaDropdownExpanded) },
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = primaryColor) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = zillaDropdownExpanded,
                        onDismissRequest = { zillaDropdownExpanded = false }
                    ) {
                        bangladeshZillas.forEach { zillaName ->
                            DropdownMenuItem(
                                text = { Text(zillaName) },
                                onClick = {
                                    selectedZilla = zillaName
                                    zillaDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            item {
                // Thana / Area
                OutlinedTextField(
                    value = thana,
                    onValueChange = { thana = it },
                    label = { Text(if (isBengali) "থানা / এলাকা" else "Thana / Area") },
                    placeholder = { Text(if (isBengali) "যেমন: কোতোয়ালী" else "e.g. Kotowali") },
                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                // Contact Info
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text(if (isBengali) "যোগাযোগের ফোন নম্বর *" else "Contact Phone Number *") },
                    placeholder = { Text("01711-XXXXXX") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                // Details
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(if (isBengali) "বিস্তারিত বিবরণ" else "Additional Details") },
                    placeholder = { Text(if (isBengali) "গ্যাস, পানি, লিফট ও পার্কিং সুবিধা সম্পর্কে লিখুন..." else "Write about gas, water, lift, parking facilities...") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showLocationPickerMap = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
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

                    if (latLng.isNotBlank()) {
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
                                    text = if (isBengali) "লোকেশন সেট করা হয়েছে: $latLng" else "Location Set: $latLng",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            }

            item {
                // Submit Button
                Button(
                    onClick = {
                        if (title.isBlank() || address.isBlank() || contactInfo.isBlank() || rentAmount.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "অনুগ্রহ করে শিরোনাম, ঠিকানা, ভাড়া ও যোগাযোগ নম্বর দিন" else "Please fill title, address, rent amount, and contact info",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSubmitting = true

                        coroutineScope.launch {
                            val storageImageUrls = if (selectedImageUris.isNotEmpty()) {
                                repository.uploadMultipleImagesToStorage(context, selectedImageUris, "house_rents/photos").getOrDefault(emptyList())
                            } else emptyList()

                            val expiresAtMillis = System.currentTimeMillis() + (selectedValidityDays * 24L * 60L * 60L * 1000L)

                            val flatDetailsDto = FlatDetailsDto(
                                houseNo = houseNo.trim(),
                                levelNo = levelNo.trim(),
                                flatNo = flatNo.trim(),
                                bedrooms = bedrooms.trim(),
                                bathrooms = bathrooms.trim(),
                                balconies = balconies.trim()
                            )

                            val houseRentDto = HouseRentDto(
                                title = title.trim(),
                                houseType = selectedHouseType,
                                rentAmount = rentAmount.trim(),
                                address = address.trim(),
                                zilla = selectedZilla,
                                thana = thana.trim(),
                                contactInfo = contactInfo.trim(),
                                latLng = latLng.trim(),
                                imageUrls = storageImageUrls,
                                flatDetails = flatDetailsDto,
                                validityDays = selectedValidityDays,
                                expiresAt = expiresAtMillis,
                                isRented = false,
                                details = details.trim()
                            )

                            val result = repository.saveHouseRentToFirestore(houseRentDto)
                            isSubmitting = false

                            if (result.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "পোস্ট করতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}" else "Failed to post: ${result.exceptionOrNull()?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "পোস্ট জমা দিন" else "Submit Post",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }

        if (showSuccessDialog) {
            CustomDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onBack()
                },
                title = if (isBengali) "পোস্ট সফলভাবে জমা হয়েছে!" else "Post Submitted Successfully!",
                icon = Icons.Default.Check,
                iconTint = Color(0xFF16A34A),
                iconBackgroundColor = Color(0xFFDCFCE7),
                confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
                onConfirm = {
                    showSuccessDialog = false
                    onBack()
                }
            ) {
                Text(
                    text = if (isBengali)
                        "আপনার বাসা ভাড়ার বিজ্ঞাপনটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর পোস্টটি প্রকাশিত হবে।"
                    else
                        "Your house rent ad has been submitted for admin review. It will be published once approved.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
