package com.barisal.cityservice.feature.shopping

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.barisal.cityservice.core.language.LocalAppLanguage
import com.barisal.cityservice.core.utils.bangladeshZillas
import com.barisal.cityservice.core.utils.toCoilModel
import com.barisal.cityservice.data.repository.ShoppingRepository
import com.barisal.cityservice.ui.components.CustomDialog
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

data class ProductInfo(
    val id: String,
    val productName: String,
    val shopName: String,
    val date: String,
    val price: String,
    val address: String,
    val latLng: String,
    val contactInfo: String,
    val description: String,
    val condition: String = "Used",
    val imageUrls: List<String> = emptyList()
)

data class ShoppingCategory(
    val nameEng: String,
    val nameBan: String,
    val icon: ImageVector
)

val shoppingCategories = listOf(
    ShoppingCategory("Mobiles", "মোবাইল", Icons.Default.Smartphone),
    ShoppingCategory("Computers & Laptops", "কম্পিউটার/ল্যাপটপ", Icons.Default.Computer),
    ShoppingCategory("Electronics Products", "ইলেকট্রনিক্স পন্য", Icons.Default.Devices),
    ShoppingCategory("Vehicles & Property", "গাড়ি ও প্রপার্টি", Icons.Default.DirectionsCar),
    ShoppingCategory("Home & Living", "হোম ও লিভিং", Icons.Default.Chair),
    ShoppingCategory("Repair & Construction", "রিপেয়ার ও কনস্ট্রাকশন", Icons.Default.Construction),
    ShoppingCategory("Animals & Pets", "পশুপাখি ও পেটস", Icons.Default.Pets),
    ShoppingCategory("Others", "অন্যান্য", Icons.Default.MoreHoriz)
)

