package com.barisal.cityservice.feature.service

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.data.model.SubCategoryItem
import com.barisal.cityservice.data.repository.CategoryRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.core.utils.toCoilModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubCategoryScreen(
    categoryId: Int = 1,
    categoryName: String = "Services",
    onNavigateBack: () -> Unit = {},
    onNavigateToSubCategoryDetail: (SubCategoryItem) -> Unit = {}
) {
    val isBengali = LocalAppLanguage.current.isBengali
    val categoryRepository = remember { CategoryRepository() }

    val subCategories by categoryRepository.getSubCategoriesFlow(categoryId)
        .collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "$categoryName - সাব-ক্যাটাগরি" else "$categoryName Sub-Categories",
                onBackClick = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = if (isBengali) "আপনার প্রয়োজনীয় সেবাটি বেছে নিন" else "Choose Your Required Sub-Service",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (subCategories.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Category,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isBengali) "কোন সাব-ক্যাটাগরি পাওয়া যায়নি" else "No Sub-Categories Available",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subCategories) { item ->
                        SubCategoryCardItem(
                            item = item,
                            isBengali = isBengali,
                            onClick = { onNavigateToSubCategoryDetail(item) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SubCategoryCardItem(
    item: SubCategoryItem,
    isBengali: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.9f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
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
            if (!item.iconUrl.isNullOrBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(model = item.iconUrl.toCoilModel()),
                    contentDescription = item.getDisplayName(isBengali),
                    modifier = Modifier.size(40.dp)
                )
            } else {
                Icon(
                    imageVector = item.fallbackIcon ?: Icons.Default.Category,
                    contentDescription = item.getDisplayName(isBengali),
                    tint = Color(0xFF0F766E),
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = item.getDisplayName(isBengali),
                fontSize = 12.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}
