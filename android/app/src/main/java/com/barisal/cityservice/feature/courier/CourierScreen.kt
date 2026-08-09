package com.barisal.cityservice.feature.courier

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class BdCourier(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val websiteUrl: String,
    val trackingUrl: String,
    val hotlinePhone: String,
    val badgeTextBn: String,
    val badgeTextEn: String,
    val accentColor: Color,
    val icon: ImageVector
)

val popularBdCouriers = listOf(
    BdCourier(
        id = "steadfast",
        titleBn = "স্টেডফাস্ট কুরিয়ার (Steadfast)",
        titleEn = "Steadfast Courier",
        descBn = "৬৪ জেলায় দ্রুত ই-কমার্স হোম ডেলিভারি ও দ্রুত পেমেন্ট সুবিধা।",
        descEn = "Fast e-commerce home delivery and quick payment across 64 districts.",
        websiteUrl = "https://steadfast.com.bd",
        trackingUrl = "https://steadfast.com.bd/tracking",
        hotlinePhone = "09678045045",
        badgeTextBn = "ই-কমার্স পার্সেল",
        badgeTextEn = "E-Commerce Parcel",
        accentColor = Color(0xFF0F766E),
        icon = Icons.Default.LocalShipping
    ),
    BdCourier(
        id = "sundarban",
        titleBn = "সুন্দরবন কুরিয়ার সার্ভিস",
        titleEn = "Sundarban Courier Service",
        descBn = "বাংলাদেশের সবচেয়ে বিস্তৃত ও বৃহত্তম কুরিয়ার ও ডক্যুমেন্ট নেটওয়ার্ক।",
        descEn = "Largest traditional courier & parcel network across Bangladesh.",
        websiteUrl = "https://sundarbancourier.com.bd",
        trackingUrl = "https://sundarbancourier.com.bd",
        hotlinePhone = "09612007007",
        badgeTextBn = "জাতীয় কুরিয়ার",
        badgeTextEn = "National Courier",
        accentColor = Color(0xFF16A34A),
        icon = Icons.Default.MarkunreadMailbox
    ),
    BdCourier(
        id = "redx",
        titleBn = "রেডএক্স লজিস্টিকস (RedX)",
        titleEn = "RedX Logistics",
        descBn = "সারাদেশে ডোরস্টেপ ই-কমার্স পার্সেল ডেলিভারি ও ক্যাশ অন ডেলিভারি।",
        descEn = "Doorstep e-commerce parcel delivery & COD services nationwide.",
        websiteUrl = "https://redx.com.bd",
        trackingUrl = "https://redx.com.bd/track-parcel",
        hotlinePhone = "09610007339",
        badgeTextBn = "ডোরস্টেপ সিওডি",
        badgeTextEn = "Doorstep COD",
        accentColor = Color(0xFFDC2626),
        icon = Icons.Default.AirportShuttle
    ),
    BdCourier(
        id = "paperfly",
        titleBn = "পেপারফ্লাই (Paperfly)",
        titleEn = "Paperfly Logistics",
        descBn = "উপজেলা পর্যন্ত ডোরস্টেপ পার্সেল পিকআপ ও স্মার্ট অনলাইন ট্র্যাকিং।",
        descEn = "Doorstep parcel pickup & smart tracking down to Upazila level.",
        websiteUrl = "https://www.paperfly.com.bd",
        trackingUrl = "https://www.paperfly.com.bd/tracking.php",
        hotlinePhone = "09604040404",
        badgeTextBn = "স্মার্ট ট্র্যাকিং",
        badgeTextEn = "Smart Tracking",
        accentColor = Color(0xFFD97706),
        icon = Icons.Default.FlightTakeoff
    ),
    BdCourier(
        id = "pathao",
        titleBn = "পাঠাও কুরিয়ার (Pathao)",
        titleEn = "Pathao Courier",
        descBn = "মার্চেন্ট ও অনলাইন শপের জন্য দ্রুততম মার্চেন্ট ডেলিভারি সেবা।",
        descEn = "Fast merchant parcel delivery & instant tracking for businesses.",
        websiteUrl = "https://pathao.com/courier",
        trackingUrl = "https://pathao.com/courier",
        hotlinePhone = "09610007284",
        badgeTextBn = "ইনস্ট্যান্ট মার্চেন্ট",
        badgeTextEn = "Instant Merchant",
        accentColor = Color(0xFF2563EB),
        icon = Icons.Default.Moped
    ),
    BdCourier(
        id = "saparibahan",
        titleBn = "এস এ পরিবহন (SA Paribahan)",
        titleEn = "SA Paribahan Courier",
        descBn = "পরিবহন ও নির্ভরযোগ্য পার্সেল এবং মানি ট্রান্সফার সুবিধা।",
        descEn = "Trusted parcel transport & money transfer services nationwide.",
        websiteUrl = "https://saparibahan.com",
        trackingUrl = "https://saparibahan.com",
        hotlinePhone = "09611115555",
        badgeTextBn = "পরিবহন ও পার্সেল",
        badgeTextEn = "Transport & Parcel",
        accentColor = Color(0xFF7C3AED),
        icon = Icons.Default.DirectionsBus
    ),
    BdCourier(
        id = "ecourier",
        titleBn = "ই-কুরিয়ার (eCourier)",
        titleEn = "eCourier Logistics",
        descBn = "ডিজিটাল ই-কমার্স লজিস্টিকস, এক্সপ্রেস ডেলিভারি ও রিয়েলটাইম ট্র্যাকিং।",
        descEn = "Digital e-commerce logistics, express delivery & live tracking.",
        websiteUrl = "https://ecourier.com.bd",
        trackingUrl = "https://ecourier.com.bd/track-parcel/",
        hotlinePhone = "09612500500",
        badgeTextBn = "ডিজিটাল লজিস্টিকস",
        badgeTextEn = "Digital Logistics",
        accentColor = Color(0xFF0284C7),
        icon = Icons.Default.LocalShipping
    ),
    BdCourier(
        id = "korotoa",
        titleBn = "করোতোয়া কুরিয়ার",
        titleEn = "Korotoa Courier",
        descBn = "উত্তরবঙ্গসহ সারাদেশে দ্রুত বুকিং ও এক্সপ্রেস পার্সেল সেবা।",
        descEn = "Express booking & parcel handling service across Bangladesh.",
        websiteUrl = "https://korotoacourier.com",
        trackingUrl = "https://korotoacourier.com",
        hotlinePhone = "09613222333",
        badgeTextBn = "এক্সপ্রেস বুকিং",
        badgeTextEn = "Express Booking",
        accentColor = Color(0xFF059669),
        icon = Icons.Default.Inventory2
    )
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

    var searchQuery by remember { mutableStateOf("") }

    fun openWebUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, if (isBengali) "লিঙ্কটি খোলা যাচ্ছে না" else "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    fun makePhoneCall(phone: String) {
        try {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, if (isBengali) "কল করা যাচ্ছে না" else "Could not initiate call", Toast.LENGTH_SHORT).show()
        }
    }

    val filteredCouriers = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            popularBdCouriers
        } else {
            popularBdCouriers.filter {
                it.titleBn.contains(searchQuery, ignoreCase = true) ||
                        it.titleEn.contains(searchQuery, ignoreCase = true) ||
                        it.descBn.contains(searchQuery, ignoreCase = true)
            }
        }
    }

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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isBengali) "কুরিয়ার বা সেবার নাম লিখুন..." else "Search courier service...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredCouriers) { courier ->
                    BdCourierCard(
                        courier = courier,
                        isBengali = isBengali,
                        onWebsiteClick = { openWebUrl(courier.websiteUrl) },
                        onTrackingClick = { openWebUrl(courier.trackingUrl) },
                        onCallClick = { makePhoneCall(courier.hotlinePhone) }
                    )
                }
            }
        }
    }
}

@Composable
fun BdCourierCard(
    courier: BdCourier,
    isBengali: Boolean,
    onWebsiteClick: () -> Unit,
    onTrackingClick: () -> Unit,
    onCallClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(courier.accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(courier.icon, contentDescription = null, tint = courier.accentColor, modifier = Modifier.size(26.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBengali) courier.titleBn else courier.titleEn,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            color = courier.accentColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (isBengali) courier.badgeTextBn else courier.badgeTextEn,
                                color = courier.accentColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = if (isBengali) courier.descBn else courier.descEn,
                        fontSize = 12.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("হটলাইন: ${courier.hotlinePhone}", fontSize = 12.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = onWebsiteClick,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = courier.accentColor),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "ওয়েবসাইট" else "Website", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onTrackingClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBengali) "ট্র্যাকিং" else "Tracking", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCallClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF1F5F9))
                ) {
                    Icon(Icons.Default.Call, contentDescription = "Call", tint = Color(0xFF16A34A), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
