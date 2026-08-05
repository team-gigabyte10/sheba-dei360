package com.barisal.cityservice.feature.blood

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.data.model.BloodDonorDto
import com.barisal.cityservice.data.repository.BloodRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBloodDonorScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val coroutineScope = rememberCoroutineScope()
    val bloodRepository = remember { BloodRepository() }

    val bloodGroups = remember { listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-") }

    var selectedBloodGroup by remember { mutableStateOf(bloodGroups.first()) }
    var bloodGroupDropdownExpanded by remember { mutableStateOf(false) }

    var donorName by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var lastDonation by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }

    var selectedZilla by remember { mutableStateOf(bangladeshZillas.firstOrNull { it == "বরিশাল" || it == "Barishal" } ?: bangladeshZillas.first()) }
    var zillaDropdownExpanded by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }

    val calendar = remember { java.util.Calendar.getInstance() }
    val year = calendar.get(java.util.Calendar.YEAR)
    val month = calendar.get(java.util.Calendar.MONTH)
    val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember(context) {
        android.app.DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                lastDonation = String.format(java.util.Locale.US, "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            },
            year,
            month,
            day
        )
    }

    SetStatusBarColor()

    val primaryColor = Color(0xFF0F766E) // Teal color matching blood feature

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "রক্তদাতা হিসেবে নিবন্ধন" else "Register as Blood Donor",
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
                    text = if (isBengali) "রক্তদাতা প্রোফাইল তৈরি করুন" else "Create Blood Donor Profile",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            // Blood Group Selector Dropdown
            item {
                ExposedDropdownMenuBox(
                    expanded = bloodGroupDropdownExpanded,
                    onExpandedChange = { bloodGroupDropdownExpanded = !bloodGroupDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedBloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isBengali) "রক্তের গ্রুপ *" else "Blood Group *") },
                        leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFFDC2626)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    ExposedDropdownMenu(
                        expanded = bloodGroupDropdownExpanded,
                        onDismissRequest = { bloodGroupDropdownExpanded = false }
                    ) {
                        bloodGroups.forEach { group ->
                            DropdownMenuItem(
                                text = { Text(group, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                                onClick = {
                                    selectedBloodGroup = group
                                    bloodGroupDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Donor Name
            item {
                OutlinedTextField(
                    value = donorName,
                    onValueChange = { donorName = it },
                    label = { Text(if (isBengali) "আপনার নাম *" else "Your Name *") },
                    placeholder = { Text(if (isBengali) "যেমন: পারভেজ মীর" else "e.g. Parvez Mir") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Contact Number
            item {
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text(if (isBengali) "ফোন নম্বর *" else "Contact Number *") },
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

            // Last Donation Date Picker
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = lastDonation,
                        onValueChange = { lastDonation = it },
                        label = { Text(if (isBengali) "সর্বশেষ রক্তদানের তারিখ *" else "Last Donation Date *") },
                        placeholder = { Text(if (isBengali) "তারিখ বাছুন (যেমন: ১২/১১/২০২৪)" else "Select date (e.g. 12/11/2024)") },
                        leadingIcon = { Icon(Icons.Default.Event, contentDescription = null, tint = primaryColor) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = primaryColor)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                lastDonation = if (isBengali) "নতুন রক্তদাতা" else "New Donor"
                            }
                        ) {
                            Text(
                                text = if (isBengali) "নতুন রক্তদাতা (কখনও দিইনি)" else "New Donor (Never Donated)",
                                color = primaryColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
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

            // Thana / Upazila
            item {
                OutlinedTextField(
                    value = thana,
                    onValueChange = { thana = it },
                    label = { Text(if (isBengali) "থানা / উপজেলা" else "Thana / Upazila") },
                    placeholder = { Text(if (isBengali) "যেমন: বরিশাল সদর / সালথা" else "e.g. Barisal Sadar") },
                    leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = primaryColor) },
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
                    label = { Text(if (isBengali) "ঠিকানা" else "Address") },
                    placeholder = { Text(if (isBengali) "যেমন: ধুলদী রেল গেইট, ফরিদপুর" else "e.g. Dhuldi Rail Gate") },
                    leadingIcon = { Icon(Icons.Default.Home, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Additional Notes / Details
            item {
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(if (isBengali) "অন্যান্য তথ্য / নোট" else "Additional Notes / Details") },
                    placeholder = { Text(if (isBengali) "যেমন: শুধুমাত্র জরুরি প্রয়োজনে কল করবেন" else "e.g. Only Emergency Call") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = primaryColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (donorName.isBlank() || contactInfo.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "অনুগ্রহ করে আপনার নাম এবং ফোন নম্বর দিন" else "Please enter your name and phone number",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            val donorDto = BloodDonorDto(
                                name = donorName.trim(),
                                bloodGroup = selectedBloodGroup,
                                lastDonation = lastDonation.trim().ifEmpty { if (isBengali) "নতুন রক্তদাতা" else "New Donor" },
                                zilla = selectedZilla,
                                thana = thana.trim(),
                                address = address.trim(),
                                contactInfo = contactInfo.trim(),
                                details = details.trim()
                            )

                            val result = bloodRepository.saveDonorToFirestore(donorDto)
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
                                    if (isBengali) "নিবন্ধন করতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}" else "Registration failed: ${result.exceptionOrNull()?.message}",
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
                            text = if (isBengali) "নিবন্ধন জমা দিন" else "Submit Registration",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
