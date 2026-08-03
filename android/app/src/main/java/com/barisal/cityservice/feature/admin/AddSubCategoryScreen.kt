package com.barisal.cityservice.feature.admin

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import coil.compose.rememberAsyncImagePainter
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.CategoryItem
import com.barisal.cityservice.data.model.SubCategoryDto
import com.barisal.cityservice.data.repository.CITY_SERVICE_DRIVE_FOLDER_ID
import com.barisal.cityservice.data.repository.CategoryRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.core.utils.toCoilModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubCategoryScreen(
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val categoryRepository = remember { CategoryRepository() }

    val parentCategories by categoryRepository.getCategoriesFlow()
        .collectAsState(initial = categoryRepository.getDefaultCategories())

    var selectedParentCategory by remember { mutableStateOf<CategoryItem?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    var subCategoryIdText by remember { mutableStateOf("101") }
    var nameEn by remember { mutableStateOf("") }
    var nameBn by remember { mutableStateOf("") }
    var routeCategoryName by remember { mutableStateOf("") }
    var displayOrderText by remember { mutableStateOf("1") }
    var isActive by remember { mutableStateOf(true) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val activeCategoryId = selectedParentCategory?.id ?: 1
    val subCategories by categoryRepository.getAllSubCategoriesFlow()
        .collectAsState(initial = emptyList())

    val filteredSubCategories = remember(subCategories, selectedParentCategory) {
        if (selectedParentCategory == null) subCategories
        else subCategories.filter { it.categoryId == activeCategoryId }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "সাব-ক্যাটাগরি এন্ট্রি (City_Service)" else "Sub-Category Entry (City_Service)",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "নতুন সাব-ক্যাটাগরি তৈরি করুন" else "Create New Sub-Category",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )

                        Text(
                            text = "Google Drive Target: City_Service (ID: $CITY_SERVICE_DRIVE_FOLDER_ID)",
                            fontSize = 12.sp,
                            color = Color(0xFF0F766E),
                            fontWeight = FontWeight.Medium
                        )

                        // 1. Parent Category Dropdown
                        Text(
                            text = if (isBengali) "মেইন ক্যাটাগরি নির্বাচন করুন" else "Select Parent Category",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        ExposedDropdownMenuBox(
                            expanded = dropdownExpanded,
                            onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedParentCategory?.getDisplayName(isBengali)
                                    ?: (if (isBengali) "-- ক্যাটাগরি বেছে নিন --" else "-- Select Category --"),
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false }
                            ) {
                                parentCategories.forEach { category ->
                                    DropdownMenuItem(
                                        text = { Text(category.getDisplayName(isBengali)) },
                                        onClick = {
                                            selectedParentCategory = category
                                            routeCategoryName = category.nameEn
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // 2. Numeric Sub-Category ID (Int)
                        OutlinedTextField(
                            value = subCategoryIdText,
                            onValueChange = { subCategoryIdText = it },
                            label = { Text(if (isBengali) "সাব-ক্যাটাগরি আইডি (Sub-Category ID: Int)" else "Sub-Category ID (Int)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 3. English Name
                        OutlinedTextField(
                            value = nameEn,
                            onValueChange = { nameEn = it },
                            label = { Text(if (isBengali) "সাব-ক্যাটাগরি নাম (English)" else "Sub-Category Name (English)") },
                            placeholder = { Text("e.g. Doctor Consultation") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 4. Bengali Name
                        OutlinedTextField(
                            value = nameBn,
                            onValueChange = { nameBn = it },
                            label = { Text(if (isBengali) "সাব-ক্যাটাগরি নাম (বাংলা)" else "Sub-Category Name (Bengali)") },
                            placeholder = { Text("e.g. ডাক্তার পরামর্শ") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 5. Route (String: Category Name)
                        OutlinedTextField(
                            value = routeCategoryName,
                            onValueChange = { routeCategoryName = it },
                            label = { Text(if (isBengali) "রুট (Route String: Category Name)" else "Route String (Category Name)") },
                            placeholder = { Text("e.g. Health Services") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 6. Display Order (Int)
                        OutlinedTextField(
                            value = displayOrderText,
                            onValueChange = { displayOrderText = it },
                            label = { Text(if (isBengali) "ডিসপ্লে অর্ডার (Order Position: Int)" else "Display Order Position (Int)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )

                        // 7. Active Status Switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isBengali) "সক্রিয় অবস্থা (Active Status)" else "Active Status",
                                fontWeight = FontWeight.Medium
                            )
                            Switch(
                                checked = isActive,
                                onCheckedChange = { isActive = it }
                            )
                        }

                        HorizontalDivider(color = Color(0xFFE2E8F0))

                        // 8. Image Picker Section
                        Text(
                            text = if (isBengali) "সাব-ক্যাটাগরি আইকন নির্বাচন করুন" else "Select Sub-Category Icon",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFE2E8F0))
                                    .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(12.dp))
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = selectedImageUri),
                                        contentDescription = "Preview",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Pick Image",
                                        tint = Color(0xFF64748B),
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(16.dp))

                            Column {
                                OutlinedButton(
                                    onClick = { imagePickerLauncher.launch("image/*") }
                                ) {
                                    Icon(Icons.Default.CloudUpload, contentDescription = null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(if (isBengali) "গ্যালারি থেকে আইকন নিন" else "Browse Icon File")
                                }

                                if (selectedImageUri != null) {
                                    Text(
                                        text = if (isBengali) "আইকন নির্বাচিত হয়েছে" else "Icon Selected",
                                        fontSize = 12.sp,
                                        color = Color(0xFF10B981),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                if (nameEn.isBlank() && nameBn.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "অনুগ্রহ করে সাব-ক্যাটাগরি নাম লিখুন" else "Please enter sub-category name",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }

                                isUploading = true

                                coroutineScope.launch {
                                    var uploadedUrl: String? = null
                                    if (selectedImageUri != null) {
                                        val uploadRes = categoryRepository.uploadCategoryIconToDrive(context, selectedImageUri!!)
                                        uploadedUrl = uploadRes.getOrNull()
                                    }

                                    val subIdInt = subCategoryIdText.toIntOrNull() ?: kotlin.math.abs(nameEn.hashCode())
                                    val categoryIdInt = selectedParentCategory?.id ?: 1
                                    val orderInt = displayOrderText.toIntOrNull() ?: 1

                                    val subCategoryDto = SubCategoryDto(
                                        id = subIdInt,
                                        categoryId = categoryIdInt,
                                        nameEn = nameEn.ifBlank { nameBn },
                                        nameBn = nameBn.ifBlank { nameEn },
                                        iconUrl = uploadedUrl,
                                        route = routeCategoryName.ifBlank { selectedParentCategory?.nameEn ?: "General" },
                                        order = orderInt,
                                        isActive = isActive
                                    )

                                    val saveResult = categoryRepository.saveSubCategoryToFirestore(subCategoryDto)

                                    isUploading = false

                                    if (saveResult.isSuccess) {
                                        Toast.makeText(
                                            context,
                                            if (isBengali) "সাব-ক্যাটাগরি সফলভাবে ফায়ারস্টোর ও ড্রাইভে সংরক্ষিত হয়েছে!" 
                                            else "Sub-Category saved to Firestore & Drive successfully!",
                                            Toast.LENGTH_LONG
                                        ).show()

                                        // Reset Form
                                        nameEn = ""
                                        nameBn = ""
                                        selectedImageUri = null
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Firestore Error: ${saveResult.exceptionOrNull()?.message}",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isUploading
                        ) {
                            if (isUploading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "সংরক্ষণ করা হচ্ছে..." else "Saving Sub-Category...")
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "সাব-ক্যাটাগরি সংরক্ষণ করুন" else "Save Sub-Category",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Sub-Categories List Section
            item {
                Text(
                    text = if (isBengali) "নিবন্ধিত সাব-ক্যাটাগরি তালিকা (${filteredSubCategories.size})" 
                    else "Registered Sub-Categories (${filteredSubCategories.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            items(filteredSubCategories) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!item.iconUrl.isNullOrBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(model = item.iconUrl.toCoilModel()),
                                        contentDescription = null,
                                        modifier = Modifier.size(28.dp)
                                    )
                                } else {
                                    Icon(
                                        imageVector = item.fallbackIcon ?: Icons.Default.Category,
                                        contentDescription = null,
                                        tint = Color(0xFF0F766E),
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = item.getDisplayName(isBengali),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "ID: ${item.id} | CatID: ${item.categoryId} | Route: ${item.route}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        IconButton(onClick = {
                            coroutineScope.launch {
                                categoryRepository.deleteSubCategoryFromFirestore(item.id)
                                Toast.makeText(
                                    context,
                                    if (isBengali) "সাব-ক্যাটাগরি মুছে ফেলা হয়েছে" else "Sub-Category Removed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFEF4444))
                        }
                    }
                }
            }
        }
    }
}
