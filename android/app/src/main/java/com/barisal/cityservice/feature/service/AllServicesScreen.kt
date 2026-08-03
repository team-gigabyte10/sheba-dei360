package com.barisal.cityservice.feature.service

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.ui.components.GlobalAppBar

data class ServiceCategoryItem(
    val key: String,
    val titleBan: String,
    val titleEng: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    onBack: () -> Unit,
    onCategoryClick: (String) -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali
    var searchQuery by remember { mutableStateOf("") }

    val allCategories = listOf(
        ServiceCategoryItem("historical", "ঐতিহাসিক স্থান", "Historical Place", Icons.Default.AccountBalance, Color(0xFFD97706)),
        ServiceCategoryItem("fire_service", "ফায়ার সার্ভিস", "Fire Service", Icons.Default.LocalFireDepartment, Color(0xFFEF4444)),
        ServiceCategoryItem("police", "থানা-পুলিশ", "Thana-Police", Icons.Default.LocalPolice, Color(0xFF1E3A8A)),
        ServiceCategoryItem("paurashava", "পৌর সেবা", "Municipality Service", Icons.Default.LocationCity, Color(0xFF0F766E)),
        ServiceCategoryItem("hotel", "হোটেল", "Hotel", Icons.Default.Hotel, Color(0xFF8B5CF6)),
        ServiceCategoryItem("restaurant", "রেস্টুরেন্ট", "Restaurant", Icons.Default.Restaurant, Color(0xFFF97316)),
        ServiceCategoryItem("education", "শিক্ষা প্রতিষ্ঠান", "Educational Institution", Icons.Default.CastForEducation, Color(0xFF2563EB)),
        ServiceCategoryItem("nursery", "নার্সারি", "Nursery", Icons.Default.Nature, Color(0xFF10B981)),
        ServiceCategoryItem("hospital", "হাসপাতাল", "Hospital", Icons.Default.LocalHospital, Color(0xFFEC4899))
    )

    val filteredCategories = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            allCategories
        } else {
            allCategories.filter {
                it.titleBan.contains(searchQuery, ignoreCase = true) ||
                        it.titleEng.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "লোকেশন ভিত্তিক সেবা" else "Location Based Services",
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isBengali) "সেবা খুঁজুন..." else "Search services...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF1E3A8A),
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isBengali) "সকল বিভাগ (${filteredCategories.size})" else "All Categories (${filteredCategories.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredCategories) { item ->
                    Card(
                        modifier = Modifier
                            .aspectRatio(0.9f)
                            .clickable { onCategoryClick(item.key) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(item.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.titleEng,
                                    tint = item.color,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isBengali) item.titleBan else item.titleEng,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B),
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
