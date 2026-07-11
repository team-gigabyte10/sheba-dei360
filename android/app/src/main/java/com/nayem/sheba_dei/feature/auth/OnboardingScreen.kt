package com.nayem.sheba_dei.feature.auth

import com.nayem.sheba_dei.core.language.AppLanguage
import com.nayem.sheba_dei.core.language.LocalAppLanguage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.R
import kotlinx.coroutines.launch



data class OnboardingPage(
    val titleBn: String,
    val titleEn: String,
    val descBn: String,
    val descEn: String,
    val imageRes: Int
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit
) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    val appName = if (isBengali) "বরিশাল সিটি সার্ভিস" else "Barisal City Service"
    val buttonTextStart = if (isBengali) "শুরু করুন" else "Get Started"
    val buttonTextNext = if (isBengali) "পরবর্তী" else "Next"

    val pages = listOf(
        OnboardingPage(
            titleBn = "'সবকিছু আপনার প্রয়োজন, এক অ্যাপেই'",
            titleEn = "'Everything you need, in one app'",
            descBn = "ডাক্তার, ইলেকট্রিশিয়ান, ড্রাইভার বা অন্য যেকোনো বিশ্বস্ত পরিসেবা পান আপনার হাতের মুঠোয়। বরিশাল সিটি সার্ভিস এর সাথে জীবন সহজ করুন।",
            descEn = "Get doctors, electricians, drivers or any other trusted service at your fingertips. Make life easier with Barisal City Service.",
            imageRes = R.drawable.open
        ),
        OnboardingPage(
            titleBn = "'বিশ্বস্ত পেশাদারদের খুঁজুন'",
            titleEn = "'Find Trusted Professionals'",
            descBn = "আমাদের যাচাইকৃত পেশাদারদের সাহায্যে আপনার কাজ দ্রুত এবং সহজে সম্পন্ন করুন।",
            descEn = "Get your work done quickly and easily with the help of our verified professionals.",
            imageRes = R.drawable.open1
        ),
        OnboardingPage(
            titleBn = "'নির্ভরযোগ্য সেবা'",
            titleEn = "'Reliable Service'",
            descBn = "আপনার দোরগোড়ায় সর্বোত্তম সেবা নিশ্চিত করতে আমরা সর্বদা প্রস্তুত।",
            descEn = "We are always ready to ensure the best service at your doorstep.",
            imageRes = R.drawable.open2
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Language Switcher Overlay
            Row(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 16.dp, end = 16.dp)
                    .align(Alignment.TopEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Gray.copy(alpha = 0.3f))
                    .clickable {
                        languageState.currentLanguage = if (isBengali) AppLanguage.ENGLISH else AppLanguage.BENGALI
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = "Change Language",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isBengali) "বাং" else "EN",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { pageIndex ->
                val page = pages[pageIndex]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth().fillMaxHeight()
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = "Onboarding Illustration",
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.6f),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    Text(
                        text = if (isBengali) page.titleBn else page.titleEn,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2563EB), // Primary Blue
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isBengali) page.descBn else page.descEn,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF475569), // Slate 600
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2563EB) // Blue 600
                    )
                ) {
                    Text(
                        text = if (pagerState.currentPage == pages.size - 1) buttonTextStart else buttonTextNext,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Page indicators
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(pages.size) { iteration ->
                        val color = if (pagerState.currentPage == iteration) Color.Black else Color.LightGray
                        val size = if (pagerState.currentPage == iteration) 8.dp else 6.dp
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(size)
                                .clip(CircleShape)
                                .background(color)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
            }
            }
        }
    }
}
