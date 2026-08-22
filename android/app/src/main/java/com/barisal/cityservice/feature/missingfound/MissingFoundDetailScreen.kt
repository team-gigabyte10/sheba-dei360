package com.barisal.cityservice.feature.missingfound

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.barisal.cityservice.data.model.MissingFoundDto
import com.barisal.cityservice.data.repository.MissingFoundRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissingFoundDetailScreen(
    id: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repo = remember { MissingFoundRepository() }

    var item by remember { mutableStateOf<MissingFoundDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(id) {
        item = repo.getMissingFoundPostById(id)
        isLoading = false
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "বিজ্ঞপ্তির বিস্তারিত" else "Notice Details",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFDC2626))
            }
        } else if (item == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(if (isBengali) "বিজ্ঞপ্তি পাওয়া যায়নি" else "Notice details not found", color = Color.Gray)
            }
        } else {
            val dto = item!!
            val isMissing = dto.noticeType == "missing"
            val typeColor = if (isMissing) Color(0xFFDC2626) else Color(0xFF16A34A)
            val typeText = if (isMissing) {
                if (isBengali) "নিখোঁজ বিজ্ঞপ্তি" else "MISSING NOTICE"
            } else {
                if (isBengali) "প্রাপ্তি বিজ্ঞপ্তি (পাওয়া গেছে)" else "FOUND NOTICE"
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Image & Title Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        val coilModel = remember(dto.photo) { dto.photo.toCoilModel() }
                        if (coilModel != null) {
                            AsyncImage(
                                model = coilModel,
                                contentDescription = "Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Column(modifier = Modifier.padding(18.dp)) {
                            Surface(
                                color = typeColor,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = typeText,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = dto.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color(0xFF0F172A)
                            )

                            if (dto.subCategory.isNotBlank()) {
                                Text(
                                    text = dto.subCategory,
                                    fontSize = 13.sp,
                                    color = typeColor,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }

                // Details Card
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
                            text = if (isBengali) "বিজ্ঞপ্তির তথ্য" else "Notice Details",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        if (dto.nameOrItem.isNotBlank()) {
                            DetailRow(label = if (isBengali) "নাম/জিনিস" else "Name/Item", value = dto.nameOrItem)
                        }

                        if (dto.ageOrDetails.isNotBlank()) {
                            DetailRow(label = if (isBengali) "বয়স/বিবরণ" else "Age/Details", value = dto.ageOrDetails)
                        }

                        if (dto.incidentDate.isNotBlank()) {
                            DetailRow(label = if (isBengali) "ঘটনার তারিখ" else "Date of Incident", value = dto.incidentDate)
                        }

                        if (dto.location.isNotBlank()) {
                            DetailRow(label = if (isBengali) "স্থান/এলাকা" else "Location", value = dto.location)
                        }

                        if (dto.rewardOrNote.isNotBlank()) {
                            DetailRow(label = if (isBengali) "পুরস্কার/নোট" else "Reward/Note", value = dto.rewardOrNote)
                        }

                        if (dto.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isBengali) "বিস্তারিত বিবরণ:" else "Description:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.DarkGray
                            )
                            Text(
                                text = dto.description,
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
                            text = if (isBengali) "যোগাযোগের মাধ্যম" else "Contact Options",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        if (dto.contact.isNotBlank()) {
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${dto.contact}"))
                                    context.startActivity(intent)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = typeColor),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                            ) {
                                Icon(Icons.Default.Phone, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(if (isBengali) "কল করুন (${dto.contact})" else "Call (${dto.contact})", fontWeight = FontWeight.Bold)
                            }
                        }

                        if (dto.whatsapp.isNotBlank()) {
                            OutlinedButton(
                                onClick = {
                                    val cleanNumber = dto.whatsapp.replace("+", "").replace("-", "").replace(" ", "")
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
                                Text("WhatsApp (${dto.whatsapp})", fontWeight = FontWeight.Bold)
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
