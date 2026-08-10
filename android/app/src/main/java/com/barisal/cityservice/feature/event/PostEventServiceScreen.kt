package com.barisal.cityservice.feature.event

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
import androidx.compose.material.icons.automirrored.filled.Send
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
import com.barisal.cityservice.data.model.EventProviderDto
import com.barisal.cityservice.data.repository.EventRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostEventServiceScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { EventRepository() }

    // Module 1: Category Selection State
    val categoryList = remember { eventCategories.map { it.title } }
    var selectedCategory by remember { mutableStateOf(categoryList.first()) }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    // Module 2: Business & Contact Info State
    var businessName by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var whatsappPhone by remember { mutableStateOf("") }

    // Category Specific Fields State
    // Catering / Baburchi
    var menuItemsText by remember { mutableStateOf("") }
    var perPlatePriceText by remember { mutableStateOf("") }
    var maxCookingCapacityText by remember { mutableStateOf("") }
    var chefSpecialtyText by remember { mutableStateOf("") }
    var assistantStaffCountText by remember { mutableStateOf("") }

    // Photographer
    var cameraGearText by remember { mutableStateOf("") }
    var deliverablesText by remember { mutableStateOf("") }
    var coveragePackageText by remember { mutableStateOf("") }

    // Decorator / Sound & Light
    var stageDecorStyleText by remember { mutableStateOf("") }
    var soundEquipmentText by remember { mutableStateOf("") }

    // Parlor / Mehndi
    var parlorPackagesText by remember { mutableStateOf("") }
    var cosmeticsBrandText by remember { mutableStateOf("") }
    var isHomeServiceAvailable by remember { mutableStateOf(true) }

    // Community Center
    var seatingCapacityText by remember { mutableStateOf("") }
    var parkingCapacityText by remember { mutableStateOf("") }
    var isAcAvailable by remember { mutableStateOf(true) }

    // Band Party / Others
    var bandInstrumentsText by remember { mutableStateOf("") }
    var customCategoryNotes by remember { mutableStateOf("") }

    // Module 3: Location State
    var selectedZilla by remember { mutableStateOf(bangladeshZillas.firstOrNull { it == "বরিশাল" || it == "Barishal" } ?: bangladeshZillas.first()) }
    var zillaDropdownExpanded by remember { mutableStateOf(false) }
    var thana by remember { mutableStateOf("") }
    var addressBn by remember { mutableStateOf("") }
    var latLng by remember { mutableStateOf("22.7010,90.3535") }

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

    // Module 4: Pricing & Package State
    var startingPackageText by remember { mutableStateOf("") }
    var isNegotiablePrice by remember { mutableStateOf(false) }
    var selectedPriceUnit by remember { mutableStateOf("ইভেন্ট") }
    val priceUnitOptions = listOf("ইভেন্ট", "জন (প্লেট)", "দিন", "ঘণ্টা")

    // Module 5: Experience & Amenities State
    var experienceYearsText by remember { mutableStateOf("") }
    val availableFeatures = listOf("এসি ভেন্যু", "সাউন্ড সিস্টেম", "আউটডোর সার্ভিস", "কাস্টম মেন্যু", "স্টেজ ডেকোরেশন", "ক্যাটারিং স্টাফ")
    var selectedFeatures by remember { mutableStateOf<Set<String>>(setOf("সাউন্ড সিস্টেম", "ক্যাটারিং স্টাফ")) }

    // Module 6: Photos & Gallery Upload State
    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val multiImagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            selectedImageUris = (selectedImageUris + uris).take(5) // Limit up to 5 photos
        }
    }
    var videoUrl by remember { mutableStateOf("") }

    // Module 7: Details / Description State
    var details by remember { mutableStateOf("") }

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
    val primaryColor = Color(0xFF1E3A8A) // Dark Blue theme matching Event Service

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "ইভেন্ট সার্ভিস পোস্ট করুন" else "Post Event Service",
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
                    text = if (isBengali) "আপনার ইভেন্ট সার্ভিসের বিস্তারিত তথ্য দিন" else "Provide Your Event Service Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // MODULE 1: Category Picker Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBengali) "সার্ভিস ক্যাটাগরি" else "Service Category",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        ExposedDropdownMenuBox(
                            expanded = categoryDropdownExpanded,
                            onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    unfocusedBorderColor = Color(0xFFCBD5E1)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = categoryDropdownExpanded,
                                onDismissRequest = { categoryDropdownExpanded = false }
                            ) {
                                categoryList.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category) },
                                        onClick = {
                                            selectedCategory = category
                                            categoryDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // DYNAMIC MODULE: Category Specific Text Fields
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = primaryColor)
                            Text(
                                text = "$selectedCategory - বিশেষায়িত তথ্য",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }

                        when (selectedCategory) {
                            "ক্যাটারিং সার্ভিস" -> {
                                OutlinedTextField(
                                    value = menuItemsText,
                                    onValueChange = { menuItemsText = it },
                                    label = { Text("মেন্যু আইটেমসমূহ (যেমন: কাচ্চি, চিকেন রোস্ট, ফিরনি)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = perPlatePriceText,
                                    onValueChange = { perPlatePriceText = it },
                                    label = { Text("প্রতি প্লেট সর্বনিম্ন মূল্য (টাকা)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            "বাবুর্চি" -> {
                                OutlinedTextField(
                                    value = maxCookingCapacityText,
                                    onValueChange = { maxCookingCapacityText = it },
                                    label = { Text("সর্বোচ্চ কতজনের রান্না করতে পারেন (মেহমান সংখ্যা)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = chefSpecialtyText,
                                    onValueChange = { chefSpecialtyText = it },
                                    label = { Text("রান্নার বিশেষত্ব (যেমন: কাচ্চি বিরিয়ানি, পোলাও রোস্ট, মিষ্টান্ন)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = assistantStaffCountText,
                                    onValueChange = { assistantStaffCountText = it },
                                    label = { Text("সহকারী বাবুর্চি / হেল্পার সংখ্যা") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            "ফটোগ্রাফার" -> {
                                OutlinedTextField(
                                    value = cameraGearText,
                                    onValueChange = { cameraGearText = it },
                                    label = { Text("ক্যামেরা ও ড্রোন গিয়ার (যেমন: Sony A7IV, Canon 5D, DJI Drone)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = deliverablesText,
                                    onValueChange = { deliverablesText = it },
                                    label = { Text("ডিলেভারি উপাদান (যেমন: ১৫০ কপি এডিটেড ছবি, ২ টি অ্যালবাম, প্রমো ভিডিও)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = coveragePackageText,
                                    onValueChange = { coveragePackageText = it },
                                    label = { Text("কাভারেজ প্যাকেজ (হাফ ডে / ফুল ডে / মাল্টি-ডে)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            "ডেকোরেটর", "লাইট, সাউন্ড" -> {
                                OutlinedTextField(
                                    value = stageDecorStyleText,
                                    onValueChange = { stageDecorStyleText = it },
                                    label = { Text("গেট ও স্টেজ ডেকোরেশন থিম (যেমন: ফ্লাওয়ার থিম, এন্ট্রি গেট লাইটিং)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = soundEquipmentText,
                                    onValueChange = { soundEquipmentText = it },
                                    label = { Text("সাউন্ড ও লাইটিং ইকুয়েপমেন্ট (যেমন: জেনারেটর, ডিজে স্পিকার, মোটিফ লাইট)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            "পার্লার", "মেহেদী আর্টিস্ট" -> {
                                OutlinedTextField(
                                    value = parlorPackagesText,
                                    onValueChange = { parlorPackagesText = it },
                                    label = { Text("প্যাকেজ সুবিধাসমূহ (যেমন: ব্রাইডাল মেকআপ, এইচডি মেকআপ, মেহেদী ডিজাইন)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = cosmeticsBrandText,
                                    onValueChange = { cosmeticsBrandText = it },
                                    label = { Text("ব্যবহৃত কসমেটিকস ব্রান্ড ও মেহেদীর ধরণ (অর্গানিক/ব্র্যান্ডেড)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Text("হোম সার্ভিস এভেইলএবল?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Switch(
                                        checked = isHomeServiceAvailable,
                                        onCheckedChange = { isHomeServiceAvailable = it }
                                    )
                                }
                            }
                            "কমিউনিটি সেন্টার" -> {
                                OutlinedTextField(
                                    value = seatingCapacityText,
                                    onValueChange = { seatingCapacityText = it },
                                    label = { Text("সর্বোচ্চ আসন সংখ্যা (মেহমান ধারণক্ষমতা)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                OutlinedTextField(
                                    value = parkingCapacityText,
                                    onValueChange = { parkingCapacityText = it },
                                    label = { Text("পার্কিং সুবিধা (গাড়ির সংখ্যা)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                                ) {
                                    Text("এসি সেন্ট্রাল হল ও জেনারেটর ব্যাকআপ?", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                    Switch(
                                        checked = isAcAvailable,
                                        onCheckedChange = { isAcAvailable = it }
                                    )
                                }
                            }
                            "ব্যান্ড পার্টি" -> {
                                OutlinedTextField(
                                    value = bandInstrumentsText,
                                    onValueChange = { bandInstrumentsText = it },
                                    label = { Text("বাদ্যযন্ত্র ও পারফর্মার বিবরণ (যেমন: ৮ জন পারফর্মার, বিগল ট্রাম্পেট, ড্রাম)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                            else -> {
                                OutlinedTextField(
                                    value = customCategoryNotes,
                                    onValueChange = { customCategoryNotes = it },
                                    label = { Text("সার্ভিস বিশেষত্ব ও বিশেষ সুবিধা...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            // MODULE 2: Media & Gallery Upload
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "পোর্টফোলিও ছবি (সর্বোচ্চ ৫ টি)" else "Portfolio Photos (Max 5)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                            Text(
                                text = "${selectedImageUris.size}/5",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(selectedImageUris) { uri ->
                                Box(
                                    modifier = Modifier
                                        .size(90.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                ) {
                                    AsyncImage(
                                        model = uri,
                                        contentDescription = "Selected Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                    IconButton(
                                        onClick = { selectedImageUris = selectedImageUris - uri },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .background(Color.Red.copy(alpha = 0.8f), CircleShape)
                                    ) {
                                        Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Remove",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            if (selectedImageUris.size < 5) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(90.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFEFF6FF))
                                            .border(1.dp, primaryColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .clickable { multiImagePickerLauncher.launch("image/*") },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.AddAPhoto, contentDescription = "Add Photo", tint = primaryColor)
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("ছবি যোগ করুন", fontSize = 10.sp, color = primaryColor)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // MODULE 3: Business & Contact Details Form
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "প্রতিষ্ঠান ও যোগাযোগের তথ্য" else "Business & Contact Info",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )

                        OutlinedTextField(
                            value = businessName,
                            onValueChange = { businessName = it },
                            label = { Text(if (isBengali) "প্রোভাইডার / প্রতিষ্ঠানের নাম *" else "Business / Provider Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { ownerName = it },
                            label = { Text(if (isBengali) "মালিক বা প্রতিনিধির নাম" else "Owner / Manager Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text(if (isBengali) "ফোন নম্বর *" else "Phone Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

                        OutlinedTextField(
                            value = whatsappPhone,
                            onValueChange = { whatsappPhone = it },
                            label = { Text(if (isBengali) "হোয়াটসঅ্যাপ নম্বর (ঐচ্ছিক)" else "WhatsApp Number (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )
                    }
                }
            }

            // MODULE 4: Flexible Pricing & Package Details
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (isBengali) "মূল্য ও প্যাকেজ" else "Pricing & Packages",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    isNegotiablePrice = !isNegotiablePrice
                                }
                            ) {
                                Checkbox(
                                    checked = isNegotiablePrice,
                                    onCheckedChange = { isNegotiablePrice = it }
                                )
                                Text(
                                    text = if (isBengali) "আলোচনা সাপেক্ষে" else "Negotiable",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF334155)
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = if (isNegotiablePrice) "আলোচনা সাপেক্ষে" else startingPackageText,
                                onValueChange = { if (!isNegotiablePrice) startingPackageText = it },
                                enabled = !isNegotiablePrice,
                                label = { Text(if (isBengali) "শুরু প্যাকেজ (টাকা)" else "Starting Price (BDT)") },
                                placeholder = { Text("মূল্য লিখুন (যেমন: ৫০০০)") },
                                modifier = Modifier.weight(1.2f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = primaryColor,
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White
                                )
                            )

                            Column(modifier = Modifier.weight(1.8f)) {
                                Text("হিসাবের একক", fontSize = 12.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(4.dp))
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    items(priceUnitOptions) { unit ->
                                        FilterChip(
                                            selected = !isNegotiablePrice && selectedPriceUnit == unit,
                                            enabled = !isNegotiablePrice,
                                            onClick = { selectedPriceUnit = unit },
                                            label = { Text(unit, fontSize = 11.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = primaryColor,
                                                selectedLabelColor = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        OutlinedTextField(
                            value = experienceYearsText,
                            onValueChange = { experienceYearsText = it },
                            label = { Text(if (isBengali) "অভিজ্ঞতার বছর (ঐচ্ছিক)" else "Experience (Years)") },
                            placeholder = { Text("যেমন: ৩") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                    }
                }
            }

            // MODULE 5: Feature Amenities Chips
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isBengali) "সুবিধাসমূহ / বৈশিষ্ট্য" else "Features & Amenities",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableFeatures.forEach { feature ->
                                val isSelected = selectedFeatures.contains(feature)
                                FilterChip(
                                    selected = isSelected,
                                    onClick = {
                                        selectedFeatures = if (isSelected) {
                                            selectedFeatures - feature
                                        } else {
                                            selectedFeatures + feature
                                        }
                                    },
                                    label = { Text(feature, fontSize = 12.sp) },
                                    leadingIcon = if (isSelected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
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

            // MODULE 6: Geographic Location & Google Maps Picker
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "অবস্থান ও ঠিকানা" else "Location & Address",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )

                        // Zilla Dropdown
                        ExposedDropdownMenuBox(
                            expanded = zillaDropdownExpanded,
                            onExpandedChange = { zillaDropdownExpanded = !zillaDropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedZilla,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(if (isBengali) "জেলা *" else "District *") },
                                modifier = Modifier.fillMaxWidth().menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zillaDropdownExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                            )
                            ExposedDropdownMenu(
                                expanded = zillaDropdownExpanded,
                                onDismissRequest = { zillaDropdownExpanded = false }
                            ) {
                                bangladeshZillas.forEach { zilla ->
                                    DropdownMenuItem(
                                        text = { Text(zilla) },
                                        onClick = {
                                            selectedZilla = zilla
                                            zillaDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = thana,
                            onValueChange = { thana = it },
                            label = { Text(if (isBengali) "থানা / উপজেলা" else "Thana / Upazila") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

                        OutlinedTextField(
                            value = addressBn,
                            onValueChange = { addressBn = it },
                            label = { Text(if (isBengali) "বিস্তারিত ঠিকানা (রোড/হোল্ডিং)" else "Full Address") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

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
            }

            // MODULE 7: Description & Video Link
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "বিস্তারিত বিবরণ" else "Detailed Description",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )

                        OutlinedTextField(
                            value = videoUrl,
                            onValueChange = { videoUrl = it },
                            label = { Text(if (isBengali) "প্রোমো ভিডিও লিংক (ঐচ্ছিক)" else "Promo Video Link (Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Videocam, contentDescription = null, tint = primaryColor) },
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )

                        OutlinedTextField(
                            value = details,
                            onValueChange = { details = it },
                            label = { Text(if (isBengali) "প্যাকেজের বিবরণ ও সার্ভিস শর্তাবলী..." else "Package details & service terms...") },
                            modifier = Modifier.fillMaxWidth().height(110.dp),
                            maxLines = 5,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor)
                        )
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (businessName.isBlank()) {
                            Toast.makeText(context, "অনুগ্রহ করে প্রতিষ্ঠানের নাম লিখুন", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (phone.isBlank()) {
                            Toast.makeText(context, "অনুগ্রহ করে ফোন নম্বর লিখুন", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            val base64Images = repository.compressMultipleImages(context, selectedImageUris)
                            val startingPackage = if (isNegotiablePrice) 0 else (startingPackageText.toIntOrNull() ?: 0)
                            val experienceYears = experienceYearsText.toIntOrNull() ?: 0

                            val categoryDetailsMap = mutableMapOf<String, String>()
                            when (selectedCategory) {
                                "ক্যাটারিং সার্ভিস" -> {
                                    if (menuItemsText.isNotBlank()) categoryDetailsMap["menuItems"] = menuItemsText.trim()
                                    if (perPlatePriceText.isNotBlank()) categoryDetailsMap["perPlatePrice"] = perPlatePriceText.trim()
                                }
                                "বাবুর্চি" -> {
                                    if (maxCookingCapacityText.isNotBlank()) categoryDetailsMap["maxCapacity"] = maxCookingCapacityText.trim()
                                    if (chefSpecialtyText.isNotBlank()) categoryDetailsMap["specialty"] = chefSpecialtyText.trim()
                                    if (assistantStaffCountText.isNotBlank()) categoryDetailsMap["assistantStaff"] = assistantStaffCountText.trim()
                                }
                                "ফটোগ্রাফার" -> {
                                    if (cameraGearText.isNotBlank()) categoryDetailsMap["cameraGear"] = cameraGearText.trim()
                                    if (deliverablesText.isNotBlank()) categoryDetailsMap["deliverables"] = deliverablesText.trim()
                                    if (coveragePackageText.isNotBlank()) categoryDetailsMap["coveragePackage"] = coveragePackageText.trim()
                                }
                                "ডেকোরেটর", "লাইট, সাউন্ড" -> {
                                    if (stageDecorStyleText.isNotBlank()) categoryDetailsMap["stageDecorStyle"] = stageDecorStyleText.trim()
                                    if (soundEquipmentText.isNotBlank()) categoryDetailsMap["soundEquipment"] = soundEquipmentText.trim()
                                }
                                "পার্লার", "মেহেদী আর্টিস্ট" -> {
                                    if (parlorPackagesText.isNotBlank()) categoryDetailsMap["parlorPackages"] = parlorPackagesText.trim()
                                    if (cosmeticsBrandText.isNotBlank()) categoryDetailsMap["cosmeticsBrand"] = cosmeticsBrandText.trim()
                                    categoryDetailsMap["isHomeServiceAvailable"] = isHomeServiceAvailable.toString()
                                }
                                "কমিউনিটি সেন্টার" -> {
                                    if (seatingCapacityText.isNotBlank()) categoryDetailsMap["seatingCapacity"] = seatingCapacityText.trim()
                                    if (parkingCapacityText.isNotBlank()) categoryDetailsMap["parkingCapacity"] = parkingCapacityText.trim()
                                    categoryDetailsMap["isAcAvailable"] = isAcAvailable.toString()
                                }
                                "ব্যান্ড পার্টি" -> {
                                    if (bandInstrumentsText.isNotBlank()) categoryDetailsMap["bandInstruments"] = bandInstrumentsText.trim()
                                }
                                else -> {
                                    if (customCategoryNotes.isNotBlank()) categoryDetailsMap["customNotes"] = customCategoryNotes.trim()
                                }
                            }

                            val providerDto = EventProviderDto(
                                name = businessName.trim(),
                                ownerName = ownerName.trim(),
                                categoryName = selectedCategory,
                                phone = phone.trim(),
                                whatsappPhone = whatsappPhone.trim(),
                                zilla = selectedZilla,
                                thana = thana.trim(),
                                addressBn = addressBn.trim(),
                                addressEn = addressBn.trim(),
                                latLng = latLng,
                                startingPackage = startingPackage,
                                priceUnit = selectedPriceUnit,
                                experienceYears = experienceYears,
                                imageUrls = base64Images,
                                videoUrl = videoUrl.trim(),
                                features = selectedFeatures.toList(),
                                categoryDetails = categoryDetailsMap,
                                details = details.trim()
                            )

                            val res = repository.saveEventService(providerDto)
                            isSubmitting = false
                            if (res.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, "পোস্ট করতে সমস্যা হয়েছে: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !isSubmitting,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("সাবমিট হচ্ছে...", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBengali) "সার্ভিস পোস্ট করুন" else "Post Event Service", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        "আপনার ইভেন্ট সেবার পোস্টটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর পোস্টটি প্রকাশিত হবে।"
                    else
                        "Your event service post has been submitted for admin review. It will be published once approved.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
