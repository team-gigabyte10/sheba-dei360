package com.nayem.sheba_dei.feature.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BookingSuccessScreen(
    onTrackBooking: () -> Unit,
    onReturnHome: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF1F5F9) // Light gray-blue background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))
            
            // Header: Green Checkmark
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF22C55E)), // Emerald Green
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Success Text
            Text(
                text = "বুকিং সফল হয়েছে",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ধন্যবাদ! আপনার সোফা পরিষ্কারের বুকিংটি\nকনফার্ম হয়েছে।",
                fontSize = 14.sp,
                color = Color.DarkGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Booking Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    // Card Header (Booking ID)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC)) // Very light gray header
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "বুকিং আইডি: SD360-123456",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B) // Dark slate
                        )
                    }
                    
                    // Card Content
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text("বুকিং বিবরণ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Service Row with Icon
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("সেবা:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(2f)
                            ) {
                                Icon(Icons.Default.Weekend, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("সোফা পরিষ্কার", fontSize = 14.sp, color = Color.Black)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Date & Time
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("তারিখ ও সময়:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text("২৭ অক্টোবর ২০২৩,\nবিকাল ৩:০০", fontSize = 14.sp, color = Color.Black, modifier = Modifier.weight(2f))
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Address
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("ঠিকানা:", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.weight(1f))
                            Text("ফ্ল্যাট ৫বি, ব্লক সি, রোড ২,\nমিরপুর-১১, ঢাকা - ১২১৬", fontSize = 14.sp, color = Color.Black, modifier = Modifier.weight(2f))
                        }
                        
                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Payment Receipt
                        Text("পেমেন্ট রসিদ", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        InfoRow(label = "সেবা মূল্য:", value = "৳ ৮৫০")
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "ভ্যাট (৫%):", value = "৳ ৪৩")
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        InfoRow(label = "সর্বমোট মূল্য:", value = "৳ ৮৯৩", isBold = true)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(label = "পেমেন্ট মোড:", value = "ক্যাশ অন ডেলিভারি")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Buttons
            Button(
                onClick = onTrackBooking,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text("বুকিং ট্র্যাক করুন", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = onReturnHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("হোম স্ক্রিনে ফিরে যান", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2563EB))
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label, 
            fontSize = if (isBold) 16.sp else 14.sp, 
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) Color.Black else Color.Gray
        )
        Text(
            text = value, 
            fontSize = if (isBold) 16.sp else 14.sp, 
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
    }
}
