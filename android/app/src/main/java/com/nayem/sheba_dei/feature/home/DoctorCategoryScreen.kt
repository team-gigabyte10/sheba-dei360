package com.nayem.sheba_dei.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nayem.sheba_dei.core.language.LocalAppLanguage
import android.app.Activity
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.nayem.sheba_dei.ui.components.GlobalAppBar
import com.nayem.sheba_dei.ui.components.SetStatusBarColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorCategoryScreen(onBack: () -> Unit, onCategoryClick: (String) -> Unit) {
    val languageState = LocalAppLanguage.current
    val isBengali = languageState.isBengali

    val view = LocalView.current
    SetStatusBarColor()

    val doctors = if (isBengali) listOf(
        Category("মেডিসিন", Icons.Default.Medication),
        Category("গাইনী", Icons.Default.PregnantWoman),
        Category("শিশু বিশেষজ্ঞ", Icons.Default.ChildCare),
        Category("চক্ষু বিশেষজ্ঞ", Icons.Default.Visibility),
        Category("নাক কান ও গলা", Icons.Default.Hearing),
        Category("ডেন্টিস্ট", Icons.Default.MedicalServices),
        Category("কার্ডিওলজি", Icons.Default.Favorite),
        Category("সার্জারি", Icons.Default.MedicalServices),
        Category("চর্ম ও যৌন রোগ", Icons.Default.Face),
        Category("ইউরোলজি", Icons.Default.WaterDrop),
        Category("হোমিওপ্যাথী", Icons.Default.Spa),
        Category("মনোরোগ বিশেষজ্ঞ", Icons.Default.Psychology),
        Category("নিউরোলজি", Icons.Default.Psychology),
        Category("গ্যাস্ট্রোএন্টারোলজি", Icons.Default.MonitorWeight),
        Category("নেফ্রোলজি", Icons.Default.WaterDrop),
        Category("অনকোলজি", Icons.Default.LocalHospital),
        Category("পালমোনোলজি", Icons.Default.Air),
        Category("রিউমাটোলজি", Icons.Default.Elderly),
        Category("এন্ডোক্রাইনোলজি", Icons.Default.Bloodtype),
        Category("হেমাটোলজি", Icons.Default.Bloodtype),
        Category("প্লাস্টিক সার্জারি", Icons.Default.Face),
        Category("নিউরোসার্জারি", Icons.Default.MedicalServices),
        Category("পেডিয়াট্রিক সার্জারি", Icons.Default.ChildCare),
        Category("ভাস্কুলার সার্জারি", Icons.Default.Bloodtype),
        Category("ফিজিওথেরাপি", Icons.Default.FitnessCenter),
        Category("ডায়াবেটোলজি", Icons.Default.Bloodtype),
        Category("পুষ্টিবিদ", Icons.Default.Restaurant),
        Category("রেডিওলজি", Icons.Default.Sensors),
        Category("অ্যানেস্থেসিওলজি", Icons.Default.MedicalServices),
        Category("প্যাথলজি", Icons.Default.Biotech),
        Category("আয়ুর্বেদিক", Icons.Default.Nature),
        Category("ইউনানি", Icons.Default.Spa),
        Category("লিভার স্পেশালিস্ট", Icons.Default.MedicalServices)
    ) else listOf(
        Category("Medicine", Icons.Default.Medication),
        Category("Gynecology", Icons.Default.PregnantWoman),
        Category("Pediatrician", Icons.Default.ChildCare),
        Category("Eye Specialist", Icons.Default.Visibility),
        Category("ENT", Icons.Default.Hearing),
        Category("Dentist", Icons.Default.MedicalServices),
        Category("Cardiology", Icons.Default.Favorite),
        Category("Surgery", Icons.Default.MedicalServices),
        Category("Skin & VD", Icons.Default.Face),
        Category("Urology", Icons.Default.WaterDrop),
        Category("Homeopathy", Icons.Default.Spa),
        Category("Psychiatrist", Icons.Default.Psychology),
        Category("Neurology", Icons.Default.Psychology),
        Category("Gastroenterology", Icons.Default.MonitorWeight),
        Category("Nephrology", Icons.Default.WaterDrop),
        Category("Oncology", Icons.Default.LocalHospital),
        Category("Pulmonology", Icons.Default.Air),
        Category("Rheumatology", Icons.Default.Elderly),
        Category("Endocrinology", Icons.Default.Bloodtype),
        Category("Hematology", Icons.Default.Bloodtype),
        Category("Plastic Surgery", Icons.Default.Face),
        Category("Neurosurgery", Icons.Default.MedicalServices),
        Category("Pediatric Surgery", Icons.Default.ChildCare),
        Category("Vascular Surgery", Icons.Default.Bloodtype),
        Category("Physiotherapy", Icons.Default.FitnessCenter),
        Category("Diabetology", Icons.Default.Bloodtype),
        Category("Nutritionist", Icons.Default.Restaurant),
        Category("Radiology", Icons.Default.Sensors),
        Category("Anesthesiology", Icons.Default.MedicalServices),
        Category("Pathology", Icons.Default.Biotech),
        Category("Ayurvedic", Icons.Default.Nature),
        Category("Unani", Icons.Default.Spa),
        Category("Hepatology", Icons.Default.MedicalServices)
    )

    var showFilterDialog by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }

    Scaffold(
        topBar = {
            GlobalAppBar(
                title = if (isBengali) "ডাক্তার" else "Doctors",
                onBackClick = onBack,

            )
        }
    ) { innerPadding ->
        if (showFilterDialog) {
            AlertDialog(
                onDismissRequest = { showFilterDialog = false },
                title = { Text(if (isBengali) "ফিল্টার করুন" else "Filter") },
                text = { Text(if (isBengali) "সারা বাংলাদেশের ডাক্তারদের ফিল্টার করার অপশন এখানে থাকবে।" else "Options to filter doctors all over Bangladesh will be here.") },
                confirmButton = {
                    TextButton(onClick = { showFilterDialog = false }) {
                        Text(if (isBengali) "ঠিক আছে" else "OK")
                    }
                }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            
            // Banner Ads
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2563EB)) // Blue Banner
                    .padding(16.dp)
            ) {
                // Decorative circles
                Box(modifier = Modifier.size(80.dp).offset(x = 180.dp, y = (-20).dp).clip(CircleShape).background(Color(0xFFFFC107)))
                Box(modifier = Modifier.size(60.dp).offset(x = 160.dp, y = 80.dp).clip(CircleShape).background(Color(0xFF10B981)))

                Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                    Text(if (isBengali) "স্বাস্থ্য রক্ষায় আমরা আছি পাশে" else "We are here for your health", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text(if (isBengali) "অভিজ্ঞ ডাক্তারদের পরামর্শ নিন" else "Consult experienced doctors", color = Color(0xFFFFC107), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .clickable { }
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(if (isBengali) "বিস্তারিত" else "Details", color = Color(0xFF2563EB), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Green instruction banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(5.dp)) // circular padding
                    .background(Color(0xFF064E3B)) // Dark Green
                    .padding(horizontal = 5.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isBengali) "এক নজরে দেখে নিন কোন রোগ হলে কোন ডাক্তার দেখাবেন?" else "At a glance: Which doctor to see for which disease?",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            // Categories Grid
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                doctors.chunked(4).forEach { rowDoctors ->
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowDoctors.forEach { doctorCategory ->
                                CategoryItem(
                                    category = doctorCategory,
                                    modifier = Modifier.weight(1f),
                                    onClick = { onCategoryClick(doctorCategory.name) }
                                )
                            }
                            repeat(4 - rowDoctors.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
