package com.barisal.cityservice.feature.emergency

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class EmergencyHotline(
    val titleBn: String,
    val titleEn: String,
    val number: String,
    val subtitleBn: String,
    val subtitleEn: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyServiceScreen(
    onNavigateBack: () -> Unit,
    onNavigateToCategoryMap: (String) -> Unit = {}
) {
    SetStatusBarColor()
    BackHandler {
        onNavigateBack()
    }

    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    val hotlines = listOf(
        EmergencyHotline("জাতীয় জরুরি সেবা", "National Emergency", "999", "পুলিশ, ফায়ার সার্ভিস ও অ্যাম্বুলেন্স", "Police, Fire & Ambulance", Icons.Default.LocalHospital, Color(0xFFDC2626)),
        EmergencyHotline("জাতীয় তথ্য ও সেবা", "National Helpline", "333", "সরকারি তথ্য ও সামাজিক সাহায্য", "Govt Info & Social Help", Icons.Default.PhoneInTalk, Color(0xFF2563EB)),
        EmergencyHotline("স্বাস্থ্য বাতায়ন", "Health Line", "16263", "২৪ ঘণ্টা বিনামূল্যে চিকিৎসকের পরামর্শ", "24/7 Free Doctor Consultation", Icons.Default.MedicalServices, Color(0xFF059669)),
        EmergencyHotline("বিদ্যুৎ জরুরি অভিযোগ", "Electricity Helpline", "16999", "বিদ্যুৎ বিভ্রাট ও জরুরি মেরামত", "Power Outage & Line Repair", Icons.Default.ElectricBolt, Color(0xFFD97706)),
        EmergencyHotline("নারী ও শিশু সহায়তা", "Women & Child Help", "109", "নির্যাতন প্রতিরোধ ও সহায়তা", "Abuse Prevention & Support", Icons.Default.Shield, Color(0xFF7C3AED)),
        EmergencyHotline("দুর্যোগের আগাম বার্তা", "Disaster Alert", "1090", "আবহাওয়া ও দুর্যোগ সতর্কবার্তা", "Weather & Disaster Warnings", Icons.Default.Warning, Color(0xFFEA580C))
    )

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "জরুরী সেবা" else "Emergency Services",
                onBackClick = onNavigateBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            // Hero Banner
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFDC2626),
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Emergency, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (isBengali) "জরুরী হটলাইন ও সেবাসমূহ" else "Emergency Hotlines & Services",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isBengali) "এক ক্লিকেই সরাসরি কল করুন যেকোনো জরুরি প্রয়োজনে" else "One-tap direct call for instant emergency response",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Direct Call Emergency Hotlines
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isBengali) "জাতীয় জরুরী হেল্পলাইন (Direct Call)" else "National Emergency Hotlines",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            items(hotlines) { hotline ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White,
                    shadowElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(hotline.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(hotline.icon, contentDescription = null, tint = hotline.color, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isBengali) hotline.titleBn else hotline.titleEn,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = hotline.color.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = hotline.number,
                                        color = hotline.color,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = if (isBengali) hotline.subtitleBn else hotline.subtitleEn,
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${hotline.number}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(hotline.color)
                                .size(36.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
