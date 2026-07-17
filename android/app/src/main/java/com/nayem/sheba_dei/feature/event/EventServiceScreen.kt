package com.nayem.sheba_dei.feature.event

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@Composable
fun EventServiceScreen(
    onNavigateBack: () -> Unit
) {
    SetStatusBarColor()
    var showZillaFilterDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedZilla by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    Scaffold(
        containerColor = Color(0xFFF8FAFC), // Light gray background
        topBar = {
            com.nayem.sheba_dei.ui.components.GlobalAppBar(
                title = "ইভেন্ট সার্ভিস",
                onBackClick = onNavigateBack,
                actions = {
                    IconButton(onClick = { showZillaFilterDialog = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Black)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            com.nayem.sheba_dei.ui.components.CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = "জেলা নির্বাচন করুন",
                confirmButtonText = "বন্ধ করুন",
                onConfirm = { showZillaFilterDialog = false }
            ) {
                androidx.compose.foundation.lazy.LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
                ) {
                    item {
                        TextButton(onClick = { 
                            selectedZilla = null
                            showZillaFilterDialog = false 
                        }) {
                            Text("রিসেট", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(com.nayem.sheba_dei.core.utils.bangladeshZillas) { zilla ->
                        TextButton(
                            onClick = { 
                                selectedZilla = zilla
                                showZillaFilterDialog = false 
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = zilla,
                                color = if (selectedZilla == zilla) Color(0xFF1E3A8A) else Color.Black,
                                fontWeight = if (selectedZilla == zilla) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Start
                            )
                        }
                    }
                }
            }
        }

        LazyVerticalGrid(

            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Banner
            item(span = { GridItemSpan(3) }) {
                EventBanner()
            }

            // Categories
            items(eventCategories) { category ->
                EventCategoryItem(category)
            }
        }
    }
}

@Composable
fun EventBanner() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2FE)), // Light Blue background
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Decorative background circles
            Box(modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-30).dp)
                .clip(CircleShape)
                .background(Color(0xFFBAE6FD).copy(alpha = 0.5f)))
            
            Box(modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 80.dp, y = 50.dp)
                .clip(CircleShape)
                .border(8.dp, Color(0xFF0284C7), CircleShape)
                .background(Color.Transparent))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "আপনার কনস্ট্রাকশন সাইট,\nপ্রজেক্ট, আউটডোর প্রোগ্রামের",
                    color = Color(0xFF0F172A),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFF1E3A8A), RoundedCornerShape(6.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "ড্রোন ভিডিও ধারণের জন্য\nযোগাযোগ করুন!",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 20.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFF10B981), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Phone, contentDescription = "Phone", tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ফোন: 01703418374", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                }
            }
        }
    }
}

@Composable
fun EventCategoryItem(category: EventCategory) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { /* Handle Click */ },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF1F5F9))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.title,
                tint = category.iconColor,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = category.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

data class EventCategory(
    val title: String,
    val icon: ImageVector,
    val iconColor: Color,
    val bgColor: Color
)

val eventCategories = listOf(
    EventCategory("ক্যাটারিং সার্ভিস", Icons.Default.RestaurantMenu, Color(0xFFF97316), Color(0xFFFFEDD5)),
    EventCategory("বাবুর্চি", Icons.Default.SoupKitchen, Color(0xFFEF4444), Color(0xFFFEE2E2)),
    EventCategory("ফটোগ্রাফার", Icons.Default.CameraAlt, Color(0xFF3B82F6), Color(0xFFDBEAFE)),
    EventCategory("ইভেন্ট ম্যানেজমেন্ট", Icons.Default.Event, Color(0xFFEAB308), Color(0xFFFEF9C3)),
    EventCategory("কমিউনিটি সেন্টার", Icons.Default.Business, Color(0xFF8B5CF6), Color(0xFFEDE9FE)),
    EventCategory("ডেকোরেটর", Icons.Default.Celebration, Color(0xFF14B8A6), Color(0xFFCCFBF1)),
    EventCategory("লাইট, সাউন্ড", Icons.Default.Speaker, Color(0xFF334155), Color(0xFFF1F5F9)),
    EventCategory("আলপনা শিল্পী", Icons.Default.FormatPaint, Color(0xFFEC4899), Color(0xFFFCE7F3)),
    EventCategory("পার্লার", Icons.Default.Face, Color(0xFFD946EF), Color(0xFFFAE8FF)),
    EventCategory("মেহেদী আর্টিস্ট", Icons.Default.Brush, Color(0xFF84CC16), Color(0xFFECFCCB)),
    EventCategory("ব্যান্ড পার্টি", Icons.Default.MusicNote, Color(0xFF06B6D4), Color(0xFFCFFAFE))
)
