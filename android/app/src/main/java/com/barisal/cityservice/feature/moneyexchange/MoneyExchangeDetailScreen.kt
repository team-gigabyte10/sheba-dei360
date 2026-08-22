package com.barisal.cityservice.feature.moneyexchange

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.barisal.cityservice.data.model.MoneyExchangeDto
import com.barisal.cityservice.data.repository.MoneyExchangeRepository
import com.barisal.cityservice.ui.components.GlobalAppBar
import com.barisal.cityservice.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoneyExchangeDetailScreen(
    id: String,
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val isBengali = LocalAppLanguage.current.isBengali
    val repo = remember { MoneyExchangeRepository() }

    var item by remember { mutableStateOf<MoneyExchangeDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    val themeColor = Color(0xFF0F766E)

    LaunchedEffect(id) {
        item = repo.getMoneyExchangePostById(id)
        isLoading = false
    }

    SetStatusBarColor()

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "মানি এক্সচেঞ্জ বিস্তারিত" else "Money Exchange Details",
                onBackClick = onBack
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = themeColor)
            }
        } else if (item == null) {
            Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                Text(if (isBengali) "তথ্য পাওয়া যায়নি" else "Details not found", color = Color.Gray)
            }
        } else {
            val dto = item!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Profile Card
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val coilModel = remember(dto.image) { dto.image.toCoilModel() }
                        if (coilModel != null) {
                            AsyncImage(
                                model = coilModel,
                                contentDescription = "Photo",
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, themeColor, CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(themeColor.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CurrencyExchange,
                                    contentDescription = null,
                                    tint = themeColor,
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = dto.agencyName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color(0xFF0F172A)
                        )

                        Surface(
                            color = themeColor.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text(
                                text = dto.subCategory,
                                color = themeColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // Information Card
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
                            text = if (isBengali) "কাউন্টার সংক্রান্ত তথ্য" else "Exchange Counter Information",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0F172A)
                        )

                        HorizontalDivider(color = Color(0xFFF1F5F9))

                        if (dto.availableCurrencies.isNotBlank()) {
                            DetailRow(label = if (isBengali) "লেনদেনকৃত মুদ্রা" else "Currencies", value = dto.availableCurrencies)
                        }

                        if (dto.address.isNotBlank()) {
                            DetailRow(label = if (isBengali) "অফিসের ঠিকানা" else "Office Address", value = dto.address)
                        }

                        if (dto.licenseNo.isNotBlank()) {
                            DetailRow(label = if (isBengali) "লাইসেন্স নং" else "License No", value = dto.licenseNo)
                        }

                        if (dto.proprietorOrManager.isNotBlank()) {
                            DetailRow(label = if (isBengali) "পরিচালক/প্রোপাইটর" else "Proprietor/Manager", value = dto.proprietorOrManager)
                        }

                        if (dto.location.isNotBlank()) {
                            DetailRow(label = if (isBengali) "এলাকা/জেলা" else "Location", value = dto.location)
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
                            text = if (isBengali) "যোগাযোগ করুন" else "Contact Counter",
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
                                colors = ButtonDefaults.buttonColors(containerColor = themeColor),
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
