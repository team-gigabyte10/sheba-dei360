package com.barisal.cityservice.feature.doctor

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.barisal.cityservice.data.model.HealthServiceDto
import com.barisal.cityservice.data.repository.HealthServiceRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

data class HealthSubCategoryOption(
    val key: String,
    val labelBan: String,
    val labelEng: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostHealthServiceScreen(
    initialCategory: String = "hospital",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { HealthServiceRepository() }

    val selectedCategoryKey = remember(initialCategory) {
        initialCategory.ifBlank { "hospital" }
    }

    var serviceName by remember { mutableStateOf("") }
    var serviceType by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var latLng by remember { mutableStateOf("22.7010,90.3535") }

    // Service Image State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Google Maps Location Picker state
    var selectedLocation by remember { mutableStateOf(LatLng(22.7010, 90.3535)) }
    val markerState = rememberMarkerState(position = selectedLocation)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 15f) // Standard 15f zoom
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

    val primaryColor = Color(0xFF0D9488)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "স্বাস্থ্য সেবা পোস্ট করুন" else "Post Healthcare Service",
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
                    text = if (isBengali) "নতুন স্বাস্থ্য সেবার তথ্য যুক্ত করুন" else "Add Healthcare Service Details",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // Image Upload UI
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { imagePickerLauncher.launch("image/*") },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (selectedImageUri != null) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Service Photo",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(2.dp, primaryColor, RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "ছবি পরিবর্তন করতে ট্যাপ করুন" else "Tap to change photo",
                                fontSize = 14.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(76.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCCFBF1)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AddAPhoto,
                                    contentDescription = "Add Photo",
                                    tint = primaryColor,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "সেবার ছবি বা ব্যানার যুক্ত করুন (ঐচ্ছিক)" else "Add Service Photo or Banner (Optional)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                    }
                }
            }

            // Service Name
            item {
                val isHomeCare = selectedCategoryKey == "home_care"
                OutlinedTextField(
                    value = serviceName,
                    onValueChange = { serviceName = it },
                    label = {
                        Text(
                            if (isHomeCare) {
                                if (isBengali) "আপনার নাম / সেবার নাম *" else "Your Name / Service Title *"
                            } else {
                                if (isBengali) "প্রতিষ্ঠান বা সেবার নাম *" else "Organization / Service Name *"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            if (isHomeCare) {
                                if (isBengali) "যেমন: নার্স আমেনা বেগম / অ্যাসিস্ট্যান্ট তারেক" else "e.g. Nurse Amena Begum / Med Assistant Tareq"
                            } else {
                                if (isBengali) "যেমন: বরিশাল অ্যাপোলো হাসপাতাল / গ্রীন অ্যাম্বুলেন্স" else "e.g. Barisal General Hospital / Green Ambulance"
                            }
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Service Type / Sub-Type
            item {
                val isHomeCare = selectedCategoryKey == "home_care"
                OutlinedTextField(
                    value = serviceType,
                    onValueChange = { serviceType = it },
                    label = {
                        Text(
                            if (isHomeCare) {
                                if (isBengali) "পদবী ও যোগ্যতা (Designation/Role) *" else "Designation & Qualification *"
                            } else {
                                if (isBengali) "সেবার বিবরণী (টাইপ) *" else "Service Type / Category Detail *"
                            }
                        )
                    },
                    placeholder = {
                        Text(
                            if (isHomeCare) {
                                if (isBengali) "যেমন: সিনিয়র নার্স (B.Sc) / মেডিকেল অ্যাসিস্ট্যান্ট (MATS)" else "e.g. Senior Nurse (B.Sc) / Med Assistant (MATS)"
                            } else {
                                if (isBengali) "যেমন: সরকারি হাসপাতাল / আইসিইউ অ্যাম্বুলেন্স / ও+ রক্ত" else "e.g. Private Hospital / ICU Ambulance / O+ Blood"
                            }
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Address
            item {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isBengali) "বাসা/এলাকার ঠিকানা *" else "Home / Area Address *") },
                    placeholder = { Text(if (isBengali) "যেমন: নথুল্লাবাদ, বরিশাল সদর" else "e.g. Nathullabad, Barisal Sadar") },
                    leadingIcon = { Icon(Icons.Default.HomeWork, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // District / Zilla Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = zillaDropdownExpanded,
                    onExpandedChange = { zillaDropdownExpanded = !zillaDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedZilla,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "জেলা *" else "District / Zilla *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zillaDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
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
            }

            // Contact Info
            item {
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text(if (isBengali) "যোগাযোগ / জরুরি ফোন নম্বর *" else "Emergency Contact / Phone Number *") },
                    placeholder = { Text("01711XXXXXX") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = primaryColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Details & Description
            item {
                val isHomeCare = selectedCategoryKey == "home_care"
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(if (isBengali) "সেবাসমূহ, সময়সূচী ও ফি *" else "Services, Available Timing & Fee *") },
                    placeholder = {
                        Text(
                            if (isHomeCare) {
                                if (isBengali) "ইনজেকশন/স্যালাইন, ড্রেসিং। সময়: অফিস সময়ের পর (বিকাল ৫টা-১০টা)। ফি: ৳৩০০।" else "Injection, Dressing. Timing: After office hours (5 PM - 10 PM). Fee: 300 BDT."
                            } else {
                                if (isBengali) "২৪ ঘন্টা খোলা, ফ্রি অক্সিজেন সুবিধা, সার্বক্ষণিক ডাক্তার..." else "24 Hours Open, Free Oxygen facility, Resident doctors..."
                            }
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Google Maps Location Picker
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
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
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (serviceName.isBlank() || contactInfo.isBlank() || address.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "অনুগ্রহ করে সেবার নাম, ঠিকানা এবং ফোন নম্বর পূরণ করুন" else "Please fill service name, address and phone number",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            var imageUrl = ""
                            if (selectedImageUri != null) {
                                val uploadResult = repository.uploadImageToStorage(context, selectedImageUri!!, "health_services")
                                if (uploadResult.isSuccess) {
                                    imageUrl = uploadResult.getOrDefault("")
                                }
                            }

                            val serviceDto = HealthServiceDto(
                                categoryKey = selectedCategoryKey,
                                name = serviceName.trim(),
                                type = serviceType.trim(),
                                address = address.trim(),
                                zilla = selectedZilla,
                                contactInfo = contactInfo.trim(),
                                latLng = latLng.trim(),
                                imageUrl = imageUrl,
                                details = details.trim()
                            )

                            val result = repository.saveHealthServiceToFirestore(serviceDto)
                            isSubmitting = false

                            if (result.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "সংরক্ষণ করতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}" else "Failed to post: ${result.exceptionOrNull()?.message}",
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
                        Text(
                            text = if (isBengali) "পোস্ট জমা দিন" else "Submit Post",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
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
                        "আপনার স্বাস্থ্য সেবার পোস্টটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর পোস্টটি প্রকাশিত হবে।"
                    else
                        "Your healthcare service post has been submitted for admin review. It will be published once approved.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
