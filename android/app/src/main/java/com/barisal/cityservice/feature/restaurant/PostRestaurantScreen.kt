package com.barisal.cityservice.feature.restaurant

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
import com.barisal.cityservice.data.model.MenuItemDto
import com.barisal.cityservice.data.model.RestaurantDto
import com.barisal.cityservice.data.repository.RestaurantRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.LocationPickerMapScreen
import com.barisal.cityservice.ui.components.SetStatusBarColor
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch

data class MenuItemInputState(
    var id: String = java.util.UUID.randomUUID().toString(),
    var itemName: String = "",
    var price: String = "",
    var description: String = "",
    var category: String = "প্রধান খাবার",
    var itemImage: Uri? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostRestaurantScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val repository = remember { RestaurantRepository() }

    val primaryColor = Color(0xFFC2410C) // Orange/Red theme for food

    var name by remember { mutableStateOf("") }
    var selectedCuisine by remember { mutableStateOf(if (isBengali) "বাংলা খাবার & বিরিয়ানি" else "Bengali Food & Biriyani") }
    var cuisineDropdownExpanded by remember { mutableStateOf(false) }
    val cuisineTypeList = listOf("বাংলা খাবার & বিরিয়ানি", "ফাস্ট ফুড & ক্যাফে", "চাইনিজ & থাই", "বেকারি & মিষ্টি", "ইন্ডিয়ান থালি")

    var address by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var contact by remember { mutableStateOf("") }
    var deliveryAvailable by remember { mutableStateOf(true) }

    var selectedCoverImage by remember { mutableStateOf<Uri?>(null) }
    val menuInputs = remember { mutableStateListOf<MenuItemInputState>() }

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
                title = if (isBengali) "রেস্টুরেন্ট তথ্য পোস্ট করুন" else "Post Restaurant Listing",
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
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = primaryColor, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBengali) "রেস্টুরেন্ট ও খাবারের মেনু বিজ্ঞাপন" else "Post Restaurant & Food Menu",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = primaryColor
                            )
                            Text(
                                text = if (isBengali) "আপনার রেস্টুরেন্ট ও স্পেশাল মেনু যুক্ত করুন" else "Add your restaurant details and food menu with photos",
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }

                // Restaurant Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "রেস্টুরেন্টের নাম *" else "Restaurant Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                )

                // Cuisine Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = cuisineDropdownExpanded,
                    onExpandedChange = { cuisineDropdownExpanded = !cuisineDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCuisine,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "খাবারের ধরন *" else "Cuisine Type *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cuisineDropdownExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    ExposedDropdownMenu(
                        expanded = cuisineDropdownExpanded,
                        onDismissRequest = { cuisineDropdownExpanded = false }
                    ) {
                        cuisineTypeList.forEach { cuisine ->
                            DropdownMenuItem(
                                text = { Text(text = cuisine) },
                                onClick = {
                                    selectedCuisine = cuisine
                                    cuisineDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Contact Phone & Home Delivery Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text(if (isBengali) "ফোন নম্বর *" else "Contact Phone *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = primaryColor, focusedLabelColor = primaryColor)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Switch(
                            checked = deliveryAvailable,
                            onCheckedChange = { deliveryAvailable = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.4f))
                        )
                        Text(if (isBengali) "হোম ডেলিভারি" else "Home Delivery", fontSize = 13.sp)
                    }
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
                    Text(if (isBengali) "ম্যাপে রেস্টুরেন্টের অবস্থান নির্বাচন করুন" else "Pick Location on Map", color = primaryColor)
                }

                // Restaurant Cover Photo
                Text(if (isBengali) "রেস্টুরেন্ট কভার ছবি *" else "Restaurant Cover Photo *", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

                // Dynamic Food Menu Section
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isBengali) "খাবারের মেনু তালিকা" else "Food Menu Items", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = primaryColor)
                    Button(
                        onClick = { menuInputs.add(MenuItemInputState()) },
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isBengali) "আইটেম যোগ করুন +" else "Add Item +", fontSize = 13.sp)
                    }
                }

                if (menuInputs.isEmpty()) {
                    Surface(
                        color = Color(0xFFF8FAFC),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isBengali) "কোন মেনু আইটেম যুক্ত করা হয়নি। 'আইটেম যোগ করুন' বাটনে চাপ দিন।" else "No menu items added yet. Tap 'Add Item' button.",
                            fontSize = 13.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }

                menuInputs.forEachIndexed { index, itemState ->
                    val itemPhotoPickerLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.PickVisualMedia()
                    ) { uri: Uri? ->
                        if (uri != null) itemState.itemImage = uri
                    }

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
                                Text(text = "${if (isBengali) "মেনু আইটেম #" else "Item #"}${index + 1}", fontWeight = FontWeight.Bold, color = primaryColor)
                                TextButton(onClick = { menuInputs.removeAt(index) }) {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                                    Text(if (isBengali) "সরান" else "Remove", color = Color.Red, fontSize = 12.sp)
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                                        .clickable { itemPhotoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (itemState.itemImage != null) {
                                        AsyncImage(model = itemState.itemImage, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                                    } else {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = primaryColor)
                                    }
                                }

                                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = itemState.itemName,
                                        onValueChange = { itemState.itemName = it },
                                        label = { Text(if (isBengali) "খাবারের নাম (যেমন: বিরিয়ানি)" else "Item Name") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    OutlinedTextField(
                                        value = itemState.price,
                                        onValueChange = { itemState.price = it },
                                        label = { Text(if (isBengali) "মূল্য (৳)" else "Price (৳)") },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            OutlinedTextField(
                                value = itemState.description,
                                onValueChange = { itemState.description = it },
                                label = { Text(if (isBengali) "বিবরণ (ঐচ্ছিক)" else "Description (Optional)") },
                                modifier = Modifier.fillMaxWidth()
                            )
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
                                val coverBase64 = if (selectedCoverImage != null) {
                                    repository.compressImageToBase64(context, selectedCoverImage!!).getOrDefault("")
                                } else ""

                                val menuDtoList = mutableListOf<MenuItemDto>()
                                for (m in menuInputs) {
                                    val itemImageBase64 = if (m.itemImage != null) {
                                        repository.compressImageToBase64(context, m.itemImage!!).getOrDefault("")
                                    } else ""
                                    menuDtoList.add(
                                        MenuItemDto(
                                            id = m.id,
                                            itemName = m.itemName,
                                            price = m.price,
                                            description = m.description,
                                            category = m.category,
                                            imageUrl = itemImageBase64
                                        )
                                    )
                                }

                                val restaurantDto = RestaurantDto(
                                    name = name,
                                    cuisineType = selectedCuisine,
                                    address = address,
                                    thana = thana,
                                    contact = contact,
                                    deliveryAvailable = deliveryAvailable,
                                    coverImage = coverBase64,
                                    menuItems = menuDtoList,
                                    latitude = selectedLocation.latitude,
                                    longitude = selectedLocation.longitude
                                )

                                val result = repository.saveRestaurantToFirestore(restaurantDto)
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
                        Text(if (isBengali) "রেস্টুরেন্ট তথ্য সাবমিট করুন" else "Submit Restaurant Listing", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    text = if (isBengali) "আপনার পোস্টটি ভেরিফিকেশনের জন্য এডমিনের নিকট জমা দেওয়া হয়েছে। এডমিন ভেরিফাই করার পর পোস্টটি অ্যাপে প্রদর্শিত হবে।" else "Your post has been submitted for admin verification. It will appear after approval.",
                    fontSize = 14.sp
                )
            }
        }
    }
}
