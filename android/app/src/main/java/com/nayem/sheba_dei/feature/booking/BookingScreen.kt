package com.nayem.sheba_dei.feature.booking

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    providerId: String,
    onBack: () -> Unit,
    onConfirmBooking: () -> Unit
) {
    var showBookingDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "প্রোভাইডার",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Share action */ }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A8A), // Navy blue
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "৳৫০০ / ঘণ্টা",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.align(Alignment.End)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { showBookingDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)) // primary blue
                    ) {
                        Text("এখন বুক করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = Color(0xFFF8FAFC) // Very light gray background
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header: Cover Image + Floating Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Total height for the header section
                ) {
                    // Cover Image Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .background(Color.LightGray)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.dummy_cover),
                            contentDescription = "Cover Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    
                    // Floating Profile Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .align(Alignment.BottomCenter)
                            .offset(y = 20.dp), // Pull it down slightly to overlap
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
                        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.85f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Avatar
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(Color.LightGray)
                                        .border(2.dp, Color.White, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.dummy_person),
                                        contentDescription = "Profile",
                                        modifier = Modifier.size(72.dp).clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(16.dp))
                                
                                // Info
                                Column {
                                    Text("রহিম আহমেদ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                    Text("পেশাদার প্লাম্বার", fontSize = 16.sp, color = Color.Blue)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("৪.৮ ", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                        Text(" (১৮৫ টি রিভিউ)", fontSize = 16.sp, color = Color.Gray)
                                    }
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Chips Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Jobs completed chip
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFDCFCE7)) // Light green
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("১০০+ কাজ সম্পূর্ণ", fontSize = 16.sp, color = Color(0xFF166534))
                                }
                                
                                // Distance chip
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9)) // Light gray
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("০.৮ কি.মি.", fontSize = 16.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp)) // Space after the floating card
            }

            // Description Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("বিবরণ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "আমরা ঘরোয়া ও বাণিজ্যিক প্লাম্বিং সমস্যার সম্পূর্ণ সমাধান প্রদান করি.................................................",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 20.sp
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Services Section
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("সেবাসমূহ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        ServiceChip(icon = Icons.Default.Build, label = "মেরামতি", iconTint = Color.Gray)
                        ServiceChip(icon = Icons.Default.Handyman, label = "ফিটিং", iconTint = Color.DarkGray)
                        ServiceChip(icon = Icons.Default.WaterDrop, label = "ড্রেনেজ", iconTint = Color(0xFF3B82F6))
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Ratings and Reviews
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text("রেটিং ও রিভিউ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Mock Review 1
                    ReviewItem(name = "রজত সেন", role = "Professional Plumber", imageRes = R.drawable.dummy_person)
                    Spacer(modifier = Modifier.height(12.dp))
                    // Mock Review 2
                    ReviewItem(name = "অহিদুজ্জামান ঢাকা", role = "Professional Plumber", imageRes = R.drawable.dummy_person)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp)) // Extra space at bottom
            }
        }

        if (showBookingDialog) {
            BookingDialog(
                onDismiss = { showBookingDialog = false },
                onConfirm = {
                    showBookingDialog = false
                    onConfirmBooking()
                }
            )
        }
    }
}

@Composable
fun BookingDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    var selectedDate by remember { mutableStateOf("তারিখ নির্বাচন করুন") }
    var selectedTime by remember { mutableStateOf("সময় নির্বাচন করুন") }
    var address by remember { mutableStateOf("") }
    
    val paymentOptions = listOf("ক্যাশ অন ডেলিভারি", "বিকাশ (Bkash)", "নগদ (Nagad)")
    var selectedPayment by remember { mutableStateOf(paymentOptions[0]) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val calendar = java.util.Calendar.getInstance()
    
    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            selectedDate = "$dayOfMonth/${month + 1}/$year"
        },
        calendar.get(java.util.Calendar.YEAR),
        calendar.get(java.util.Calendar.MONTH),
        calendar.get(java.util.Calendar.DAY_OF_MONTH)
    )

    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM"
            val hour = if (hourOfDay % 12 == 0) 12 else hourOfDay % 12
            val formattedMinute = if (minute < 10) "0$minute" else minute.toString()
            val finalHour = if (hour == 0) 12 else hour
            selectedTime = "$finalHour:$formattedMinute $amPm"
        },
        calendar.get(java.util.Calendar.HOUR_OF_DAY),
        calendar.get(java.util.Calendar.MINUTE),
        false
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("বুকিং কনফার্মেশন", fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                // Date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { datePickerDialog.show() }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("তারিখ", fontSize = 12.sp, color = Color.Gray)
                        Text(selectedDate, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                    }
                }
                
                // Time
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { timePickerDialog.show() }
                        .padding(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = Color(0xFFF97316), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("সময়", fontSize = 12.sp, color = Color.Gray)
                        Text(selectedTime, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF97316))
                    }
                }
                
                // Address
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("ঠিকানা (Address)", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
                
                // Payment Method
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payments, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("পেমেন্ট পদ্ধতি", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    paymentOptions.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selectedPayment = text }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == selectedPayment),
                                onClick = { selectedPayment = text }
                            )
                            Text(
                                text = text,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = selectedDate != "তারিখ নির্বাচন করুন" && selectedTime != "সময় নির্বাচন করুন" && address.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("কনফার্ম করুন")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

@Composable
fun ServiceChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, iconTint: Color = Color.Gray) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White)
            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
    }
}

@Composable
fun ReviewItem(name: String, role: String, imageRes: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                modifier = Modifier.size(40.dp).clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = role, fontSize = 12.sp, color = Color.Gray)
        }
    }
}
