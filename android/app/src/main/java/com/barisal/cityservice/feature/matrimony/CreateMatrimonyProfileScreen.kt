package com.barisal.cityservice.feature.matrimony

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMatrimonyProfileScreen(
    onBack: () -> Unit,
    onProfileCreated: (MatrimonyProfile) -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    val matrimonyPrimary = Color(0xFFBE123C)
    val matrimonyLightBg = Color(0xFFFFF1F2)
    val matrimonyBorder = Color(0xFFFECDD3)

    // Section 1: Basic Info & Photo
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var gender by remember { mutableStateOf("Male") }
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var complexion by remember { mutableStateOf("") }
    var bloodGroup by remember { mutableStateOf("") }
    var maritalStatus by remember { mutableStateOf("") }
    var religion by remember { mutableStateOf("ইসলাম") }
    var sect by remember { mutableStateOf("সুন্নি") }

    // Section 2: Education & Profession
    var education by remember { mutableStateOf("") }
    var institute by remember { mutableStateOf("") }
    var profession by remember { mutableStateOf("") }
    var monthlyIncome by remember { mutableStateOf("") }

    // Section 3: Family & Address
    var location by remember { mutableStateOf("") }
    var permanentAddress by remember { mutableStateOf("") }
    var fatherOccupation by remember { mutableStateOf("") }
    var motherOccupation by remember { mutableStateOf("") }
    var siblings by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // Section 4: Partner Expectations
    var partnerExpectationAge by remember { mutableStateOf("") }
    var partnerExpectationHeight by remember { mutableStateOf("") }
    var partnerExpectationEducation by remember { mutableStateOf("") }
    var partnerExpectationProfession by remember { mutableStateOf("") }
    var partnerExpectationLocation by remember { mutableStateOf("") }

    // Section 5: About Myself
    var about by remember { mutableStateOf("") }

    var showSuccessDialog by remember { mutableStateOf(false) }
    var createdProfileTemp by remember { mutableStateOf<MatrimonyProfile?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "নতুন বায়োডাটা তৈরি করুন" else "Create Biodata",
                onBackClick = onBack
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 12.dp,
                tonalElevation = 6.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            if (name.isBlank() || profession.isBlank() || location.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে নাম, পেশা ও বর্তমান ঠিকানা পূরণ করুন" else "Please fill Name, Profession, and Location", Toast.LENGTH_SHORT).show()
                            } else {
                                val parsedAge = age.toIntOrNull() ?: 26
                                val newProfile = MatrimonyProfile(
                                    id = System.currentTimeMillis().toString(),
                                    name = name,
                                    gender = gender,
                                    age = parsedAge,
                                    height = if (height.isBlank()) "৫'৭\"" else height,
                                    weight = if (weight.isBlank()) "৬৫ কেজি" else weight,
                                    complexion = if (complexion.isBlank()) "উজ্জ্বল ফর্সা" else complexion,
                                    bloodGroup = if (bloodGroup.isBlank()) "B+" else bloodGroup,
                                    maritalStatus = if (maritalStatus.isBlank()) "অবিবাহিত" else maritalStatus,
                                    religion = religion,
                                    sect = sect,
                                    profession = profession,
                                    monthlyIncome = if (monthlyIncome.isBlank()) "৫০,০০০+ টাকা" else monthlyIncome,
                                    education = if (education.isBlank()) "স্নাতক" else education,
                                    institute = if (institute.isBlank()) "ঢাকা বিশ্ববিদ্যালয়" else institute,
                                    location = location,
                                    permanentAddress = if (permanentAddress.isBlank()) location else permanentAddress,
                                    fatherOccupation = if (fatherOccupation.isBlank()) "সরকারি কর্মকর্তা" else fatherOccupation,
                                    motherOccupation = if (motherOccupation.isBlank()) "গৃহিনী" else motherOccupation,
                                    siblings = if (siblings.isBlank()) "১ ভাই, ১ বোন" else siblings,
                                    partnerExpectationAge = if (partnerExpectationAge.isBlank()) "২০-২৫ বছর" else partnerExpectationAge,
                                    partnerExpectationHeight = if (partnerExpectationHeight.isBlank()) "৫'২\"-৫'৬\"" else partnerExpectationHeight,
                                    partnerExpectationEducation = if (partnerExpectationEducation.isBlank()) "স্নাতক / স্নাতকোত্তর" else partnerExpectationEducation,
                                    partnerExpectationProfession = if (partnerExpectationProfession.isBlank()) "সম্মানজনক পেশা" else partnerExpectationProfession,
                                    partnerExpectationLocation = if (partnerExpectationLocation.isBlank()) "ঢাকা / নিজ জেলা" else partnerExpectationLocation,
                                    matchPercentage = 95,
                                    about = if (about.isBlank()) "ধার্মিক, সৎ ও পরিবার সচেতন ব্যক্তিত্ব।" else about,
                                    imageUri = selectedImageUri?.toString()
                                )
                                createdProfileTemp = newProfile
                                showSuccessDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = matrimonyPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isBengali) "বায়োডাটা পোস্ট করুন" else "Publish Biodata",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }
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
            // Photo Upload Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isBengali) "প্রার্থীর ছবি আপলোড করুন" else "Upload Candidate Photo",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .size(130.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(matrimonyLightBg)
                            .border(1.dp, matrimonyBorder, RoundedCornerShape(16.dp))
                            .clickable {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                AsyncImage(
                                    model = selectedImageUri,
                                    contentDescription = "Candidate Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { selectedImageUri = null },
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        .size(28.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        } else {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.AddPhotoAlternate,
                                    contentDescription = "Upload",
                                    tint = matrimonyPrimary,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isBengali) "ছবি সিলেক্ট করতে ট্যাপ করুন" else "Tap to select photo",
                                    fontSize = 11.sp,
                                    color = matrimonyPrimary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Personal & Physical Details
            FormSectionCard(
                title = if (isBengali) "ব্যক্তিগত ও শারীরিক তথ্য" else "Personal & Physical Details",
                icon = Icons.Default.Person,
                themeColor = matrimonyPrimary
            ) {
                Text(if (isBengali) "বায়োডাটার ধরন:" else "Biodata Type:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender = "Male" }
                    ) {
                        RadioButton(selected = gender == "Male", onClick = { gender = "Male" })
                        Text(if (isBengali) "পাত্র (Groom)" else "Groom")
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { gender = "Female" }
                    ) {
                        RadioButton(selected = gender == "Female", onClick = { gender = "Female" })
                        Text(if (isBengali) "পাত্রী (Bride)" else "Bride")
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBengali) "পূর্ণ নাম *" else "Full Name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = age,
                        onValueChange = { age = it },
                        label = { Text(if (isBengali) "বয়স *" else "Age *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = height,
                        onValueChange = { height = it },
                        label = { Text(if (isBengali) "উচ্চতা (৫'৭\")" else "Height") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = weight,
                        onValueChange = { weight = it },
                        label = { Text(if (isBengali) "ওজন (৬৫ কেজি)" else "Weight") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = complexion,
                        onValueChange = { complexion = it },
                        label = { Text(if (isBengali) "গাত্রবর্ণ" else "Complexion") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = { bloodGroup = it },
                        label = { Text(if (isBengali) "রক্তের গ্রুপ (B+)" else "Blood Group") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = maritalStatus,
                        onValueChange = { maritalStatus = it },
                        label = { Text(if (isBengali) "বৈবাহিক অবস্থা" else "Marital Status") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = religion,
                        onValueChange = { religion = it },
                        label = { Text(if (isBengali) "ধর্ম" else "Religion") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = sect,
                        onValueChange = { sect = it },
                        label = { Text(if (isBengali) "উপদল / মাযহাব" else "Sect") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Section 2: Education & Profession
            FormSectionCard(
                title = if (isBengali) "শিক্ষাগত ও পেশাগত তথ্য" else "Education & Profession",
                icon = Icons.Default.School,
                themeColor = matrimonyPrimary
            ) {
                OutlinedTextField(
                    value = education,
                    onValueChange = { education = it },
                    label = { Text(if (isBengali) "শিক্ষাগত যোগ্যতা *" else "Education Level *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = institute,
                    onValueChange = { institute = it },
                    label = { Text(if (isBengali) "শিক্ষা প্রতিষ্ঠান" else "Educational Institute") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = profession,
                        onValueChange = { profession = it },
                        label = { Text(if (isBengali) "পেশা *" else "Profession *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = monthlyIncome,
                        onValueChange = { monthlyIncome = it },
                        label = { Text(if (isBengali) "মাসিক আয়" else "Monthly Income") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Section 3: Family & Address
            FormSectionCard(
                title = if (isBengali) "পারিবারিক ও ঠিকানার তথ্য" else "Family & Address Details",
                icon = Icons.Default.HomeWork,
                themeColor = matrimonyPrimary
            ) {
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text(if (isBengali) "বর্তমান ঠিকানা / এলাকা *" else "Present Address / Location *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = permanentAddress,
                    onValueChange = { permanentAddress = it },
                    label = { Text(if (isBengali) "স্থায়ী ঠিকানা" else "Permanent Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = fatherOccupation,
                        onValueChange = { fatherOccupation = it },
                        label = { Text(if (isBengali) "পিতার পেশা" else "Father's Profession") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = motherOccupation,
                        onValueChange = { motherOccupation = it },
                        label = { Text(if (isBengali) "মাতার পেশা" else "Mother's Profession") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = siblings,
                        onValueChange = { siblings = it },
                        label = { Text(if (isBengali) "ভাই-বোন" else "Siblings") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text(if (isBengali) "যোগাযোগের নম্বর *" else "Contact Phone *") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }

            // Section 4: Partner Expectations
            FormSectionCard(
                title = if (isBengali) "জীবনসঙ্গী সম্পর্কিত প্রত্যাশা" else "Partner Expectations",
                icon = Icons.Default.Favorite,
                themeColor = matrimonyPrimary
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = partnerExpectationAge,
                        onValueChange = { partnerExpectationAge = it },
                        label = { Text(if (isBengali) "প্রত্যাশিত বয়স (২০-২৫)" else "Expected Age Range") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = partnerExpectationHeight,
                        onValueChange = { partnerExpectationHeight = it },
                        label = { Text(if (isBengali) "প্রত্যাশিত উচ্চতা" else "Expected Height") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                OutlinedTextField(
                    value = partnerExpectationEducation,
                    onValueChange = { partnerExpectationEducation = it },
                    label = { Text(if (isBengali) "প্রত্যাশিত শিক্ষা" else "Expected Education") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = partnerExpectationProfession,
                    onValueChange = { partnerExpectationProfession = it },
                    label = { Text(if (isBengali) "প্রত্যাশিত পেশা" else "Expected Profession") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = partnerExpectationLocation,
                    onValueChange = { partnerExpectationLocation = it },
                    label = { Text(if (isBengali) "প্রত্যাশিত এলাকা / জেলা" else "Expected Location") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            // Section 5: Personal Statement
            FormSectionCard(
                title = if (isBengali) "নিজের সম্পর্কে ও জীবনদর্শন" else "About Myself",
                icon = Icons.Default.FormatQuote,
                themeColor = matrimonyPrimary
            ) {
                OutlinedTextField(
                    value = about,
                    onValueChange = { about = it },
                    label = { Text(if (isBengali) "নিজের অথবা প্রার্থীর সম্পর্কে সংক্ষেপে বিস্তারিত লিখুন" else "About Candidate") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 6
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showSuccessDialog) {
        CustomDialog(
            onDismissRequest = {
                showSuccessDialog = false
                createdProfileTemp?.let { onProfileCreated(it) }
                onBack()
            },
            title = if (isBengali) "বায়োডাটা জমা হয়েছে!" else "Biodata Submitted Successfully!",
            icon = Icons.Default.Check,
            iconTint = Color(0xFF16A34A),
            iconBackgroundColor = Color(0xFFDCFCE7),
            confirmButtonText = if (isBengali) "ঠিক আছে" else "OK",
            onConfirm = {
                showSuccessDialog = false
                createdProfileTemp?.let { onProfileCreated(it) }
                onBack()
            }
        ) {
            Text(
                text = if (isBengali)
                    "আপনার পাত্র/পাত্রীর বায়োডাটা সফলভাবে পোস্ট হয়েছে। এডমিন অনুমোদনের পর প্রকাশিত হবে।"
                else
                    "Your matrimony profile has been submitted for admin review. It will be published once approved.",
                fontSize = 14.sp,
                color = Color.DarkGray
            )
        }
    }
}

@Composable
fun FormSectionCard(
    title: String,
    icon: ImageVector,
    themeColor: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(themeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = themeColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            content()
        }
    }
}
