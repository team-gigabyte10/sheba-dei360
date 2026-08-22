package com.barisal.cityservice.feature.hotel

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.barisal.cityservice.data.model.HotelDto
import com.barisal.cityservice.data.model.HotelRoomDto
import com.barisal.cityservice.data.repository.HotelRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

data class RoomInputState(
    var id: String = java.util.UUID.randomUUID().toString(),
    var roomType: String = "",
    var pricePerNight: String = "",
    var bedType: String = "",
    var capacity: String = "",
    val amenities: MutableList<String> = mutableStateListOf(),
    val roomImages: MutableList<Uri> = mutableStateListOf()
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PostHotelScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { HotelRepository() }

    val primaryColor = Color(0xFF0F766E) // Teal theme

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(if (isBengali) "আবাসিক হোটেল" else "Residential Hotel") }
    var typeDropdownExpanded by remember { mutableStateOf(false) }
    val hotelTypeList = listOf("আবাসিক হোটেল", "রিসোর্ট ও কটেজ", "রেস্ট হাউস")

    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var pricePerNight by remember { mutableStateOf("") }

    var selectedCoverImage by remember { mutableStateOf<Uri?>(null) }
    val roomInputs = remember { mutableStateListOf<RoomInputState>() }

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

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "হোটেল তথ্য পোস্ট করুন" else "Post Hotel Information",
                onBackClick = onBack
            )
        }
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
                        Icon(Icons.Default.Hotel, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBengali) "হোটেল ও রুমের বিজ্ঞাপন দিন" else "Post Hotel & Rooms Listing",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = if (isBengali) "আপনার হোটেলের সঠিক তথ্য ও রুমের ছবি যুক্ত করুন" else "Add details and room photos for your hotel",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Hotel Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "হোটেলের নাম *" else "Hotel Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )

                // Hotel Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeDropdownExpanded,
                    onExpandedChange = { typeDropdownExpanded = !typeDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "হোটেলের ধরন *" else "Hotel Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    ExposedDropdownMenu(
                        expanded = typeDropdownExpanded,
                        onDismissRequest = { typeDropdownExpanded = false }
                    ) {
                        hotelTypeList.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(text = type) },
                                onClick = {
                                    selectedType = type
                                    typeDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Starting Price / Contact Phone
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = pricePerNight,
                        onValueChange = { pricePerNight = it },
                        label = { Text(if (isBengali) "শুরুর ভাড়া (৳/রাত)" else "Price / Night") },
                        placeholder = { Text("৳২,৫০০") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text(if (isBengali) "ফোন নম্বর *" else "Contact Phone *") },
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
                        label = { Text(if (isBengali) "থানা *" else "Thana *") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                }

                // Map Location Picker
                OutlinedButton(
                    onClick = { showLocationPickerMap = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = primaryColor)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isBengali) "ম্যাপে হোটেলের অবস্থান নির্বাচন করুন" else "Pick Location on Map", color = primaryColor)
                }

                // Hotel Cover Photo
                Text(if (isBengali) "হোটেলের কভার ছবি *" else "Hotel Cover Photo *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable { coverPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                    contentAlignment = Alignment.Center
                ) {
                    if (selectedCoverImage != null) {
                        AsyncImage(model = selectedCoverImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                            Text(if (isBengali) "কভার ছবি আপলোড করুন" else "Upload Cover Photo", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }

                // Hotel Rooms Section
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isBengali) "হোটেল রুম/কক্ষ তালিকা" else "Hotel Rooms List", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                    Button(
                        onClick = { roomInputs.add(RoomInputState()) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "রুম যোগ করুন +" else "Add Room +", fontSize = 13.sp)
                    }
                }

                if (roomInputs.isEmpty()) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "কোন রুম যুক্ত করা হয়নি। 'রুম যোগ করুন' বাটনে চাপ দিন।" else "No rooms added yet. Tap 'Add Room' button.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                roomInputs.forEachIndexed { index, room ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "${if (isBengali) "রুম #" else "Room #"}${index + 1}", fontWeight = FontWeight.Bold, color = primaryColor)
                                TextButton(onClick = { roomInputs.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                    Text(if (isBengali) "সরান" else "Remove", color = Color.Red, fontSize = 12.sp)
                                }
                            }

                            OutlinedTextField(
                                value = room.roomType,
                                onValueChange = { room.roomType = it },
                                label = { Text(if (isBengali) "রুমের ধরন (যেমন: সিঙ্গেল এসি/ডাবল ডিল্যাক্স)" else "Room Type (e.g. Single AC)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = room.pricePerNight,
                                    onValueChange = { room.pricePerNight = it },
                                    label = { Text(if (isBengali) "ভাড়া (৳/রাত)" else "Price/Night") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = room.capacity,
                                    onValueChange = { room.capacity = it },
                                    label = { Text(if (isBengali) "ধারণক্ষমতা" else "Capacity") },
                                    placeholder = { Text("২ জন") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Amenities Multi-Select Chips
                            Text(if (isBengali) "রুমের সুযোগ-সুবিধা:" else "Amenities:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            val amenityOptions = listOf("WiFi", "AC", "TV", "গরম পানি", "সকালের নাস্তা", "রুম সার্ভিস")
                            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                amenityOptions.forEach { amenity ->
                                    val isChecked = room.amenities.contains(amenity)
                                    FilterChip(
                                        selected = isChecked,
                                        onClick = {
                                            if (isChecked) room.amenities.remove(amenity) else room.amenities.add(amenity)
                                        },
                                        label = { Text(amenity, fontSize = 12.sp) },
                                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = primaryColor, selectedLabelColor = Color.White)
                                    )
                                }
                            }

                            // Room Photos Picker
                            val roomPhotoPickerLauncher = rememberLauncherForActivityResult(
                                contract = ActivityResultContracts.PickMultipleVisualMedia()
                            ) { uris: List<Uri> ->
                                room.roomImages.addAll(uris)
                            }

                            Text(if (isBengali) "রুমের ছবিসমূহ:" else "Room Photos:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .size(70.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFFF1F5F9))
                                            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                            .clickable { roomPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = primaryColor)
                                    }
                                }
                                items(room.roomImages) { uri ->
                                    Box(modifier = Modifier.size(70.dp).clip(RoundedCornerShape(8.dp))) {
                                        AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Submit Button Footer
            Surface(
                shadowElevation = 8.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        if (name.isBlank() || address.isBlank() || contact.isBlank()) {
                            Toast.makeText(context, if (isBengali) "অনুগ্রহ করে সব প্রয়োজনীয় তথ্য পূরণ করুন" else "Please fill all required fields", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            try {
                                val coverUrl = if (selectedCoverImage != null) {
                                    repository.uploadImageToStorage(context, selectedCoverImage!!, "hotels/covers").getOrDefault("")
                                } else ""

                                val roomsDtoList = mutableListOf<HotelRoomDto>()
                                for (r in roomInputs) {
                                    val roomImageUrls = if (r.roomImages.isNotEmpty()) {
                                        repository.uploadMultipleImagesToStorage(context, r.roomImages, "hotels/rooms").getOrDefault(emptyList())
                                    } else emptyList()
                                    roomsDtoList.add(
                                        HotelRoomDto(
                                            id = r.id,
                                            roomType = r.roomType,
                                            pricePerNight = r.pricePerNight,
                                            bedType = r.bedType,
                                            capacity = r.capacity,
                                            amenities = r.amenities.toList(),
                                            roomImages = roomImageUrls
                                        )
                                    )
                                }

                                val hotelDto = HotelDto(
                                    name = name,
                                    type = selectedType,
                                    address = address,
                                    thana = thana,
                                    contact = contact,
                                    pricePerNight = pricePerNight,
                                    coverImage = coverUrl,
                                    rooms = roomsDtoList,
                                    latitude = selectedLocation.latitude,
                                    longitude = selectedLocation.longitude
                                )

                                val result = repository.saveHotelToFirestore(hotelDto)
                                isSubmitting = false
                                if (result.isSuccess) {
                                    showSuccessDialog = true
                                } else {
                                    Toast.makeText(context, "Failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                                }
                            } catch (e: Exception) {
                                isSubmitting = false
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = !isSubmitting,
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(50.dp)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isBengali) "হোটেল তথ্য সাবমিট করুন" else "Submit Hotel Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                title = if (isBengali) "সফল হয়েছে!" else "Success!",
                confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
                onConfirm = {
                    showSuccessDialog = false
                    onBack()
                }
            ) {
                Text(
                    text = if (isBengali) "আপনার পোস্টটি ভেরিফিকেশনের জন্য এডমিনের নিকট জমা দেওয়া হয়েছে। এডমিন ভেরিফাই করার পর অ্যাপে প্রদর্শিত হবে।" else "Your post has been submitted for admin verification. It will appear after approval.",
                    fontSize = 14.sp
                )
            }
        }
    }
}
