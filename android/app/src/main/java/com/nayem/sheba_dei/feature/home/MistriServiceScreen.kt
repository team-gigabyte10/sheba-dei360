package com.nayem.sheba_dei.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MistriCategory(
    val name: String,
    val icon: ImageVector,
    val route: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MistriServiceScreen(
    onNavigateBack: () -> Unit
) {

    var showZillaFilterDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedZilla by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<String?>(null) }

    val categories = listOf(
        MistriCategory("রাজ মিস্ত্রি", Icons.Default.Construction),
        MistriCategory("কাঠ মিস্ত্রি", Icons.Default.Handyman),
        MistriCategory("রং মিস্ত্রি", Icons.Default.FormatPaint),
        MistriCategory("টিউবওয়েল মিস্ত্রি", Icons.Default.Plumbing),
        MistriCategory("সেনেটারি মিস্ত্রি", Icons.Default.WaterDrop),
        MistriCategory("টাইলস মিস্ত্রি", Icons.Default.Dashboard),
        MistriCategory("গ্রিল মিস্ত্রি", Icons.Default.Fence),
        MistriCategory("থাই মিস্ত্রি", Icons.Default.Window),
        MistriCategory("গাড়ি সার্ভিসিং", Icons.Default.DirectionsCar),
        MistriCategory("বাইকের মিস্ত্রি", Icons.Default.TwoWheeler),
        MistriCategory("WiFi টেকনিশিয়ান", Icons.Default.Wifi),
        MistriCategory("এসি সার্ভিসিং", Icons.Default.AcUnit),
        MistriCategory("ফ্রিজ সার্ভিসিং", Icons.Default.Kitchen),
        MistriCategory("ইন্টেরিয়র মিস্ত্রি", Icons.Default.Chair),
        MistriCategory("ইলেকট্রিশিয়ান", Icons.Default.ElectricBolt),
        MistriCategory("মোবাইল সার্ভিসিং", Icons.Default.PhoneAndroid),
        MistriCategory("কম্পিউটার সার্ভিসিং", Icons.Default.Computer),
        MistriCategory("CCTV সার্ভিসিং", Icons.Default.Videocam),
        MistriCategory("টিভি সার্ভিসিং", Icons.Default.Tv),
        MistriCategory("অন্যান্য মিস্ত্রি", Icons.Default.MoreHoriz)
    )

    Scaffold(
        topBar = {
            com.nayem.sheba_dei.ui.components.GlobalAppBar(
                title = "মিস্ত্রি সার্ভিস",
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8FAFC)) // Light gray background
                .padding(innerPadding)
        ) {
            // Ad Banner Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(130.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2563EB)) // Blue Banner similar to image
                    .padding(16.dp)
            ) {
                // Banner text content
                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                    Text("আপনার সেবার", color = Color.White, fontSize = 14.sp)
                    Text("বিজ্ঞাপন দিন", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF10B981))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("আজই বিজ্ঞাপন দিন", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Categories Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    MistriCategoryItem(category = category)
                }
            }
        }
    }
}

@Composable
fun MistriCategoryItem(category: MistriCategory) {
    val colorPalette = listOf(
        Pair(Color(0xFF3B82F6), Color(0xFFDBEAFE)), // Blue
        Pair(Color(0xFF14B8A6), Color(0xFFCCFBF1)), // Teal
        Pair(Color(0xFFF97316), Color(0xFFFFEDD5)), // Orange
        Pair(Color(0xFF8B5CF6), Color(0xFFEDE9FE)), // Purple
        Pair(Color(0xFFEF4444), Color(0xFFFEE2E2)), // Red
        Pair(Color(0xFF10B981), Color(0xFFD1FAE5)), // Emerald
        Pair(Color(0xFFEAB308), Color(0xFFFEF9C3)), // Yellow
        Pair(Color(0xFFEC4899), Color(0xFFFCE7F3))  // Pink
    )
    val colorIndex = kotlin.math.abs(category.name.hashCode()) % colorPalette.size
    val iconColor = colorPalette[colorIndex].first
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable { },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.name,
                tint = iconColor,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = category.name,
                fontSize = 11.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}
