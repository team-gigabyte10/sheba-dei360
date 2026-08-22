package com.barisal.cityservice.feature.property

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import com.barisal.cityservice.data.model.PropertyDto
import com.barisal.cityservice.data.repository.PropertyRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostPropertyScreen(
    onBack: () -> Unit,
    onPostCreated: () -> Unit = {}
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val propertyRepo = remember { PropertyRepository() }

    val primaryColor = Color(0xFF334155) // Slate Theme

    var propertyType by remember { mutableStateOf("flat") } // "flat" = ফ্ল্যাট, "land" = জমি

    var title by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    // Flat Specific
    var roomSize by remember { mutableStateOf("") }
    var floor by remember { mutableStateOf("") }
    var beds by remember { mutableStateOf("3") }
    var baths by remember { mutableStateOf("3") }
    var dining by remember { mutableStateOf("1") }
    var drawing by remember { mutableStateOf("1") }
    var balcony by remember { mutableStateOf("2") }

    // Land Specific
    var area by remember { mutableStateOf("") }
    var roadWidth by remember { mutableStateOf("") }
    var landType by remember { mutableStateOf("আবাসিক") }
    var registrationStatus by remember { mutableStateOf("সব কাগজপত্র আপডেট") }

    // Map Location State
    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showLocationPickerMap by remember { mutableStateOf(false) }

    // Photo Picker State
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages = (selectedImages + uris).distinct().take(5)
        }
    }

    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

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
                title = if (isBengali) "ফ্ল্যাট ও জমি বিজ্ঞাপন দিন" else "Post Property Ad",
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
                // Property Type Chips
                Text(
                    text = if (isBengali) "প্রপার্টির ধরন নির্বাচন করুন *" else "Select Property Type *",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = propertyType == "flat",
                        onClick = { propertyType = "flat" },
                        modifier = Modifier.weight(1f),
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(if (isBengali) "🏢 ফ্ল্যাট বিক্রি" else "🏢 Flat Sale", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = primaryColor,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = propertyType == "land",
                        onClick = { propertyType = "land" },
                        modifier = Modifier.weight(1f),
                        label = {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                                Text(if (isBengali) "🏞️ জমি বিক্রি" else "🏞️ Land Sale", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF0F766E),
                            selectedLabelColor = Color.White
                        )
                    )
                }

                // Photos Picker Section
                Text(
                    text = if (isBengali) "ছবি সমুহ যুক্ত করুন (সর্বোচ্চ ৫টি)" else "Add Photos (Max 5)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFE2E8F0),
                            modifier = Modifier
                                .size(90.dp)
                                .clickable { photoPickerLauncher.launch(androidx.activity.result.PickVisualMediaRequest(androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = primaryColor, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(if (isBengali) "ছবি যোগ করুন" else "Add Photo", fontSize = 10.sp, color = primaryColor, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                    items(selectedImages) { uri ->
                        Box(modifier = Modifier.size(90.dp)) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { selectedImages = selectedImages.filter { it != uri } },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                                    .background(Color.Red, CircleShape)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(if (isBengali) "শিরোনাম *" else "Title *", fontSize = 14.sp) },
                    placeholder = {
                        Text(
                            if (propertyType == "flat")
                                (if (isBengali) "যেমন: ১২২০ স্কয়ার ফিটের আকর্ষণীয় ফ্ল্যাট বিক্রি হবে" else "e.g., 1220 sqft Flat for Sale")
                            else
                                (if (isBengali) "যেমন: ৫ কাঠা আবাসিক জমি বিক্রি হবে" else "e.g., 5 Katha Land for Sale"),
                            fontSize = 13.sp
                        )
                    },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                if (propertyType == "flat") {
                    // Flat fields: roomSize & floor
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = roomSize,
                            onValueChange = { roomSize = it },
                            label = { Text(if (isBengali) "সাইজ (sqft) *" else "Size (sqft) *", fontSize = 13.sp) },
                            placeholder = { Text("১২২০ স্কয়ার ফিট", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                        OutlinedTextField(
                            value = floor,
                            onValueChange = { floor = it },
                            label = { Text(if (isBengali) "তলা *" else "Floor *", fontSize = 13.sp) },
                            placeholder = { Text("৫ম তলা", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                    }

                    // Beds, Baths, Balcony
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = beds,
                            onValueChange = { beds = it },
                            label = { Text(if (isBengali) "বেডরুম" else "Bedrooms", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                        OutlinedTextField(
                            value = baths,
                            onValueChange = { baths = it },
                            label = { Text(if (isBengali) "বাথরুম" else "Bathrooms", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                        OutlinedTextField(
                            value = balcony,
                            onValueChange = { balcony = it },
                            label = { Text(if (isBengali) "বারান্দা" else "Balcony", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                    }
                } else {
                    // Land fields: area & roadWidth
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = area,
                            onValueChange = { area = it },
                            label = { Text(if (isBengali) "জমির পরিমাণ *" else "Land Area *", fontSize = 13.sp) },
                            placeholder = { Text("৫ কাঠা / ১০ শতাংশ", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                        OutlinedTextField(
                            value = roadWidth,
                            onValueChange = { roadWidth = it },
                            label = { Text(if (isBengali) "সংলগ্ন রাস্তা *" else "Road Width *", fontSize = 13.sp) },
                            placeholder = { Text("২০ ফুট", fontSize = 13.sp) },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                        )
                    }

                    // Land Type selection
                    Text(if (isBengali) "জমির ধরন *" else "Land Type *", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("আবাসিক", "বাণিজ্যিক", "কৃষি").forEach { type ->
                            FilterChip(
                                selected = landType == type,
                                onClick = { landType = type },
                                label = { Text(type, fontSize = 12.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = registrationStatus,
                        onValueChange = { registrationStatus = it },
                        label = { Text(if (isBengali) "কাগজপত্রের অবস্থা *" else "Paper Status *", fontSize = 13.sp) },
                        placeholder = { Text("সব কাগজপত্র আপডেট / নামজারি করা", fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                // Price Input
                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text(if (isBengali) "মূল্য/দাম *" else "Price *", fontSize = 14.sp) },
                    placeholder = { Text("৳ ৮০ লক্ষ / ৳ ১.৫ কোটি", fontSize = 13.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Location & Thana
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text(if (isBengali) "অবস্থান / এলাকা *" else "Location *", fontSize = 13.sp) },
                        placeholder = { Text("মিরপুর ডিওএইচএস, ঢাকা", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                    OutlinedTextField(
                        value = thana,
                        onValueChange = { thana = it },
                        label = { Text(if (isBengali) "থানা / উপজেলা *" else "Thana *", fontSize = 13.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                    )
                }

                // Phone Input
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (isBengali) "যোগাযোগের ফোন নম্বর *" else "Contact Phone *", fontSize = 14.sp) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(if (isBengali) "বিস্তারিত বিবরণ" else "Description / Details", fontSize = 14.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
                )

                // Location Map Picker Button
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
                        text = if (isBengali) "আপনার লোকেশন ম্যাপ থেকে সেট করুন" else "Set location from map",
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
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
            }

            // Bottom Action Bar
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
                            val formattedPrice = if (price.startsWith("৳")) price else "৳$price"

                            if (title.isBlank() || phone.isBlank() || location.isBlank() || price.isBlank()) {
                                Toast.makeText(context, if (isBengali) "দয়া করে প্রয়োজনীয় ঘরগুলো পূরণ করুন" else "Please fill required fields", Toast.LENGTH_SHORT).show()
                            } else {
                                isSubmitting = true

                                coroutineScope.launch {
                                    val imageUrlList = propertyRepo.uploadMultipleImagesToStorage(context, selectedImages, "properties").getOrDefault(emptyList())

                                    val dto = PropertyDto(
                                        propertyType = propertyType,
                                        title = title,
                                        description = description,
                                        location = location,
                                        thana = thana,
                                        latLng = if (selectedLat != null && selectedLng != null) String.format("%.5f,%.5f", selectedLat, selectedLng) else "",
                                        price = formattedPrice,
                                        phone = phone,
                                        images = imageUrlList,
                                        roomSize = roomSize,
                                        floor = floor,
                                        beds = beds.toIntOrNull() ?: 0,
                                        baths = baths.toIntOrNull() ?: 0,
                                        dining = dining.toIntOrNull() ?: 0,
                                        drawing = drawing.toIntOrNull() ?: 0,
                                        balcony = balcony.toIntOrNull() ?: 0,
                                        area = area,
                                        roadWidth = roadWidth,
                                        landType = landType,
                                        registrationStatus = registrationStatus
                                    )

                                    val result = propertyRepo.savePropertyPostToFirestore(dto)
                                    isSubmitting = false
                                    if (result.isSuccess) {
                                        showSuccessDialog = true
                                    } else {
                                        Toast.makeText(
                                            context,
                                            if (isBengali) "পোস্ট প্রকাশ করতে সমস্যা হয়েছে: ${result.exceptionOrNull()?.localizedMessage}" else "Failed to submit post",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
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
                            Text(if (isBengali) "পোস্ট প্রকাশ করুন" else "Publish Post", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (showSuccessDialog) {
            CustomDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onPostCreated()
                    onBack()
                },
                title = if (isBengali) "পোস্ট সফলভাবে জমা হয়েছে!" else "Post Submitted Successfully!",
                icon = Icons.Default.Check,
                iconTint = Color(0xFF16A34A),
                iconBackgroundColor = Color(0xFFDCFCE7),
                confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
                onConfirm = {
                    showSuccessDialog = false
                    onPostCreated()
                    onBack()
                }
            ) {
                Text(
                    text = if (isBengali)
                        "আপনার প্রপার্টি পোস্টটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর পোস্টটি প্রকাশিত হবে।"
                    else
                        "Your property post has been submitted for admin review. It will be published once approved.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
