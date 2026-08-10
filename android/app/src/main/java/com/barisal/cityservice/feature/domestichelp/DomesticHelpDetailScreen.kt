package com.barisal.cityservice.feature.domestichelp

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
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
import com.barisal.cityservice.data.model.DomesticHelpDto
import com.barisal.cityservice.data.repository.DomesticHelpRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DomesticHelpDetailScreen(
    helpId: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val domesticHelpRepo = remember { DomesticHelpRepository() }

    val firestoreHelps by domesticHelpRepo.getApprovedDomesticHelps().collectAsState(initial = emptyList())
    val help = firestoreHelps.find { it.id == helpId }

    val primaryColor = Color(0xFFD97706)

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "গৃহকর্মীর বিস্তারিত" else "Domestic Help Details",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (help == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = primaryColor)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Image
            if (help.coverImage.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    AsyncImage(
                        model = help.coverImage.toCoilModel(context),
                        contentDescription = "Maid Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Main Profile Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(primaryColor.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CleaningServices, contentDescription = null, tint = primaryColor, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = help.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF0F172A))
                            Text(text = "${help.providerName} • ${help.subCategory}", fontSize = 14.sp, color = primaryColor, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Meta Details
                    DetailRow(icon = Icons.Default.Category, label = if (isBengali) "সেবার ধরন" else "Service Type", value = help.subCategory)
                    DetailRow(icon = Icons.Default.Schedule, label = if (isBengali) "কাজের ধরণ" else "Work Type", value = help.workType)
                    DetailRow(icon = Icons.Default.Payments, label = if (isBengali) "প্রত্যাশিত বেতন/মজুরি" else "Expected Rate", value = help.expectedSalary)
                    if (help.experience.isNotBlank()) {
                        DetailRow(icon = Icons.Default.History, label = if (isBengali) "অভিজ্ঞতা" else "Experience", value = help.experience)
                    }
                    DetailRow(icon = Icons.Default.LocationOn, label = if (isBengali) "এলাকা/ঠিকানা" else "Location", value = help.location)
                }
            }

            // Description Card
            if (help.description.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = if (isBengali) "কাজের বিবরণ ও বিশেষ দক্ষতা" else "Service Details & Skills", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = help.description, fontSize = 14.sp, color = Color(0xFF334155), lineHeight = 20.sp)
                    }
                }
            }

            // Contact & Call Card
            if (help.contact.isNotBlank()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = if (isBengali) "যোগাযোগ করুন" else "Contact Information", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${help.contact}"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "${if (isBengali) "কল করুন:" else "Call:"} ${help.contact}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "$label: ", fontWeight = FontWeight.Medium, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF0F172A))
    }
}
