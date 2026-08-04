package com.barisal.cityservice.feature.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventBudgetCalculatorDialog(
    onDismissRequest: () -> Unit
) {
    val isBengali = LocalAppLanguage.current.isBengali

    var guestCount by remember { mutableStateOf(150) }
    var includeCatering by remember { mutableStateOf(true) }
    var includeDecoration by remember { mutableStateOf(true) }
    var includePhotography by remember { mutableStateOf(true) }
    var includeLighting by remember { mutableStateOf(false) }
    var includeParlor by remember { mutableStateOf(false) }

    val cateringPrice = if (includeCatering) guestCount * 380 else 0
    val decorationPrice = if (includeDecoration) 18000 else 0
    val photographyPrice = if (includePhotography) 15000 else 0
    val lightingPrice = if (includeLighting) 10000 else 0
    val parlorPrice = if (includeParlor) 8000 else 0

    val grandTotal = cateringPrice + decorationPrice + photographyPrice + lightingPrice + parlorPrice

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Calculate, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isBengali) "ইভেন্ট বাজেট ক্যালকুলেটর" else "Event Budget Calculator",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = if (isBengali) "আপনার অনুষ্ঠানের সম্ভাব্য মেহমান সংখ্যা ও সেবা নির্বাচন করুন:" else "Select guest count and required services:",
                    fontSize = 12.sp,
                    color = Color.DarkGray
                )

                // Guest Count Selector
                Text(
                    text = if (isBengali) "মেহমান সংখ্যা (Guest Count): $guestCount জন" else "Guest Count: $guestCount Guests",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(50, 100, 150, 300, 500).forEach { count ->
                        FilterChip(
                            selected = guestCount == count,
                            onClick = { guestCount = count },
                            label = { Text("$count", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF2563EB),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                // Service Checkboxes
                Text(
                    text = if (isBengali) "প্রয়োজনীয় ইভেন্ট সার্ভিসসমূহ:" else "Required Event Services:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )

                // Catering
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeCatering, onCheckedChange = { includeCatering = it })
                        Text(if (isBengali) "খাবার / ক্যাটারিং (৳৩৮০/জন)" else "Catering (৳380/guest)", fontSize = 12.sp)
                    }
                    Text("৳$cateringPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                // Decoration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeDecoration, onCheckedChange = { includeDecoration = it })
                        Text(if (isBengali) "স্টেজ & ডেকোরেশন" else "Stage & Decoration", fontSize = 12.sp)
                    }
                    Text("৳$decorationPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                // Photography
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includePhotography, onCheckedChange = { includePhotography = it })
                        Text(if (isBengali) "ফটোগ্রাফি & সিনেমাটোগ্রাফি" else "Photography & Video", fontSize = 12.sp)
                    }
                    Text("৳$photographyPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                // Lighting & Sound
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeLighting, onCheckedChange = { includeLighting = it })
                        Text(if (isBengali) "সাউন্ড & লাইটিং সিস্টেম" else "Sound & Lighting System", fontSize = 12.sp)
                    }
                    Text("৳$lightingPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                // Parlor / Mehendi
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = includeParlor, onCheckedChange = { includeParlor = it })
                        Text(if (isBengali) "মেহেদী & বিউটি পার্লার" else "Mehendi & Parlor Service", fontSize = 12.sp)
                    }
                    Text("৳$parlorPrice", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF16A34A))
                }

                // Total Result Box
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2563EB))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBengali) "আনুমানিক মোট বাজেট:" else "Estimated Total Budget:",
                            fontSize = 12.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "৳ $grandTotal",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismissRequest,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(if (isBengali) "ঠিক আছে" else "Got It")
            }
        }
    )
}
