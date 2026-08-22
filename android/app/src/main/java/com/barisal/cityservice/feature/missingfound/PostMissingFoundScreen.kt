package com.barisal.cityservice.feature.missingfound

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
import com.barisal.cityservice.data.model.MissingFoundDto
import com.barisal.cityservice.data.repository.MissingFoundRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMissingFoundScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repo = remember { MissingFoundRepository() }

    var noticeType by remember { mutableStateOf("missing") } // "missing" or "found"
    var title by remember { mutableStateOf("") }
    var subCategory by remember { mutableStateOf(if (isBengali) "নিখোঁজ ব্যক্তি" else "Missing Person") }
    var nameOrItem by remember { mutableStateOf("") }
    var ageOrDetails by remember { mutableStateOf("") }
    var incidentDate by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var rewardOrNote by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var base64Image by remember { mutableStateOf("") }

    var selectedLat by remember { mutableStateOf<Double?>(null) }
    var selectedLng by remember { mutableStateOf<Double?>(null) }
    var showMapPicker by remember { mutableStateOf(false) }

    var subCategoryDropdownExpanded by remember { mutableStateOf(false) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isMissing = noticeType == "missing"
    val themeColor = if (isMissing) Color(0xFFDC2626) else Color(0xFF16A34A)

    val subCategoryOptions = remember(isBengali) {
        if (isBengali) {
            listOf("নিখোঁজ ব্যক্তি", "নিখোঁজ শিশু", "নিখোঁজ বয়স্ক", "হারানো কাগজপত্র / আইডি", "হারানো মানিব্যাগ / টাকা", "হারানো মোবাইল / ডিভাইস", "পাওয়া গেছে (ব্যক্তি/জিনিস)")
        } else {
            listOf("Missing Person", "Missing Child", "Missing Elderly", "Lost Documents / ID", "Lost Wallet / Cash", "Lost Electronics", "Found Item / Person")
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
                title = if (isBengali) "নিখোঁজ/প্রাপ্তি বিজ্ঞপ্তি পোস্ট করুন" else "Post Missing or Found Notice",
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
                        text = if (isBengali) "বিজ্ঞপ্তির ধরন নির্বাচন করুন *" else "Select Notice Type *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF0F172A)
                    )

                    // Notice Type Radio Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = noticeType == "missing",
                            onClick = { noticeType = "missing" },
                            label = { Text(if (isBengali) "নিখোঁজ (Missing)" else "Missing", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFDC2626),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = noticeType == "found",
                            onClick = { noticeType = "found" },
                            label = { Text(if (isBengali) "পাওয়া গেছে (Found)" else "Found", fontSize = 13.sp, fontWeight = FontWeight.Bold) },
                            leadingIcon = { Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp)) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF16A34A),
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }

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
                                if (isMissing) {
                                    if (isBengali) "বিজ্ঞপ্তির শিরোনাম (যেমন: ৭ বছরের শিশু নিখোঁজ) *" else "Notice Title (e.g. 7yo Child Missing) *"
                                } else {
                                    if (isBengali) "বিজ্ঞপ্তির শিরোনাম (যেমন: মানিব্যাগ পাওয়া গেছে) *" else "Notice Title (e.g. Wallet Found) *"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Name / Item Name & Age/Details
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = nameOrItem,
                            onValueChange = { nameOrItem = it },
                            label = { Text(if (isBengali) "ব্যক্তি/জিনিসের নাম *" else "Name of Person / Item *") },
                            modifier = Modifier.weight(1.2f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = ageOrDetails,
                            onValueChange = { ageOrDetails = it },
                            label = { Text(if (isBengali) "বয়স/বর্ণনা" else "Age / Specifics") },
                            modifier = Modifier.weight(0.8f),
                            singleLine = true
                        )
                    }

                    // Date & Location of Incident
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = incidentDate,
                            onValueChange = { incidentDate = it },
                            label = { Text(if (isBengali) "তারিখ (যেমন: ১০ আগস্ট ২০২৬)" else "Date of Incident") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = location,
                            onValueChange = { location = it },
                            label = { Text(if (isBengali) "স্থান/এলাকা *" else "Location/Area *") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    // Contact Phone & WhatsApp
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = contact,
                            onValueChange = { contact = it },
                            label = { Text(if (isBengali) "যোগাযোগের ফোন *" else "Contact Phone *") },
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

                    // Reward / Note
                    OutlinedTextField(
                        value = rewardOrNote,
                        onValueChange = { rewardOrNote = it },
                        label = { Text(if (isBengali) "পুরস্কার / বিশেষ নির্দেশিকা (ঐচ্ছিক)" else "Reward / Note (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text(if (isBengali) "বিস্তারিত বিবরণ (পোশাকের রং, সনাক্তকারী চিহ্ন...)" else "Full Description (Clothing, Identification marks...)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        maxLines = 4
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
                                    if (isBengali) "ঘটনা/প্রাপ্তির স্থান ম্যাপে নির্দিষ্ট করুন" else "Pick incident location on Map"
                                },
                                fontSize = 13.sp,
                                color = if (selectedLat != null) themeColor else Color.DarkGray,
                                fontWeight = if (selectedLat != null) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    // Photo Selector
                    Text(
                        text = if (isBengali) "ছবি (ব্যক্তি বা বস্তুর ছবি - সুপারিশকৃত)" else "Photo (Recommended)",
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
                            if (title.isBlank() || nameOrItem.isBlank() || location.isBlank() || contact.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সকল আবশ্যক তথ্য দিন" else "Please fill in required fields (*)", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isSubmitting = true
                            coroutineScope.launch {
                                val photoUrl = if (selectedImageUri != null) {
                                    repo.uploadImageToStorage(context, selectedImageUri!!, "missing_found").getOrDefault("")
                                } else ""

                                val itemDto = MissingFoundDto(
                                    noticeType = noticeType,
                                    title = title.trim(),
                                    subCategory = subCategory,
                                    nameOrItem = nameOrItem.trim(),
                                    ageOrDetails = ageOrDetails.trim(),
                                    incidentDate = incidentDate.trim(),
                                    location = location.trim(),
                                    contact = contact.trim(),
                                    whatsapp = whatsapp.trim(),
                                    rewardOrNote = rewardOrNote.trim(),
                                    description = description.trim(),
                                    photo = photoUrl,
                                    latitude = selectedLat,
                                    longitude = selectedLng
                                )
                                val result = repo.saveMissingFoundPost(itemDto)
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
                            Text(if (isBengali) "বিজ্ঞপ্তি জমা দিন" else "Submit Notice for Approval", fontSize = 15.sp, fontWeight = FontWeight.Bold)
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
            title = { Text(if (isBengali) "বিজ্ঞপ্তি সফলভাবে জমা দেওয়া হয়েছে!" else "Notice Submitted Successfully!") },
            text = { Text(if (isBengali) "আপনার বিজ্ঞপ্তি অ্যাডমিন পর্যালোচনার জন্য জমা রাখা হয়েছে। অনুমোদনের পর প্রকাশিত হবে।" else "Your notice is pending admin review. It will be published after approval.") },
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
