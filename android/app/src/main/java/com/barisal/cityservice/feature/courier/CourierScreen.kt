package com.barisal.cityservice.feature.courier

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class ParcelType(
    val id: String,
    val nameBn: String,
    val nameEn: String,
    val icon: ImageVector,
    val basePrice: Int,
    val description: String
)

data class DeliverySpeed(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val timeBn: String,
    val timeEn: String,
    val extraFee: Int,
    val color: Color
)

data class ParcelTrackingStep(
    val stepNumber: Int,
    val titleBn: String,
    val titleEn: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val time: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

val sampleParcelTypes = listOf(
    ParcelType("doc", "ডকুমেন্ট / কাগজপত্র", "Documents", Icons.Default.Description, 60, "চিঠিপত্র, ফাইল ও অফিসিয়াল কাগজ"),
    ParcelType("small", "স্মল পার্সেল (< ২ কেজী)", "Small (< 2kg)", Icons.Default.Inventory2, 80, "পোশাক, গ্যাজেট ও ছোট গিফট"),
    ParcelType("medium", "মিডিয়াম পার্সেল (২-৫ কেজী)", "Medium (2-5kg)", Icons.Default.LocalMall, 120, "ইলেকট্রনিক্স ও জুতা/বক্স"),
    ParcelType("heavy", "হেভি কার্গো (> ৫ কেজী)", "Heavy Cargo (> 5kg)", Icons.Default.LocalShipping, 250, "ভারী পণ্য, পার্সেল ও ফাস্ট ডেলিভারি")
)

val sampleSpeedTiers = listOf(
    DeliverySpeed("regular", "রেগুলার ডেলিভারি", "Standard", "২৪-৪৮ ঘণ্টা", "24-48 Hours", 0, Color(0xFF2563EB)),
    DeliverySpeed("sameday", "সেম-ডে ডেলিভারি", "Same Day", "৬ ঘণ্টার মধ্যে", "Within 6 Hours", 50, Color(0xFFD97706)),
    DeliverySpeed("express", "ইনস্ট্যান্ট এক্সপ্রেস", "Instant Express", "১-২ ঘণ্টা (রাইডার)", "1-2 Hours", 120, Color(0xFFDC2626))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourierScreen(
    onBack: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    val context = LocalContext.current

    SetStatusBarColor()

    var selectedTab by remember { mutableStateOf(0) } // 0: Send Parcel, 1: Track Parcel

    // Send Parcel Form State
    var selectedParcelType by remember { mutableStateOf(sampleParcelTypes.first()) }
    var selectedSpeed by remember { mutableStateOf(sampleSpeedTiers.first()) }
    
    var senderName by remember { mutableStateOf("") }
    var senderPhone by remember { mutableStateOf("") }
    var senderAddress by remember { mutableStateOf("") }

    var receiverName by remember { mutableStateOf("") }
    var receiverPhone by remember { mutableStateOf("") }
    var receiverAddress by remember { mutableStateOf("") }
    var codAmount by remember { mutableStateOf("") }

    var promoCode by remember { mutableStateOf("") }
    var discountAmount by remember { mutableStateOf(0) }

    var createdWaybillId by remember { mutableStateOf<String?>(null) }
    var showWaybillDialog by remember { mutableStateOf(false) }

    // Track Parcel State
    var searchTrackingCode by remember { mutableStateOf("SB-994812") }
    var searchedResultId by remember { mutableStateOf<String?>(null) }

    val codVal = codAmount.toIntOrNull() ?: 0
    val codFee = (codVal * 0.01).toInt() // 1% COD fee
    val deliveryFee = selectedParcelType.basePrice + selectedSpeed.extraFee
    val grossTotal = deliveryFee + codFee
    val finalTotal = (grossTotal - discountAmount).coerceAtLeast(0)

    val trackingSteps = listOf(
        ParcelTrackingStep(1, "অর্ডার কনফার্মড", "Order Placed", "পার্সেল বুকিং সম্পন্ন হয়েছে", "Parcel booked", "আজ ১০:১৫ AM", true, false),
        ParcelTrackingStep(2, "রাইডার পার্সেল পিক করেছে", "Picked Up", "রাইডার মোঃ সুমন পার্সেল গ্রহণ করেছে", "Rider picked up parcel", "আজ ১১:৩০ AM", true, false),
        ParcelTrackingStep(3, "সর্টিং হাবে প্রক্রিয়াজাত", "At Sorting Hub", "ঢাকা সেন্ট্রাল হাবে প্রসেসিং চলছে", "Processing at sorting hub", "আজ ০২:00 PM", true, true),
        ParcelTrackingStep(4, "ডেলিভারির জন্য রওয়ানা", "Out for Delivery", "গন্তব্যের নিকটস্থ রাইডারের হাতে", "Assigned to last-mile rider", "আসন্ন", false, false),
        ParcelTrackingStep(5, "সফলভাবে ডেলিভারড", "Delivered", "প্রাপকের নিকট পার্সেল হস্তান্তরিত", "Handed over to receiver", "আসন্ন", false, false)
    )

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "কুরিয়ার ও পার্সেল সার্ভিস" else "Courier & Parcel Service",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Switcher (Send Parcel vs Track Parcel)
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF2563EB)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBengali) "পার্সেল পাঠান" else "Send Parcel", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ManageSearch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBengali) "পার্সেল ট্র্যাকিং" else "Track Parcel", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }

            if (selectedTab == 0) {
                // SEND PARCEL FORM
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. Parcel Type Category Selector
                    Text(if (isBengali) "১. পার্সেলের ধরন ও ওজন নির্বাচন করুন:" else "1. Select Parcel Type & Weight:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(sampleParcelTypes) { item ->
                            val isSelected = item.id == selectedParcelType.id
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clickable { selectedParcelType = item },
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFEFF6FF) else Color.White),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF2563EB) else Color(0xFFE2E8F0)
                                )
                            ) {
                                Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(item.icon, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(if (isBengali) item.nameBn else item.nameEn, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 1)
                                    Text(item.description, fontSize = 10.sp, color = Color.Gray, textAlign = TextAlign.Center, maxLines = 2)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("৳ ${item.basePrice}", fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                                }
                            }
                        }
                    }

                    // 2. Delivery Speed Tiers
                    Text(if (isBengali) "২. ডেলিভারির গতি ও সময়সীমা:" else "2. Select Delivery Speed:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        sampleSpeedTiers.forEach { speed ->
                            val isSelected = speed.id == selectedSpeed.id
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedSpeed = speed },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) speed.color.copy(alpha = 0.12f) else Color.White,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) speed.color else Color(0xFFE2E8F0)
                                )
                            ) {
                                Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(if (isBengali) speed.titleBn else speed.titleEn, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = speed.color, maxLines = 1)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(if (isBengali) speed.timeBn else speed.timeEn, fontSize = 10.sp, color = Color.DarkGray)
                                    if (speed.extraFee > 0) {
                                        Text("+৳ ${speed.extraFee}", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = speed.color)
                                    }
                                }
                            }
                        }
                    }

                    // 3. Sender Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF2563EB))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "প্রেরকের তথ্য (Sender Info)" else "Sender Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            OutlinedTextField(
                                value = senderName,
                                onValueChange = { senderName = it },
                                label = { Text(if (isBengali) "প্রেরকের নাম *" else "Sender Name *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = senderPhone,
                                    onValueChange = { senderPhone = it },
                                    label = { Text(if (isBengali) "ফোন নম্বর *" else "Phone *") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = senderAddress,
                                    onValueChange = { senderAddress = it },
                                    label = { Text(if (isBengali) "পিকআপ ঠিকানা *" else "Pickup Address *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // 4. Receiver Details Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFDC2626))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "প্রাপকের তথ্য (Receiver Info)" else "Receiver Information", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                            OutlinedTextField(
                                value = receiverName,
                                onValueChange = { receiverName = it },
                                label = { Text(if (isBengali) "প্রাপকের নাম *" else "Receiver Name *") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = receiverPhone,
                                    onValueChange = { receiverPhone = it },
                                    label = { Text(if (isBengali) "ফোন নম্বর *" else "Phone *") },
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = receiverAddress,
                                    onValueChange = { receiverAddress = it },
                                    label = { Text(if (isBengali) "ডেলিভারি ঠিকানা *" else "Delivery Address *") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            OutlinedTextField(
                                value = codAmount,
                                onValueChange = { codAmount = it },
                                label = { Text(if (isBengali) "ক্যাশ অন ডেলিভারি (COD ৳ টাকা)" else "COD Amount (BDT)") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                        }
                    }

                    // 5. Promo Code & Pricing Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = promoCode,
                            onValueChange = { promoCode = it },
                            label = { Text(if (isBengali) "কুপন কোড (COURIER50)" else "Promo Coupon") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Button(
                            onClick = {
                                if (promoCode.trim().uppercase() == "COURIER50") {
                                    discountAmount = 50
                                    Toast.makeText(context, "৫০ টাকা ডিসকাউন্ট যুক্ত হয়েছে!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "অকার্যকর কুপন", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(if (isBengali) "প্রয়োগ" else "Apply")
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("ডেলিভারি চার্জ (${selectedParcelType.nameBn}):", fontSize = 13.sp, color = Color.DarkGray)
                                Text("৳ ${selectedParcelType.basePrice}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                            if (selectedSpeed.extraFee > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("গতি চার্জ (${selectedSpeed.titleBn}):", fontSize = 13.sp, color = Color.DarkGray)
                                    Text("৳ ${selectedSpeed.extraFee}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (codFee > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("COD হ্যান্ডলিং ফি (১%):", fontSize = 13.sp, color = Color.DarkGray)
                                    Text("৳ $codFee", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (discountAmount > 0) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text("কুপন ছাড় (COURIER50):", fontSize = 13.sp, color = Color(0xFF16A34A))
                                    Text("-৳ $discountAmount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                                }
                            }
                            Divider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFF93C5FD))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("মোট পরিশোধযোগ্য চার্জ:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
                                Text("৳ $finalTotal", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E3A8A))
                            }
                        }
                    }

                    // SUBMIT BUTTON
                    Button(
                        onClick = {
                            if (senderName.isBlank() || receiverName.isBlank() || receiverAddress.isBlank()) {
                                Toast.makeText(context, if (isBengali) "অনুগ্রহ করে আবশ্যক তথ্যসমূহ পূরণ করুন" else "Please fill required fields", Toast.LENGTH_SHORT).show()
                            } else {
                                createdWaybillId = "SB-" + (100000..999999).random()
                                showWaybillDialog = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBengali) "পার্সেল বুকিং সম্পন্ন করুন" else "Confirm Parcel Booking", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            } else {
                // TRACK PARCEL VIEW
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search Bar
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(if (isBengali) "ওয়েবিল বা ট্র্যাকিং নম্বর লিখুন:" else "Enter Waybill / Tracking Code:", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = searchTrackingCode,
                                    onValueChange = { searchTrackingCode = it },
                                    modifier = Modifier.weight(1f),
                                    placeholder = { Text("উদা: SB-994812") },
                                    singleLine = true
                                )
                                Button(
                                    onClick = {
                                        searchedResultId = searchTrackingCode.trim().uppercase()
                                        Toast.makeText(context, if (isBengali) "ট্র্যাকিং স্ট্যাটাস আপডেট হয়েছে!" else "Tracking status updated!", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White)
                                }
                            }
                        }
                    }

                    // Live Status Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ট্র্যাকিং আইডি: ${searchedResultId ?: searchTrackingCode}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2563EB))
                                    Text("স্মল পার্সেল | এক্সপ্রেস ডেলিভারি", fontSize = 12.sp, color = Color.Gray)
                                }
                                Surface(
                                    color = Color(0xFFFEF3C7),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("প্রসেসিং চলছে", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                                }
                            }

                            Divider(modifier = Modifier.padding(vertical = 14.dp), color = Color(0xFFF1F5F9))

                            // 5-Step Timeline Stepper
                            Text(if (isBengali) "ডেলিভারি স্ট্যাটাস টাইমলাইন:" else "Delivery Progress Timeline:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(12.dp))

                            trackingSteps.forEachIndexed { index, step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (step.isCompleted) Color(0xFF16A34A)
                                                    else if (step.isCurrent) Color(0xFFD97706)
                                                    else Color.LightGray
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (step.isCompleted) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                            } else {
                                                Text("${step.stepNumber}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        if (index < trackingSteps.size - 1) {
                                            Box(
                                                modifier = Modifier
                                                    .width(2.dp)
                                                    .height(36.dp)
                                                    .background(if (step.isCompleted) Color(0xFF16A34A) else Color.LightGray)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = if (isBengali) step.titleBn else step.titleEn,
                                            fontWeight = if (step.isCurrent || step.isCompleted) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 14.sp,
                                            color = if (step.isCurrent) Color(0xFFD97706) else if (step.isCompleted) Color.Black else Color.Gray
                                        )
                                        Text(
                                            text = if (isBengali) step.subtitleBn else step.subtitleEn,
                                            fontSize = 12.sp,
                                            color = Color.Gray
                                        )
                                        Text(step.time, fontSize = 10.sp, color = Color.LightGray)
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // WAYBILL RECEIPT DIALOG
    if (showWaybillDialog && createdWaybillId != null) {
        AlertDialog(
            onDismissRequest = { showWaybillDialog = false },
            title = {
                Text(
                    text = if (isBengali) "পার্সেল বুকিং সফল হয়েছে!" else "Booking Confirmed!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF16A34A)
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("ওয়েবিল ট্র্যাকিং আইডি:", fontSize = 12.sp, color = Color.Gray)
                    Text(createdWaybillId!!, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF2563EB))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("প্রেরক: $senderName ($senderPhone)")
                    Text("প্রাপক: $receiverName ($receiverPhone)")
                    Text("গন্তব্য: $receiverAddress")
                    Text("মোট চার্জ: ৳ $finalTotal")
                    if (codVal > 0) {
                        Text("ক্যাশ অন ডেলিভারি (COD): ৳ $codVal", fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("নিকটস্থ রাইডার আপনার ঠিকানায় পার্সেল পিকআপের জন্য রওয়ানা হয়েছে।", fontSize = 12.sp, color = Color.DarkGray)
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showWaybillDialog = false
                        selectedTab = 1
                        searchTrackingCode = createdWaybillId!!
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                ) {
                    Text(if (isBengali) "ট্র্যাকিং দেখুন" else "Track Parcel", color = Color.White)
                }
            }
        )
    }
}
