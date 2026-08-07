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
import com.barisal.cityservice.data.model.BloodRequestDto
import com.barisal.cityservice.data.repository.BloodRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostBloodRequestScreen(
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

    var patientName by remember { mutableStateOf("") }
    var bagsNeeded by remember { mutableStateOf("1") }
    var hospitalName by remember { mutableStateOf("") }
    var requiredDate by remember { mutableStateOf("") }
    var contactInfo by remember { mutableStateOf("") }
    var thana by remember { mutableStateOf("") }
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
                requiredDate = String.format(java.util.Locale.US, "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            },
            year,
            month,
            day
        )
    }

    SetStatusBarColor()

    val redColor = Color(0xFFDC2626) // Red accent for emergency blood request

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "জরুরি রক্তের পোস্ট" else "Post Urgent Blood Need",
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
                    text = if (isBengali) "জরুরি রক্তের প্রয়োজনে পোস্ট করুন" else "Post Urgent Blood Requirement",
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
                        label = { Text(if (isBengali) "প্রয়োজনীয় রক্তের গ্রুপ *" else "Required Blood Group *") },
                        leadingIcon = { Icon(Icons.Default.WaterDrop, contentDescription = null, tint = redColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = redColor,
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
                                text = { Text(group, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = redColor) },
                                onClick = {
                                    selectedBloodGroup = group
                                    bloodGroupDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            // Patient Name
            item {
                OutlinedTextField(
                    value = patientName,
                    onValueChange = { patientName = it },
                    label = { Text(if (isBengali) "রোগীর নাম *" else "Patient Name *") },
                    placeholder = { Text(if (isBengali) "যেমন: মো: পারভেজ হোসেন" else "e.g. Md. Parvez Hossain") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = redColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Amount / Bags Needed
            item {
                OutlinedTextField(
                    value = bagsNeeded,
                    onValueChange = { bagsNeeded = it },
                    label = { Text(if (isBengali) "রক্তের পরিমাণ (ব্যাগের সংখ্যা) *" else "Bags Needed *") },
                    placeholder = { Text(if (isBengali) "যেমন: ১ ব্যাগ / 2 Bags" else "e.g. 1 Bag / 2 Bags") },
                    leadingIcon = { Icon(Icons.Default.LocalHospital, contentDescription = null, tint = redColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Hospital / Location
            item {
                OutlinedTextField(
                    value = hospitalName,
                    onValueChange = { hospitalName = it },
                    label = { Text(if (isBengali) "হাসপাতাল / রক্তদানের স্থান *" else "Hospital / Location *") },
                    placeholder = { Text(if (isBengali) "যেমন: শের-ই-বাংলা মেডিকেল কলেজ হাসপাতাল" else "e.g. Sher-e-Bangla Medical College Hospital") },
                    leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = redColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Required Date / Time
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    OutlinedTextField(
                        value = requiredDate,
                        onValueChange = { requiredDate = it },
                        label = { Text(if (isBengali) "কখন রক্ত প্রয়োজন (তারিখ ও সময়) *" else "Date & Time Needed *") },
                        placeholder = { Text(if (isBengali) "যেমন: আজ বিকাল ৪টা / জরুরি এখনই" else "e.g. Today 4 PM / Immediate") },
                        leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null, tint = redColor) },
                        trailingIcon = {
                            IconButton(onClick = { datePickerDialog.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick Date", tint = redColor)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePickerDialog.show() },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = redColor,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                requiredDate = if (isBengali) "জরুরি এখনই" else "Urgent Immediate"
                            }
                        ) {
                            Text(
                                text = if (isBengali) "জরুরি এখনই" else "Urgent Immediate",
                                color = redColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Emergency Contact Phone
            item {
                OutlinedTextField(
                    value = contactInfo,
                    onValueChange = { contactInfo = it },
                    label = { Text(if (isBengali) "জরুরি যোগাযোগের ফোন নম্বর *" else "Emergency Contact Number *") },
                    placeholder = { Text("01711XXXXXX") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = redColor) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
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
                        leadingIcon = { Icon(Icons.Default.Map, contentDescription = null, tint = redColor) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = zillaDropdownExpanded) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = redColor,
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
                    placeholder = { Text(if (isBengali) "যেমন: ফরিদপুর সদর / বরিশাল সদর" else "e.g. Barisal Sadar") },
                    leadingIcon = { Icon(Icons.Default.HomeWork, contentDescription = null, tint = redColor) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Details & Emergency Reason
            item {
                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text(if (isBengali) "রোগের বিবরণ / জরুরি কারণ" else "Patient Condition / Emergency Details") },
                    placeholder = { Text(if (isBengali) "যেমন: জরুরি ডেলিভারি অপারেশনের জন্য প্রয়োজন" else "e.g. Emergency delivery operation") },
                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null, tint = redColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = redColor,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        if (patientName.isBlank() || contactInfo.isBlank() || hospitalName.isBlank()) {
                            Toast.makeText(
                                context,
                                if (isBengali) "অনুগ্রহ করে রোগীর নাম, হাসপাতাল এবং যোগাযোগের নম্বর দিন" else "Please enter patient name, hospital name and contact number",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        isSubmitting = true
                        coroutineScope.launch {
                            val requestDto = BloodRequestDto(
                                patientName = patientName.trim(),
                                bloodGroup = selectedBloodGroup,
                                bagsNeeded = bagsNeeded.trim(),
                                hospitalName = hospitalName.trim(),
                                zilla = selectedZilla,
                                thana = thana.trim(),
                                requiredDate = requiredDate.trim(),
                                contactInfo = contactInfo.trim(),
                                details = details.trim()
                            )

                            val result = bloodRepository.saveRequestToFirestore(requestDto)
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
                                    if (isBengali) "পোস্ট করতে ব্যর্থ হয়েছে: ${result.exceptionOrNull()?.message}" else "Post failed: ${result.exceptionOrNull()?.message}",
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
                    colors = ButtonDefaults.buttonColors(containerColor = redColor)
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = if (isBengali) "জরুরি পোস্ট জমা দিন" else "Submit Urgent Post",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