val dummyProducts = listOf(
    ProductInfo(
        id = "1",
        productName = "Walton 32 GB Blue",
        shopName = "Mohshin Telecom",
        date = "12 Jun 2026",
        price = "BDT 4,200",
        address = "Pabna, Pabna Sadar",
        latLng = "23.6061,89.8406",
        contactInfo = "01711-223344",
        description = "Walton 32 GB Blue. Fresh condition.",
        condition = "Used"
    ),
    ProductInfo(
        id = "2",
        productName = "Vivo Y20 64 GB Blue",
        shopName = "Mohshin Telecom",
        date = "10 Jun 2026",
        price = "BDT 5,300",
        address = "Pabna, Pabna Sadar",
        latLng = "23.6012,89.8322",
        contactInfo = "01712-334455",
        description = "4GB RAM, 64GB ROM. No scratches.",
        condition = "Used"
    ),
    ProductInfo(
        id = "3",
        productName = "HP Core i5 Laptop 11th Gen",
        shopName = "Tech World BD",
        date = "08 Jun 2026",
        price = "BDT 45,000",
        address = "Faridpur, Faridpur Sadar",
        latLng = "23.6061,89.8406",
        contactInfo = "01713-445566",
        description = "8GB RAM, 512GB SSD. Excellent condition.",
        condition = "Used"
    ),
    ProductInfo(
        id = "4",
        productName = "Redmi Note 12 (8/128)",
        shopName = "Mobile Point",
        date = "05 Jun 2026",
        price = "BDT 15,000",
        address = "Dhaka, New Market",
        latLng = "23.7000,90.4000",
        contactInfo = "01714-556677",
        description = "Official variant with box and charger.",
        condition = "Used"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    onBack: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onNavigateToPostProduct: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    SetStatusBarColor(colorString = "#FFFFFF", isLightIcons = false)

    var searchQuery by remember { mutableStateOf("") }
    var showZillaFilterDialog by remember { mutableStateOf(false) }
    var selectedZilla by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var selectedCondition by remember { mutableStateOf("All") } // All, New, Used, Verified
    var isGridView by remember { mutableStateOf(true) }

    var favoriteProductIds by remember { mutableStateOf(setOf<String>()) }
    var cartItems by remember { mutableStateOf(mapOf<ProductInfo, Int>()) }
    var showCartDialog by remember { mutableStateOf(false) }
    var showWishlistOnly by remember { mutableStateOf(false) }

    val primaryColor = Color(0xFF059669) // Green theme accent

    val context = LocalContext.current
    val shoppingRepo = remember { ShoppingRepository() }
    val firestoreProductsDto by shoppingRepo.getApprovedProducts().collectAsState(initial = emptyList())

    val firestoreProducts = remember(firestoreProductsDto) {
        firestoreProductsDto.map { dto ->
            val dateStr = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault()).format(java.util.Date(dto.createdAt))
            val priceStr = if (dto.price.startsWith("BDT") || dto.price.contains("টাকা")) dto.price else "BDT ${dto.price}"
            ProductInfo(
                id = dto.id,
                productName = dto.productName,
                shopName = if (dto.userDisplayName.isNotBlank()) dto.userDisplayName else "Seller",
                date = dateStr,
                price = priceStr,
                address = dto.address.ifBlank { "Barisal" },
                latLng = dto.latLng,
                contactInfo = dto.contactInfo,
                description = dto.description,
                condition = dto.condition,
                imageUrls = dto.imageUrls
            )
        }
    }

    val totalCartCount = cartItems.values.sum()

    Scaffold(
        containerColor = Color(0xFFF8FAFC),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(bottom = 10.dp)
            ) {
                GlobalAppBar(
                    title = if (isBengali) "বেচা-কেনা" else "Shopping",
                    onBackClick = onBack,
                    containerColor = Color.White,
                    contentColor = Color.Black,
                    actions = {
                        IconButton(onClick = { showWishlistOnly = !showWishlistOnly }) {
                            Icon(
                                imageVector = if (showWishlistOnly) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Wishlist",
                                tint = if (showWishlistOnly) Color(0xFFE11D48) else Color.DarkGray
                            )
                        }

                        IconButton(
                            onClick = { showCartDialog = true },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            BadgedBox(
                                badge = {
                                    if (totalCartCount > 0) {
                                        Badge(containerColor = primaryColor, contentColor = Color.White) {
                                            Text("$totalCartCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = primaryColor)
                            }
                        }
                    }
                )

                // Single Row: 64 Zilla Dropdown Button & Search Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 1. 64 Zilla Selector
                    Row(
                        modifier = Modifier
                            .widthIn(max = 135.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { showZillaFilterDialog = true }
                            .padding(horizontal = 10.dp, vertical = 9.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = selectedZilla ?: (if (isBengali) "বাংলাদেশ..." else "Banglade..."),
                            color = Color(0xFF1F2937),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Select Zilla",
                            tint = Color(0xFF4B5563),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // 2. Search Box
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFF1F5F9))
                            .padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = if (isBengali) "আমি খুঁজছি..." else "I am looking for...",
                                    color = Color(0xFF6B7280),
                                    fontSize = 13.sp
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = TextStyle(color = Color(0xFF1F2937), fontSize = 13.sp),
                                cursorBrush = SolidColor(Color(0xFF1F2937)),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF6B7280),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToPostProduct() },
                containerColor = primaryColor,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = if (isBengali) "পণ্য যোগ করুন" else "Post Product")
            }
        }
    ) { innerPadding ->
        if (showZillaFilterDialog) {
            CustomDialog(
                onDismissRequest = { showZillaFilterDialog = false },
                title = if (isBengali) "জেলা নির্বাচন করুন" else "Select Zilla",
                confirmButtonText = if (isBengali) "বন্ধ করুন" else "Close",
                onConfirm = { showZillaFilterDialog = false }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                ) {
                    item {
                        TextButton(onClick = {
                            selectedZilla = null
                            showZillaFilterDialog = false
                        }) {
                            Text(if (isBengali) "রিসেট (সকল বাংলাদেশ)" else "Reset (All Bangladesh)", color = Color.Red, fontWeight = FontWeight.Bold)
                        }
                    }
                    items(bangladeshZillas) { zilla ->
                        TextButton(
                            onClick = {
                                selectedZilla = zilla
                                showZillaFilterDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = zilla,
                                color = if (selectedZilla == zilla) Color(0xFF059669) else Color.Black,
                                fontWeight = if (selectedZilla == zilla) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Start
                            )
                        }
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC)),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Categories Grid (4 items per row)
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val filteredCategories = if (searchQuery.isNotEmpty()) {
                        shoppingCategories.filter {
                            it.nameEng.contains(searchQuery, ignoreCase = true) ||
                            it.nameBan.contains(searchQuery, ignoreCase = true)
                        }
                    } else {
                        shoppingCategories
                    }

                    filteredCategories.chunked(4).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowCategories.forEach { category ->
                                CategoryCardItem(
                                    category = category,
                                    isBengali = isBengali,
                                    isSelected = selectedCategory == category.nameEng,
                                    modifier = Modifier.weight(1f),
                                    onClick = {
                                        selectedCategory = if (selectedCategory == category.nameEng) null else category.nameEng
                                    }
                                )
                            }
                            repeat(4 - rowCategories.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            // Section Header: Trending & Condition Filter Chips
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Column(modifier = Modifier.padding(horizontal = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showWishlistOnly) (if (isBengali) "পছন্দের তালিকা" else "Wishlist") else (if (isBengali) "জনপ্রিয় পণ্য" else "Trending Items"),
                            color = Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(
                            onClick = { isGridView = !isGridView },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            Icon(
                                imageVector = if (isGridView) Icons.Default.FormatListBulleted else Icons.Default.GridView,
                                contentDescription = "Toggle View",
                                tint = Color(0xFF0F172A),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Condition Filter Chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val conditions = listOf("All" to "সব", "New" to "নতুন", "Used" to "ব্যবহৃত")
                        conditions.forEach { (key, label) ->
                            val isSelected = selectedCondition == key
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCondition = key },
                                label = { Text(label, fontSize = 14.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Shopping Cards Grid (2 items per row)
            val combinedProducts = firestoreProducts + dummyProducts
            val filteredProducts = combinedProducts.filter { product ->
                (selectedZilla == null || product.address.contains(selectedZilla!!, ignoreCase = true)) &&
                (searchQuery.isEmpty() || product.productName.contains(searchQuery, ignoreCase = true) || product.address.contains(searchQuery, ignoreCase = true)) &&
                (selectedCategory == null || product.productName.contains(selectedCategory!!, ignoreCase = true) || product.description.contains(selectedCategory!!, ignoreCase = true)) &&
                (selectedCondition == "All" || product.condition.equals(selectedCondition, ignoreCase = true) || (selectedCondition == "New" && product.condition.equals("নতুন", ignoreCase = true)) || (selectedCondition == "Used" && product.condition.equals("পুরাতন", ignoreCase = true))) &&
                (!showWishlistOnly || favoriteProductIds.contains(product.id))
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isBengali) "কোনো পণ্য পাওয়া যায়নি" else "No products found",
                            color = Color.Gray,
                            fontSize = 14.sp
                        )
                    }
                }
            } else if (isGridView) {
                filteredProducts.chunked(2).forEach { productPair ->
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 5.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            productPair.forEach { product ->
                                ShoppingGridCard(
                                    product = product,
                                    isBengali = isBengali,
                                    isFavorite = favoriteProductIds.contains(product.id),
                                    onFavoriteToggle = {
                                        favoriteProductIds = if (favoriteProductIds.contains(product.id)) {
                                            favoriteProductIds - product.id
                                        } else {
                                            favoriteProductIds + product.id
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    onNavigateToDetails = { onNavigateToDetails(product.id) }
                                )
                            }
                            if (productPair.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp)) {
                        ShoppingListCardItem(
                            product = product,
                            isBengali = isBengali,
                            isFavorite = favoriteProductIds.contains(product.id),
                            onFavoriteToggle = {
                                favoriteProductIds = if (favoriteProductIds.contains(product.id)) {
                                    favoriteProductIds - product.id
                                } else {
                                    favoriteProductIds + product.id
                                }
                            },
                            onNavigateToDetails = { onNavigateToDetails(product.id) }
                        )
                    }
                }
            }
        }
    }

    if (showCartDialog) {
        ShoppingCartDialog(
            cartItems = cartItems,
            onDismiss = { showCartDialog = false },
            onUpdateQty = { product, newQty ->
                cartItems = if (newQty <= 0) {
                    cartItems - product
                } else {
                    cartItems + (product to newQty)
                }
            },
            onCheckout = {
                cartItems = emptyMap()
                showCartDialog = false
                Toast.makeText(context, if (isBengali) "অর্ডার সফলভাবে প্লেস হয়েছে!" else "Order placed successfully!", Toast.LENGTH_LONG).show()
            }
        )
    }
}

@Composable
fun CategoryCardItem(
    category: ShoppingCategory,
    isBengali: Boolean,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(if (isSelected) Color(0xFF059669) else Color(0xFFE6F4EA)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.nameEng,
                tint = if (isSelected) Color.White else Color(0xFF059669),
                modifier = Modifier.size(36.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (isBengali) category.nameBan else category.nameEng,
            color = Color(0xFF1F2937),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun ShoppingGridCard(
    product: ProductInfo,
    isBengali: Boolean,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
    modifier: Modifier = Modifier,
    onNavigateToDetails: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNavigateToDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrls.first().toCoilModel(),
                        contentDescription = product.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = "SHEBA SHOPPING",
                    color = Color.Black.copy(alpha = 0.08f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Column(
                modifier = Modifier.padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        color = Color(0xFF059669),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onFavoriteToggle,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFE11D48) else Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = product.productName,
                    color = Color(0xFF0F172A),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${product.address} • ${product.condition}",
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShoppingListCardItem(
    product: ProductInfo,
    isBengali: Boolean,
    isFavorite: Boolean = false,
    onFavoriteToggle: () -> Unit = {},
    onNavigateToDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToDetails() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrls.isNotEmpty()) {
                    AsyncImage(
                        model = product.imageUrls.first().toCoilModel(),
                        contentDescription = product.productName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        color = Color(0xFF059669),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFE11D48) else Color.DarkGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.productName,
                    color = Color(0xFF0F172A),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${product.address} • ${product.condition}",
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun ShoppingCartDialog(
    cartItems: Map<ProductInfo, Int>,
    onDismiss: () -> Unit,
    onUpdateQty: (ProductInfo, Int) -> Unit,
    onCheckout: () -> Unit
) {
    val subtotal = cartItems.entries.sumOf { (product, qty) ->
        val digitsOnly = product.price.filter { it.isDigit() }
        val priceInt = digitsOnly.toIntOrNull() ?: 0
        priceInt * qty
    }
    val deliveryFee = if (cartItems.isEmpty()) 0 else 60
    val grandTotal = subtotal + deliveryFee

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color(0xFF059669))
                Spacer(modifier = Modifier.width(8.dp))
                Text("শপিং কার্ট (${cartItems.values.sum()} টি পণ্য)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF059669))
            }
        },
        text = {
            if (cartItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("আপনার কার্ট বর্তমানে খালি।", color = Color.Gray)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    cartItems.forEach { (product, qty) ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(product.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(product.price, fontSize = 14.sp, color = Color(0xFF059669), fontWeight = FontWeight.SemiBold)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { onUpdateQty(product, qty - 1) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                    }
                                    Text("$qty", fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 6.dp))
                                    IconButton(onClick = { onUpdateQty(product, qty + 1) }, modifier = Modifier.size(28.dp)) {
                                        Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color(0xFFCBD5E1))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("পণ্যের সাবটোটাল:", fontSize = 15.sp, color = Color.DarkGray)
                        Text("৳ $subtotal", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("ডেলিভারি চার্জ:", fontSize = 15.sp, color = Color.DarkGray)
                        Text("৳ $deliveryFee", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("সর্বমোট মূল্য:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                        Text("৳ $grandTotal", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF059669))
                    }
                }
            }
        },
        confirmButton = {
            if (cartItems.isNotEmpty()) {
                Button(
                    onClick = onCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669))
                ) {
                    Text("অর্ডার নিশ্চিত করুন", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন", color = Color.Gray)
            }
        }
    )
}
