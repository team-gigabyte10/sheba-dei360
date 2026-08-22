package com.barisal.cityservice.feature.legal

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.model.LegalServiceDto
import com.barisal.cityservice.data.repository.LegalServiceRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalServiceDetailScreen(
    serviceId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val legalRepo = remember { LegalServiceRepository() }

    var serviceItem by remember { mutableStateOf<LegalServiceDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(serviceId) {
        serviceItem = legalRepo.getLegalServiceById(serviceId)
        isLoading = false
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বিস্তারিত প্রোফাইল" else "Detailed Profile",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1E293B))
            }
        } else if (serviceItem == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(if (isBengali) "তথ্য পাওয়া যায়নি" else "Listing not found", color = Color.Gray)
            }
        } else {
            val item = serviceItem!!
            val isLegal = item.categoryKey == "legal"
            val themeColor = if (isLegal) Color(0xFF1E293B) else Color(0xFFD97706)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Profile Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val coilModel = remember(item.profileImage) { item.profileImage.toCoilModel() }
                        if (coilModel != null) {
                            AsyncImage(
                                model = coilModel,
                                contentDescription = "Profile Photo",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, themeColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape)
                                    .background(themeColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isLegal) Icons.Default.Gavel else Icons.Default.Assignment,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = item.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A)
                        )

                        if (item.designation.isNotBlank()) {
                            Text(
                                text = item.designation,
                                fontSize = 14.sp,
                                color = themeColor,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        if (item.title.isNotBlank()) {
                            Text(
                                text = item.title,
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        Surface(
                            color = themeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            Text(
                                text = item.subCategory,
                                color = themeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Information Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (isBengali) "প্রফেশনাল তথ্য" else "Professional Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        if (item.chamberOrOffice.isNotBlank()) {
                            DetailRow(label = if (isBengali) "চেম্বার/অফিস" else "Chamber/Office", value = item.chamberOrOffice)
                        }

                        if (item.experience.isNotBlank()) {
                            DetailRow(label = if (isBengali) "অভিজ্ঞতা" else "Experience", value = item.experience)
                        }

                        if (item.feeInfo.isNotBlank()) {
                            DetailRow(label = if (isBengali) "ফি/খরচ" else "Fee Information", value = item.feeInfo)
                        }

                        if (item.location.isNotBlank()) {
                            DetailRow(label = if (isBengali) "অবস্থান/জেলা" else "Location", value = item.location)
                        }

                        if (item.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBengali) "বিস্তারিত বিবরণ:" else "Details:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = item.description,
                                fontSize = 13.sp,
                                color = Color(0xFF334155),
                                lineHeight = 18.sp
                            )
                        }
                    }
                }

                // Contact Action Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (isBengali) "যোগাযোগ করুন" else "Get in Touch",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        if (item.contact.isNotBlank()) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${item.contact}"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "কল করুন (${item.contact})" else "Call (${item.contact})", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (item.whatsapp.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val cleanNumber = item.whatsapp.replace("+", "").replace("-", "").replace(" ", "")
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=$cleanNumber"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF16A34A)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Text("WhatsApp (${item.whatsapp})", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "$label:", fontSize = 13.sp, color = Color.Gray, modifier = Modifier.weight(0.4f))
        Text(text = value, fontSize = 13.sp, color = Color(0xFF0F172A), fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(0.6f))
    }
}
