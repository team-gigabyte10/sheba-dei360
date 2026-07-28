package com.nayem.sheba_dei.feature.transport

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import com.nayem.sheba_dei.ui.components.GlobalAppBar

data class TicketSubcategory(
    val id: String,
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val url: String,
    val icon: ImageVector,
    val badgeTextBn: String,
    val badgeTextEn: String,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportServiceScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali

    val ticketSubcategories = listOf(
        TicketSubcategory(
            id = "train",
            titleBn = "বাংলাদেশ রেলওয়ে ট্রেন টিকিট",
            titleEn = "Bangladesh Railway Train Ticket",
            descBn = "বাংলাদেশ রেলওয়ের অফিসিয়াল ই-টিকিটিং পোর্টাল থেকে ঘরে বসেই সিট কাটুন।",
            descEn = "Book official Bangladesh Railway train tickets online easily.",
            url = "https://eticket.railway.gov.bd",
            icon = Icons.Default.Train,
            badgeTextBn = "অফিসিয়াল রেল সেবা",
            badgeTextEn = "Official Railway",
            accentColor = Color(0xFF0F766E)
        ),
        TicketSubcategory(
            id = "bus",
            titleBn = "বাস টিকিট বুকিং (সকল রুট)",
            titleEn = "Intercity Bus Ticket Booking",
            descBn = "শ্যামলী, হানিফ, এনা, গ্রীন লাইনসহ সকল বাস কোম্পানীর অনলাইন টিকিট।",
            descEn = "Book online tickets for Shyamoli, Hanif, Ena, Green Line and more.",
            url = "https://www.shohoz.com/bus-tickets",
            icon = Icons.Default.DirectionsBus,
            badgeTextBn = "অনলাইন বাস টিকিট",
            badgeTextEn = "Online Bus Ticket",
            accentColor = Color(0xFF2563EB)
        ),
        TicketSubcategory(
            id = "launch",
            titleBn = "লঞ্চ কেবিন ও টিকিট বুকিং",
            titleEn = "Launch Cabin & Deck Ticket",
            descBn = "সদরঘাট ও বরিশাল, পটুয়াখালী রুটের আধুনিক লঞ্চ কেবিন বুকিং সার্ভিস।",
            descEn = "Book launch cabins & deck tickets for Sadarghat & Barishal routes.",
            url = "https://www.shohoz.com/launch",
            icon = Icons.Default.DirectionsBoat,
            badgeTextBn = "লঞ্চ সার্ভিস",
            badgeTextEn = "Launch Service",
            accentColor = Color(0xFF0284C7)
        ),
        TicketSubcategory(
            id = "flight",
            titleBn = "বিমান / এয়ার টিকিট বুকিং",
            titleEn = "Flight Ticket Booking",
            descBn = "বিমান বাংলাদেশ ও অভ্যন্তরীণ সকল রুটের এয়ার টিকিট সহজে কাটুন।",
            descEn = "Book Biman Bangladesh and domestic/international flight tickets.",
            url = "https://www.biman-airlines.com",
            icon = Icons.Default.FlightTakeoff,
            badgeTextBn = "বিমান সেবা",
            badgeTextEn = "Airlines Ticket",
            accentColor = Color(0xFF7C3AED)
        )
    )

    fun openWebPortal(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                if (isBengali) "ওয়েব পেজটি খোলা যাচ্ছে না" else "Could not open web link",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "যাতায়াত সেবা (টিকিট)" else "Transport Services",
                onBackClick = onNavigateBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Header Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1E3A8A))
                    .padding(20.dp)
            ) {
                Column {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFF3B82F6).copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = if (isBengali) "অনলাইন টিকিট পোর্টাল" else "Online Ticketing Portals",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isBengali) "ঘরে বসেই কাটুন ট্রেন, বাস, লঞ্চ ও বিমানের টিকিট" else "Book Train, Bus, Launch & Flight tickets online",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBengali) "অফিসিয়াল ওয়েব পোর্টাল থেকে সরাসরি ই-টিকিট গ্রহণ করুন" else "Direct access to official BD e-ticketing websites",
                        color = Color(0xFF93C5FD),
                        fontSize = 12.sp
                    )
                }
            }

            // Subcategories List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ticketSubcategories) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { openWebPortal(item.url) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = item.accentColor.copy(alpha = 0.12f),
                                        modifier = Modifier.size(48.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = item.icon,
                                                contentDescription = null,
                                                tint = item.accentColor,
                                                modifier = Modifier.size(26.dp)
                                            )
                                        }
                                    }
                                    Column {
                                        Text(
                                            text = if (isBengali) item.titleBn else item.titleEn,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = if (isBengali) item.badgeTextBn else item.badgeTextEn,
                                            fontSize = 11.sp,
                                            color = item.accentColor,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open Web Link",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Text(
                                text = if (isBengali) item.descBn else item.descEn,
                                fontSize = 12.sp,
                                color = Color.DarkGray,
                                lineHeight = 16.sp
                            )

                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            Button(
                                onClick = { openWebPortal(item.url) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = item.accentColor),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "অনলাইন টিকিট কাটুন (অফিসিয়াল লিংক)" else "Book Online Ticket (Official Link)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
