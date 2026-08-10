package com.barisal.cityservice.feature.mistri

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
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
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.MistriProviderDto
import com.barisal.cityservice.data.repository.MistriRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostMistriServiceScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val coroutineScope = rememberCoroutineScope()
    val mistriRepo = remember { MistriRepository() }

    SetStatusBarColor(colorString = "#2563EB", isLightIcons = true)

    // Form Basic State
    var providerName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var whatsappNumber by remember { mutableStateOf("") }
    var selectedCategoryName by remember { mutableStateOf("রাজ মিস্ত্রি") }
    var expandedCategoryDropdown by remember { mutableStateOf(false) }

    var experienceYearsText by remember { mutableStateOf("") }
    var minChargeText by remember { mutableStateOf("") }
    var pricingUnit by remember { mutableStateOf("প্রতি সার্ভিস") }

    var selectedZilla by remember { mutableStateOf("বরিশাল") }
    var expandedZillaDropdown by remember { mutableStateOf(false) }
    var detailedAddress by remember { mutableStateOf("") }
    var latLngString by remember { mutableStateOf("22.7010,90.3535") }
    var descriptionText by remember { mutableStateOf("") }

    // Photos State
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Dynamic Category-Specific Input Fields States
    var acFridgeBrands by remember { mutableStateOf("") }
    var acFridgeWarranty by remember { mutableStateOf("৭ দিন") }
    var acFridgeGasRefill by remember { mutableStateOf(true) }
    var acFridgeCompressorRepair by remember { mutableStateOf(true) }

    var constructionTeamSize by remember { mutableStateOf("একক মিস্ত্রি") }
    var constructionWorkScope by remember { mutableStateOf("নতুন কাজ ও মেরামত") }
    var constructionDailyWage by remember { mutableStateOf("") }

    var autoRoadsideAssistance by remember { mutableStateOf("অন-সাইট রোডসাইড ও গ্যারেজ") }
    var autoGarageName by remember { mutableStateOf("") }
    var autoVehicleTypes by remember { mutableStateOf("") }

    var techServiceLocation by remember { mutableStateOf("হোম সার্ভিস ও শপ") }
    var techShopName by remember { mutableStateOf("") }
    var techEmergencyService by remember { mutableStateOf("হ্যাঁ (২৪/৭)") }
    var techDiagnosticFee by remember { mutableStateOf("") }

    var interiorSpecialties by remember { mutableStateOf("") }
    var interiorPortfolioLink by remember { mutableStateOf("") }

    var genericSpecialSkill by remember { mutableStateOf("") }

    val categoriesList = listOf(
        "রাজ মিস্ত্রি", "কাঠ মিস্ত্রি", "রং মিস্ত্রি", "টিউবওয়েল মিস্ত্রি",
        "সেনেটারি মিস্ত্রি", "টাইলস মিস্ত্রি", "গ্রিল মিস্ত্রি", "থাই মিস্ত্রি",
        "গাড়ি সার্ভিসিং", "বাইকের মিস্ত্রি", "WiFi টেকনিশিয়ান", "এসি সার্ভিসিং",
        "ফ্রিজ সার্ভিসিং", "ইন্টেরিয়র মিস্ত্রি", "ইলেকট্রিশিয়ান", "মোবাইল সার্ভিসিং",
        "কম্পিউটার সার্ভিসিং", "CCTV সার্ভিসিং", "টিভি সার্ভিসিং", "অন্যান্য মিস্ত্রি"
    )

    // Image Launchers
    val profileImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    val primaryColor = Color(0xFF2563EB)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "মিস্ত্রি সার্ভিস পোস্ট করুন" else "Post Mistri Service",
                onBackClick = onBack,
                containerColor = primaryColor,
                contentColor = Color.White
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
            // Card 1: Basic Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "প্রোফাইল ও মৌলিক তথ্য" else "Basic Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    // Category Picker Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedCategoryDropdown,
                        onExpandedChange = { expandedCategoryDropdown = !expandedCategoryDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedCategoryName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "সার্ভিস ক্যাটাগরি নির্বাচন করুন *" else "Select Category *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategoryDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedCategoryDropdown,
                            onDismissRequest = { expandedCategoryDropdown = false }
                        ) {
                            categoriesList.forEach { category ->
                                DropdownMenuItem(
                                    text = { Text(category) },
                                    onClick = {
                                        selectedCategoryName = category
                                        expandedCategoryDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    // Provider Name
                    OutlinedTextField(
                        value = providerName,
                        onValueChange = { providerName = it },
                        label = { Text(if (isBengali) "আপনার নাম / প্রতিষ্ঠানের নাম *" else "Name / Business Name *") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Contact Phones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text(if (isBengali) "মোবাইল নম্বর *" else "Phone Number *") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = whatsappNumber,
                            onValueChange = { whatsappNumber = it },
                            label = { Text(if (isBengali) "হোয়াটসঅ্যাপ" else "WhatsApp") },
                            leadingIcon = { Icon(Icons.Default.Send, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Experience & Pricing
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = experienceYearsText,
                            onValueChange = { experienceYearsText = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isBengali) "অভিজ্ঞতা (বছর)" else "Experience (Yrs)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minChargeText,
                            onValueChange = { minChargeText = it.filter { ch -> ch.isDigit() } },
                            label = { Text(if (isBengali) "ভিজিটিং চার্জ (টাকা)" else "Visiting Fee (BDT)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Pricing Unit
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "মূল্য হিসাব:" else "Pricing Unit:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.DarkGray
                        )
                        val units = listOf("প্রতি সার্ভিস", "প্রতি ঘণ্টা", "প্রতি দিন")
                        units.forEach { unit ->
                            FilterChip(
                                selected = pricingUnit == unit,
                                onClick = { pricingUnit = unit },
                                label = { Text(unit, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Card 2: Individual Input Fields for Selected Category
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = primaryColor)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "$selectedCategoryName - ক্যাটাগরি অনুযায়ী অতিরিক্ত তথ্য" else "Category Specific Details",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    when (selectedCategoryName) {
                        "এসি সার্ভিসিং", "ফ্রিজ সার্ভিসিং" -> {
                            OutlinedTextField(
                                value = acFridgeBrands,
                                onValueChange = { acFridgeBrands = it },
                                label = { Text(if (isBengali) "যে যে ব্র্যান্ডের সার্ভিস দেন (যেমন: Gree, LG, Singer, Walton)" else "Supported Brands") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "সার্ভিস ওয়ারেন্টি:" else "Warranty:", fontSize = 13.sp)
                                val warrantyOptions = listOf("কোনো ওয়ারেন্টি নেই", "৭ দিন", "১৫ দিন", "৩০ দিন")
                                var expandedW by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedW,
                                    onExpandedChange = { expandedW = !expandedW },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = acFridgeWarranty,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedW) },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(expanded = expandedW, onDismissRequest = { expandedW = false }) {
                                        warrantyOptions.forEach { opt ->
                                            DropdownMenuItem(text = { Text(opt) }, onClick = { acFridgeWarranty = opt; expandedW = false })
                                        }
                                    }
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = acFridgeGasRefill, onCheckedChange = { acFridgeGasRefill = it })
                                Text(if (isBengali) "গ্যাস রিফিল ও লিক মেরামত সুবিধা আছে" else "Gas Refill Available", fontSize = 13.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = acFridgeCompressorRepair, onCheckedChange = { acFridgeCompressorRepair = it })
                                Text(if (isBengali) "কম্প্রেসর ও সার্কিট বোর্ড সার্ভিসিং" else "Compressor & PCB Repair", fontSize = 13.sp)
                            }
                        }

                        "রাজ মিস্ত্রি", "কাঠ মিস্ত্রি", "রং মিস্ত্রি", "টিউবওয়েল মিস্ত্রি",
                        "সেনেটারি মিস্ত্রি", "টাইলস মিস্ত্রি", "গ্রিল মিস্ত্রি", "থাই মিস্ত্রি" -> {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "টিমের ধরন:" else "Team Size:", fontSize = 13.sp, modifier = Modifier.width(90.dp))
                                val teamOptions = listOf("একক মিস্ত্রি", "ছোট টিম (২-৪ জন)", "কন্ট্রাক্টর টিম")
                                var expandedT by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedT,
                                    onExpandedChange = { expandedT = !expandedT },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = constructionTeamSize,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedT) },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(expanded = expandedT, onDismissRequest = { expandedT = false }) {
                                        teamOptions.forEach { opt ->
                                            DropdownMenuItem(text = { Text(opt) }, onClick = { constructionTeamSize = opt; expandedT = false })
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = constructionWorkScope,
                                onValueChange = { constructionWorkScope = it },
                                label = { Text(if (isBengali) "কাজের ধরন (যেমন: নতুন ফিটিং, ডেমোলিশন, মেরামত)" else "Work Scope") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = constructionDailyWage,
                                onValueChange = { constructionDailyWage = it.filter { c -> c.isDigit() } },
                                label = { Text(if (isBengali) "দৈনিক আনুমানিক মজুরি (টাকা - ঐচ্ছিক)" else "Estimated Daily Wage (BDT)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        "গাড়ি সার্ভিসিং", "বাইকের মিস্ত্রি" -> {
                            OutlinedTextField(
                                value = autoGarageName,
                                onValueChange = { autoGarageName = it },
                                label = { Text(if (isBengali) "গ্যারেজ / ওয়ার্কশপের নাম (যদি থাকে)" else "Garage / Workshop Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = autoVehicleTypes,
                                onValueChange = { autoVehicleTypes = it },
                                label = { Text(if (isBengali) "যে সব গাড়ির কাজ করেন (যেমন: প্রাইভেট কার, বাইক, মাইক্রো)" else "Vehicle Types Serviced") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "সার্ভিস সুযোগ:" else "Scope:", fontSize = 13.sp, modifier = Modifier.width(90.dp))
                                val scopeOptions = listOf("অন-সাইট রোডসাইড ও গ্যারেজ", "শুধুমাত্র গ্যারেজ", "হোম কল সার্ভিস")
                                var expandedS by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedS,
                                    onExpandedChange = { expandedS = !expandedS },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = autoRoadsideAssistance,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedS) },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(expanded = expandedS, onDismissRequest = { expandedS = false }) {
                                        scopeOptions.forEach { opt ->
                                            DropdownMenuItem(text = { Text(opt) }, onClick = { autoRoadsideAssistance = opt; expandedS = false })
                                        }
                                    }
                                }
                            }
                        }

                        "WiFi টেকনিশিয়ান", "মোবাইল সার্ভিসিং", "কম্পিউটার সার্ভিসিং", "CCTV সার্ভিসিং", "টিভি সার্ভিসিং" -> {
                            OutlinedTextField(
                                value = techShopName,
                                onValueChange = { techShopName = it },
                                label = { Text(if (isBengali) "দোকান / সার্ভিস সেন্টারের নাম" else "Shop / Service Center Name") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = techDiagnosticFee,
                                onValueChange = { techDiagnosticFee = it.filter { c -> c.isDigit() } },
                                label = { Text(if (isBengali) "ডায়াগনস্টিক / টেস্ট ফি (টাকা)" else "Diagnostic Fee (BDT)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(if (isBengali) "সার্ভিসের স্থান:" else "Location:", fontSize = 13.sp, modifier = Modifier.width(90.dp))
                                val locOptions = listOf("হোম সার্ভিস ও শপ", "শুধুমাত্র হোম সার্ভিস", "শুধুমাত্র শপ/ল্যাব")
                                var expandedL by remember { mutableStateOf(false) }
                                ExposedDropdownMenuBox(
                                    expanded = expandedL,
                                    onExpandedChange = { expandedL = !expandedL },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    OutlinedTextField(
                                        value = techServiceLocation,
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedL) },
                                        modifier = Modifier.menuAnchor()
                                    )
                                    ExposedDropdownMenu(expanded = expandedL, onDismissRequest = { expandedL = false }) {
                                        locOptions.forEach { opt ->
                                            DropdownMenuItem(text = { Text(opt) }, onClick = { techServiceLocation = opt; expandedL = false })
                                        }
                                    }
                                }
                            }
                        }

                        "ইন্টেরিয়র মিস্ত্রি" -> {
                            OutlinedTextField(
                                value = interiorSpecialties,
                                onValueChange = { interiorSpecialties = it },
                                label = { Text(if (isBengali) "বিশেষত্ব (যেমন: ফলস সিলিং, কিচেন ক্যাবিনেট, থ্রিডি ডেকোর)" else "Specialties") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = interiorPortfolioLink,
                                onValueChange = { interiorPortfolioLink = it },
                                label = { Text(if (isBengali) "পোর্টফোলিও / ফেসবুক পেজ লিংক (ঐচ্ছিক)" else "Portfolio / Page Link") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        else -> {
                            OutlinedTextField(
                                value = genericSpecialSkill,
                                onValueChange = { genericSpecialSkill = it },
                                label = { Text(if (isBengali) "বিশেষ কাজের বিবরণ ও দক্ষতা" else "Special Skills & Details") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Card 3: Location & Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "অবস্থান ও এলাকা" else "Location & Address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    // Zilla Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = expandedZillaDropdown,
                        onExpandedChange = { expandedZillaDropdown = !expandedZillaDropdown }
                    ) {
                        OutlinedTextField(
                            value = selectedZilla,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isBengali) "জেলা নির্বাচন করুন *" else "Select Zilla *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedZillaDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expandedZillaDropdown,
                            onDismissRequest = { expandedZillaDropdown = false }
                        ) {
                            bangladeshZillas.forEach { zilla ->
                                DropdownMenuItem(
                                    text = { Text(zilla) },
                                    onClick = {
                                        selectedZilla = zilla
                                        expandedZillaDropdown = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = detailedAddress,
                        onValueChange = { detailedAddress = it },
                        label = { Text(if (isBengali) "বিস্তারিত ঠিকানা (যেমন: সড়ক, ওয়ার্ড, এলাকা) *" else "Detailed Address *") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text(if (isBengali) "আপনার কাজ ও সেবার বিস্তারিত বিবরণ" else "Service Description") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            }

            // Card 4: Photo Attachments
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = if (isBengali) "ছবি সংযুক্ত করুন" else "Attach Profile Photo",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    // Profile Photo Selection
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { profileImageLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (profileImageUri != null) {
                                AsyncImage(
                                    model = profileImageUri,
                                    contentDescription = "Profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(Icons.Default.AddAPhoto, contentDescription = "Add Profile Image", tint = Color.Gray)
                            }
                        }
                        Column {
                            Text(if (isBengali) "আপনার ছবি / লোগো সংযুক্ত করুন" else "Upload Profile Photo", fontWeight = FontWeight.Medium, fontSize = 13.sp)
                            TextButton(onClick = { profileImageLauncher.launch("image/*") }, contentPadding = PaddingValues(0.dp)) {
                                Text(if (isBengali) "ছবি পরিবর্তন" else "Choose Photo", color = primaryColor, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (providerName.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে আপনার নাম লিখুন" else "Please enter provider name", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (phoneNumber.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে ফোন নম্বর লিখুন" else "Please enter phone number", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (detailedAddress.isBlank()) {
                        Toast.makeText(context, if (isBengali) "অনুগ্রহ করে ঠিকানা লিখুন" else "Please enter detailed address", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isSubmitting = true
                    coroutineScope.launch {
                        try {
                            var profileBase64 = ""
                            if (profileImageUri != null) {
                                val res = mistriRepo.compressImageToBase64(context, profileImageUri!!)
                                profileBase64 = res.getOrDefault("")
                            }

                            // Build category specific map
                            val categoryMap = mutableMapOf<String, String>()
                            when (selectedCategoryName) {
                                "এসি সার্ভিসিং", "ফ্রিজ সার্ভিসিং" -> {
                                    categoryMap["supportedBrands"] = acFridgeBrands
                                    categoryMap["warranty"] = acFridgeWarranty
                                    categoryMap["gasRefill"] = if (acFridgeGasRefill) "হ্যাঁ" else "না"
                                    categoryMap["compressorRepair"] = if (acFridgeCompressorRepair) "হ্যাঁ" else "না"
                                }
                                "রাজ মিস্ত্রি", "কাঠ মিস্ত্রি", "রং মিস্ত্রি", "টিউবওয়েল মিস্ত্রি",
                                "সেনেটারি মিস্ত্রি", "টাইলস মিস্ত্রি", "গ্রিল মিস্ত্রি", "থাই মিস্ত্রি" -> {
                                    categoryMap["teamSize"] = constructionTeamSize
                                    categoryMap["workScope"] = constructionWorkScope
                                    categoryMap["dailyWage"] = constructionDailyWage
                                }
                                "গাড়ি সার্ভিসিং", "বাইকের মিস্ত্রি" -> {
                                    categoryMap["garageName"] = autoGarageName
                                    categoryMap["vehicleTypes"] = autoVehicleTypes
                                    categoryMap["serviceScope"] = autoRoadsideAssistance
                                }
                                "WiFi টেকনিশিয়ান", "মোবাইল সার্ভিসিং", "কম্পিউটার সার্ভিসিং", "CCTV সার্ভিসিং", "টিভি সার্ভিসিং" -> {
                                    categoryMap["shopName"] = techShopName
                                    categoryMap["diagnosticFee"] = techDiagnosticFee
                                    categoryMap["serviceLocation"] = techServiceLocation
                                    categoryMap["emergency24_7"] = techEmergencyService
                                }
                                "ইন্টেরিয়র মিস্ত্রি" -> {
                                    categoryMap["specialties"] = interiorSpecialties
                                    categoryMap["portfolioLink"] = interiorPortfolioLink
                                }
                                else -> {
                                    categoryMap["specialSkills"] = genericSpecialSkill
                                }
                            }

                            val dto = MistriProviderDto(
                                name = providerName,
                                categoryName = selectedCategoryName,
                                phone = phoneNumber,
                                whatsapp = whatsappNumber,
                                experienceYears = experienceYearsText.toIntOrNull() ?: 0,
                                minCharge = minChargeText.toIntOrNull() ?: 0,
                                pricingUnit = pricingUnit,
                                addressBn = detailedAddress,
                                addressEn = detailedAddress,
                                zilla = selectedZilla,
                                latLng = latLngString,
                                description = descriptionText,
                                profileImageUrl = profileBase64,
                                workSampleImages = emptyList(),
                                categorySpecificFields = categoryMap,
                                isApproved = false
                            )

                            val result = mistriRepo.saveMistriProviderToFirestore(dto)
                            isSubmitting = false
                            if (result.isSuccess) {
                                showSuccessDialog = true
                            } else {
                                Toast.makeText(context, if (isBengali) "পোস্ট সংরক্ষণ ব্যর্থ হয়েছে" else "Failed to save post", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            isSubmitting = false
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isBengali) "সার্ভিস পোস্ট নিশ্চিত করুন" else "Publish Service",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (showSuccessDialog) {
            CustomDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    onBack()
                },
                title = if (isBengali) "পোস্ট সফলভাবে জমা হয়েছে!" else "Submission Successful!",
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
                        "আপনার মিস্ত্রি সার্ভিস প্রোফাইলটি সফলভাবে অ্যাডমিন প্যানেলে জমা হয়েছে। পর্যালোচনার পর সার্ভিসটি ম্যাপে ও তালিকায় প্রকাশিত হবে।"
                    else
                        "Your service profile has been submitted for admin approval. It will appear on the map and listing once verified.",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}
