package com.barisal.cityservice.feature.shopping

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.CameraPosition
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager
import androidx.compose.ui.platform.LocalContext
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

import android.widget.Toast
import com.barisal.cityservice.data.model.ProductDto
import com.barisal.cityservice.data.repository.ShoppingRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostProductScreen(
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val shoppingRepo = remember { ShoppingRepository() }

    SetStatusBarColor()

    var selectedCategory by remember { mutableStateOf("") }
    var selectedCondition by remember { mutableStateOf(if (isBengali) "পুরাতন" else "Used") }
    var productName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var isSubmitting by remember { mutableStateOf(false) }
    
    var selectedLocation by remember { mutableStateOf(LatLng(23.8103, 90.4125)) } // Default to Dhaka
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(selectedLocation, 10f)
    }

    val hasLocationPermission = com.barisal.cityservice.core.utils.rememberLocationPermissionState()

    val multiplePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 5)
    ) { uris ->
        if (uris.isNotEmpty()) {
            selectedImages = (selectedImages + uris).distinct()
        }
    }

    val primaryColor = Color(0xFFE65100) // Orange theme for shopping

    val categories = if (isBengali) {
        listOf(
            "মোবাইল ও ইলেকট্রনিক্স",
            "গাড়ি ও প্রপার্টি",
            "হোম ও লিভিং",
            "সার্ভিসেস",
            "রিপেয়ার ও কনস্ট্রাকশন",
            "কমার্শিয়াল ইকুইপমেন্ট",
            "বিনোদন ও স্পোর্টস",
            "শিশু ও কিডস",
            "খাবার ও কৃষি",
            "পশুপাখি ও পেটস",
            "অন্যান্য"
        )
    } else {
        listOf(
            "Mobiles & Electronics",
            "Vehicles & Property",
            "Home & Living",
            "Services",
            "Repair & Construction",
            "Commercial Equipment & Tools",
            "Leisure & Activities",
            "Babies & Kids",
            "Food, Agriculture & Farming",
            "Animals & Pets",
            "Others"
        )
    }

    val conditionOptions = if (isBengali) {
        listOf("পুরাতন", "নতুন")
    } else {
        listOf("Used", "New")
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "পণ্য পোস্ট করুন" else "Post Product",
                onBackClick = onBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState(), enabled = !cameraPositionState.isMoving)
                .background(Color(0xFFF3F4F6))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            // Image Upload Placeholder
            if (selectedImages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .clickable { 
                            multiplePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.AddPhotoAlternate,
                            contentDescription = "Upload Image",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBengali) "ছবি আপলোড করুন (সর্বোচ্চ ৫টি)" else "Upload Images (Max 5)",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    items(selectedImages) { uri ->
                        val isThumbnail = selectedImages.first() == uri
                        Box(
                            modifier = Modifier
                                .size(if (isThumbnail) 120.dp else 80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isThumbnail) 2.dp else 1.dp,
                                    color = if (isThumbnail) primaryColor else Color.LightGray,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    val newList = selectedImages.toMutableList()
                                    newList.remove(uri)
                                    newList.add(0, uri)
                                    selectedImages = newList
                                }
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isThumbnail) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .background(primaryColor, RoundedCornerShape(topStart = 12.dp, bottomEnd = 12.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("Thumbnail", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    
                    if (selectedImages.size < 5) {
                        item {
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .background(Color.White, RoundedCornerShape(12.dp))
                                    .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                                    .clickable { 
                                        multiplePhotoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = "Add More", tint = Color.Gray)
                            }
                        }
                    }
                }
            }

            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(if (isBengali) "ক্যাটাগরি *" else "Category *") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        focusedLabelColor = primaryColor,
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category) },
                            onClick = {
                                selectedCategory = category
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            // Product Condition Selector (New vs Used)
            Column {
                Text(
                    text = if (isBengali) "পণ্যের অবস্থা *" else "Product Condition *",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    conditionOptions.forEach { condition ->
                        val isSelected = selectedCondition.equals(condition, ignoreCase = true)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCondition = condition },
                            label = {
                                Text(
                                    text = condition,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = primaryColor,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Product Name
            OutlinedTextField(
                value = productName,
                onValueChange = { productName = it },
                label = { Text(if (isBengali) "পণ্যের নাম *" else "Product Name *") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text(if (isBengali) "দাম (টাকা) *" else "Price (BDT) *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (isBengali) "বিবরণ" else "Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Address
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(if (isBengali) "ঠিকানা / এলাকা" else "Address / Location") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Location Map
            Text(
                text = if (isBengali) "ম্যাপে অবস্থান নির্বাচন করুন" else "Select Location on Map",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = Color.DarkGray
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                    uiSettings = MapUiSettings(myLocationButtonEnabled = true),
                    onMapClick = { latLng ->
                        selectedLocation = latLng
                    }
                ) {
                    Marker(
                        state = MarkerState(position = selectedLocation),
                        title = if (isBengali) "পণ্যের অবস্থান" else "Product Location"
                    )
                }
            }

            // Contact Info
            OutlinedTextField(
                value = contactInfo,
                onValueChange = { contactInfo = it },
                label = { Text(if (isBengali) "যোগাযোগের নম্বর *" else "Contact Number *") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    focusedLabelColor = primaryColor,
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Submit Button
            Button(
                onClick = {
                    if (productName.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে পণ্যের নাম দিন" else "Please enter product name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (selectedCategory.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে ক্যাটাগরি নির্বাচন করুন" else "Please select category", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (price.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে দাম দিন" else "Please enter price", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (contactInfo.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে যোগাযোগের নম্বর দিন" else "Please enter contact number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSubmitting = true
                    coroutineScope.launch {
                        val base64Images = shoppingRepo.compressMultipleImages(context, selectedImages)
                        val product = ProductDto(
                            productName = productName.trim(),
                            category = selectedCategory,
                            condition = selectedCondition,
                            price = price.trim(),
                            description = description.trim(),
                            address = address.trim(),
                            contactInfo = contactInfo.trim(),
                            latLng = "${selectedLocation.latitude},${selectedLocation.longitude}",
                            imageUrls = base64Images
                        )

                        val result = shoppingRepo.saveProductToFirestore(product)
                        isSubmitting = false

                        if (result.isSuccess) {
                            Toast.makeText(
                                context,
                                if (isBengali) "পোস্টটি সফলভাবে জমা দেওয়া হয়েছে! অনুমোদনের পর প্রদর্শিত হবে।" else "Post submitted successfully! It will appear after admin approval.",
                                Toast.LENGTH_LONG
                            ).show()
                            onBack()
                        } else {
                            Toast.makeText(
                                context,
                                if (isBengali) "পোস্ট জমা দিতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.localizedMessage}" else "Failed to submit post: ${result.exceptionOrNull()?.localizedMessage}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = if (isBengali) "পোস্ট করুন" else "Post Product",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

