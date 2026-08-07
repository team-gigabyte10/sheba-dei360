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
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.barisal.cityservice.data.model.ChamberDto
import com.barisal.cityservice.data.model.DoctorDto
import com.barisal.cityservice.data.repository.DoctorRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDoctorScreen(
    initialCategory: String = "",
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val doctorRepository = remember { DoctorRepository() }

    val selectedCategory = remember(initialCategory, isBengali) {
        initialCategory.ifEmpty { if (isBengali) "মেডিসিন" else "Medicine" }
    }

    var doctorName by remember { mutableStateOf("") }
    var specialization by remember { mutableStateOf("") }
    var education by remember { mutableStateOf("") }
    var treatments by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var latLng by remember { mutableStateOf("22.7010,90.3535") }

    // Doctor Image State
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    // Location Map picker state
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

    // Multi-chamber state with at least 1 default chamber
    var chambers by remember {
        mutableStateOf(
            listOf(ChamberDto(chamberName = "", address = "", visitingTime = ""))
        )
    }

    var isSubmitting by remember { mutableStateOf(false) }

    SetStatusBarColor()

    val primaryColor = Color(0xFF00897B)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "নতুন ডাক্তার যুক্ত করুন ($selectedCategory)" else "Post Doctor Info ($selectedCategory)",
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
                    text = if (isBengali) "ডাক্তারের তথ্য যুক্ত করুন ($selectedCategory)" else "Add Doctor Details ($selectedCategory)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // Doctor Image Upload UI
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
                                contentDescription = "Doctor Photo",
                                modifier = Modifier
                                    .size(110.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, primaryColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) "ছবি পরিবর্তন করতে ট্যাপ করুন" else "Tap to change photo",
                                fontSize = 12.sp,
                                color = primaryColor,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE0F2FE)),
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
                                text = if (isBengali) "ডাক্তারের ছবি যোগ করুন (ঐচ্ছিক)" else "Add Doctor Photo (Optional)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor
                            )
                        }
                    }
                }
            }

            // Doctor Name
            item {
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = { Text(if (isBengali) "ডাক্তারের নাম *" else "Doctor Name *") },
                    placeholder = { Text(if (isBengali) "যেমন: ডাঃ মোঃ গোলাম সরোয়ার" else "e.g. Dr. Md. Golam Sarwar") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Specialization
            item {
                OutlinedTextField(
                    value = specialization,
                    onValueChange = { specialization = it },
                    label = { Text(if (isBengali) "বিশেষজ্ঞ (Specialization) *" else "Specialization *") },
                    placeholder = { Text(if (isBengali) "যেমন: হৃদরোগ ও মেডিসিন বিশেষজ্ঞ" else "e.g. Cardiologist & Medicine Specialist") },
                    leadingIcon = { Icon(Icons.Default.MedicalServices, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Educational Qualification
            item {
                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text(if (isBengali) "শিক্ষাগত যোগ্যতা *" else "Education / Degrees *") },
                    placeholder = { Text(if (isBengali) "যেমন: এম.বি.বি.এস, বি.সি.এস, এফ.সি.পি.এস" else "e.g. MBBS, BCS (Health), FCPS") },
                    leadingIcon = { Icon(Icons.Default.School, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Multiple Chambers / Workplaces Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBengali) "কর্মস্থল ও চেম্বার তথ্য (ডিউটি টাইম সহ)" else "Workplace & Chamber Info (With Duty Time)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    IconButton(
                        onClick = {
                            chambers = chambers + ChamberDto(chamberName = "", address = "", visitingTime = "")
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.AddCircle,
                            contentDescription = "Add Chamber",
                            tint = primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            itemsIndexed(chambers) { index, chamber ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${if (isBengali) "চেম্বার / কর্মস্থল #" else "Chamber / Workplace #"} ${index + 1}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF334155)
                            )
                            if (chambers.size > 1) {
                                IconButton(
                                    onClick = {
                                        chambers = chambers.filterIndexed { i, _ -> i != index }
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Remove Chamber",
                                        tint = Color.Red
                                    )
                                }
                            }
                        }

                        // Hospital / Chamber Name
                        OutlinedTextField(
                            value = chamber.chamberName,
                            onValueChange = { newName ->
                                chambers = chambers.mapIndexed { i, item ->
                                    if (i == index) item.copy(chamberName = newName) else item
                                }
                            },
                            label = { Text(if (isBengali) "হাসপাতাল / চেম্বারের নাম *" else "Hospital / Chamber Name *") },
                            placeholder = { Text(if (isBengali) "যেমন: শের-ই-বাংলা মেডিকেল কলেজ" else "e.g. Sher-e-Bangla Medical College") },
                            leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        // Chamber Address
                        OutlinedTextField(
                            value = chamber.address,
                            onValueChange = { newAddr ->
                                chambers = chambers.mapIndexed { i, item ->
                                    if (i == index) item.copy(address = newAddr) else item
                                }
                            },
                            label = { Text(if (isBengali) "চেম্বারের ঠিকানা" else "Chamber Address") },
                            placeholder = { Text(if (isBengali) "যেমন: বান্দ রোড, বরিশাল" else "e.g. Band Road, Barisal") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )

                        // Duty / Visiting Time
                        OutlinedTextField(
                            value = chamber.visitingTime,
                            onValueChange = { newTime ->
                                chambers = chambers.mapIndexed { i, item ->
                                    if (i == index) item.copy(visitingTime = newTime) else item
                                }
                            },
                            label = { Text(if (isBengali) "ডিউটি / রোগী দেখার সময় *" else "Duty / Visiting Time *") },
                            placeholder = { Text(if (isBengali) "যেমন: শনি - বৃহস্পতি: বিকাল ৪টা - রাত ৮টা" else "e.g. Sat - Thu: 4:00 PM - 8:00 PM") },
                            leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = primaryColor) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor,
                                unfocusedBorderColor = Color.LightGray
                            )
                        )
                    }
                }
            }

            item {
                OutlinedButton(
                    onClick = {
                        chambers = chambers + ChamberDto(chamberName = "", address = "", visitingTime = "")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = primaryColor)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBengali) "আরও চেম্বার / কর্মস্থল যোগ করুন (+)" else "Add More Chamber / Workplace (+)",
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Treatments / Services
            item {
                OutlinedTextField(
                    value = treatments,
                    onValueChange = { treatments = it },
                    label = { Text(if (isBengali) "চিকিৎসা ও সেবাসমূহ" else "Treatments & Services") },
                    placeholder = { Text(if (isBengali) "যেমন: ইসিজি, ইকোকার্ডিওগ্রাফি, ইটিটি" else "e.g. ECG, Echocardiography, ETT") },
                    leadingIcon = { Icon(Icons.Default.Star, contentDescription = null, tint = primaryColor) },
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
                    label = { Text(if (isBengali) "যোগাযোগ / ফোন নম্বর *" else "Contact / Phone Number *") },
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

            // Google Maps Location Picker
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isBengali) "📍 ম্যাপে ট্যাপ করে অথবা পিন ড্র্যাগ-ড্রপ করে অবস্থান সিলেক্ট করুন" else "📍 Tap on map or drag & drop pin to select location",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = primaryColor
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(290.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                    ) {
                        GoogleMap(
                            modifier = Modifier.fillMaxSize(),
                            cameraPositionState = cameraPositionState,
                            properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                            uiSettings = MapUiSettings(
                                myLocationButtonEnabled = true,
                                zoomControlsEnabled = true,
                                compassEnabled = true
                            ),
                            onMapClick = { latLngPoint ->
                                markerState.position = latLngPoint
                            }
                        ) {
                            Marker(
                                state = markerState,
                                draggable = true,
                                title = doctorName.ifBlank { if (isBengali) "ডাক্তারের চেম্বার" else "Doctor Chamber" },
                                snippet = if (isBengali) "পিন ড্র্যাগ করে সঠিক স্থান সিলেক্ট করুন" else "Drag pin to select exact location"
                            )
                        }
                    }

                    OutlinedTextField(
                        value = latLng,
                        onValueChange = { latLng = it },
                        label = { Text(if (isBengali) "ম্যাপ কো-অর্ডিনেট (LatLng)" else "Map Location Coordinates (LatLng)") },
                        placeholder = { Text("22.7010,90.3535") },
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = primaryColor) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )
                }
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (doctorName.isBlank() || selectedCategory.isBlank() || contactInfo.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "অনুগ্রহ করে ডাক্তারের নাম, ক্যাটাগরি এবং ফোন নম্বর পূরণ করুন" else "Please fill doctor name, category and contact info",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        val validChambers = chambers.filter { it.chamberName.isNotBlank() }
                        if (validChambers.isEmpty()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "কমপক্ষে একটি চেম্বার/হাসপাতাল এর নাম এবং ডিউটি টাইম দিন" else "Please provide at least one chamber/hospital name & visiting time",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            var base64Image = ""
                            if (selectedImageUri != null) {
                                val compressResult = doctorRepository.compressImageToBase64(context, selectedImageUri!!)
                                if (compressResult.isSuccess) {
                                    base64Image = compressResult.getOrDefault("")
                                }
                            }

                            val doctorDto = DoctorDto(
                                categoryName = selectedCategory,
                                name = doctorName.trim(),
                                specialization = specialization.trim(),
                                education = education.trim(),
                                chambers = validChambers,
                                treatments = treatments.trim(),
                                latLng = latLng.trim(),
                                contactInfo = contactInfo.trim(),
                                zilla = selectedZilla,
                                imageUrl = base64Image
                            )

                            val result = doctorRepository.saveDoctorToFirestore(doctorDto)
                            isSubmitting = false

                            if (result.isSuccess) {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "আপনার পোস্টটি জমা নেওয়া হয়েছে! অ্যাডমিন অনুমোদনের পর প্রকাশিত হবে।" else "Post submitted! Will be published after admin approval.",
                                    Toast.LENGTH_LONG
                                ).show()
                                onBack()
                            } else {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "সংরক্ষণ করতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}" else "Failed to save: ${result.exceptionOrNull()?.message}",
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
    }
}
