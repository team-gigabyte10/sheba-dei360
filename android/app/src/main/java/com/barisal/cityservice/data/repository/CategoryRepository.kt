package com.barisal.cityservice.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.barisal.cityservice.data.model.CategoryDto
import com.barisal.cityservice.data.model.CategoryItem
import com.barisal.cityservice.data.model.SubCategoryItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

const val CITY_SERVICE_DRIVE_FOLDER_ID = "1OzKqnb1pfW1-nbt2HQQSO6hlnj2oUy3B"

/**
 * CategoryRepository manages static Categories and Sub-Categories in-memory.
 * Categories and Sub-Categories are fixed static system assets and are not read from or saved to Firestore.
 */
class CategoryRepository {

    /**
     * Resizes, compresses and converts the selected category icon image to a Base64 data URI string.
     */
    suspend fun uploadCategoryIconToDrive(context: Context, uri: Uri): Result<String> = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open image file stream"))
            val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                return@withContext Result.failure(Exception("Failed to decode bitmap"))
            }

            val maxDimension = 256
            val width = originalBitmap.width
            val height = originalBitmap.height
            val (newWidth, newHeight) = if (width > height) {
                if (width > maxDimension) {
                    val ratio = height.toFloat() / width.toFloat()
                    Pair(maxDimension, (maxDimension * ratio).toInt())
                } else {
                    Pair(width, height)
                }
            } else {
                if (height > maxDimension) {
                    val ratio = width.toFloat() / height.toFloat()
                    Pair((maxDimension * ratio).toInt(), maxDimension)
                } else {
                    Pair(width, height)
                }
            }

            val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            if (scaledBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 80, outputStream)
            val bytes = outputStream.toByteArray()
            scaledBitmap.recycle()

            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64String"
            Result.success(dataUri)
        } catch (e: Exception) {
            android.util.Log.e("CategoryRepo", "Base64 conversion failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Gets next category ID for static preset indexing.
     */
    suspend fun getNextCategoryId(): Int {
        return getDefaultCategories().maxOfOrNull { it.id }?.plus(1) ?: 14
    }

    /**
     * Stub method since Category and Sub-Category are static and not saved to Firestore.
     */
    suspend fun saveCategoryToFirestore(category: CategoryDto): Result<Boolean> {
        return Result.success(true)
    }

    /**
     * Stub method since Category and Sub-Category are static and not saved to Firestore.
     */
    suspend fun saveSubCategoryToFirestore(subCategory: com.barisal.cityservice.data.model.SubCategoryDto): Result<Boolean> {
        return Result.success(true)
    }

    /**
     * Stub method since Category and Sub-Category are static and not saved to Firestore.
     */
    suspend fun deleteSubCategoryFromFirestore(subCategoryId: Int): Result<Boolean> {
        return Result.success(true)
    }

    /**
     * Stub method since Category and Sub-Category are static and not saved to Firestore.
     */
    suspend fun deleteCategoryFromFirestore(categoryId: Int): Result<Boolean> {
        return Result.success(true)
    }

    /**
     * Returns static sub-categories filtered by categoryId.
     */
    fun getSubCategoriesFlow(categoryId: Int): Flow<List<SubCategoryItem>> {
        val filtered = getDefaultSubCategories().filter { it.categoryId == categoryId }
        return flowOf(filtered)
    }

    /**
     * Returns all static sub-categories across categories.
     */
    fun getAllSubCategoriesFlow(): Flow<List<SubCategoryItem>> {
        return flowOf(getDefaultSubCategories())
    }

    /**
     * Returns all static categories.
     */
    fun getCategoriesFlow(): Flow<List<CategoryItem>> {
        return flowOf(getDefaultCategories())
    }

    /**
     * Helper to match fallback Material Icon based on category route key.
     */
    fun matchFallbackIcon(route: String): androidx.compose.ui.graphics.vector.ImageVector {
        return when (route.lowercase()) {
            "health" -> Icons.Default.MedicalServices
            "transport" -> Icons.Default.Commute
            "houserent" -> Icons.Default.House
            "shopping" -> Icons.Default.ShoppingCart
            "matrimony" -> Icons.Default.People
            "event" -> Icons.Default.Event
            "ride" -> Icons.Default.TwoWheeler
            "courier" -> Icons.Default.LocalShipping
            "mistri" -> Icons.Default.Construction
            "emergency" -> Icons.Default.Emergency
            "tutor" -> Icons.Default.School
            "flatland" -> Icons.Default.Landscape
            "categorymap" -> Icons.Default.LocationOn
            else -> Icons.Default.Category
        }
    }

    /**
     * Default core categories list (Static, non-Firestore).
     */
    fun getDefaultCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(1, "Health Services", "স্বাস্থ্য সেবা", fallbackIcon = Icons.Default.MedicalServices, route = "health"),
            CategoryItem(2, "Transport Services", "যাতায়াত সেবা", fallbackIcon = Icons.Default.Commute, route = "transport"),
            CategoryItem(3, "House Rent", "বাসা ভাড়া", fallbackIcon = Icons.Default.House, route = "houserent"),
            CategoryItem(4, "Shopping", "বেচা-কেনা", fallbackIcon = Icons.Default.ShoppingCart, route = "shopping"),
            CategoryItem(5, "Matrimony", "পাত্র-পাত্রী", fallbackIcon = Icons.Default.People, route = "matrimony"),
            CategoryItem(6, "Event Service", "ইভেন্ট সার্ভিস", fallbackIcon = Icons.Default.Event, route = "event"),
            CategoryItem(7, "Ride", "রাইড", fallbackIcon = Icons.Default.TwoWheeler, route = "ride"),
            CategoryItem(8, "Courier", "কুরিয়ার", fallbackIcon = Icons.Default.LocalShipping, route = "courier"),
            CategoryItem(9, "Mistri", "মিস্ত্রি", fallbackIcon = Icons.Default.Construction, route = "mistri"),
            CategoryItem(10, "Emergency Service", "জরুরী সেবা", fallbackIcon = Icons.Default.Emergency, route = "emergency"),
            CategoryItem(11, "Tutor", "টিউটর", fallbackIcon = Icons.Default.School, route = "tutor"),
            CategoryItem(12, "Flat and Land", "ফ্ল্যাট ও জমি", fallbackIcon = Icons.Default.Landscape, route = "flatland"),
            CategoryItem(13, "Location Based Services", "লোকেশন ভিত্তিক সেবা", fallbackIcon = Icons.Default.LocationOn, route = "categorymap")
        )
    }

    /**
     * Default sub-categories list for all 13 core categories (Static, non-Firestore).
     */
    fun getDefaultSubCategories(): List<SubCategoryItem> {
        return listOf(
            // Category 1: Health Services
            SubCategoryItem(101, 1, "Hospitals & Clinics", "হাসপাতাল ও ক্লিনিক", fallbackIcon = Icons.Default.LocalHospital, route = "hospital"),
            SubCategoryItem(102, 1, "Doctors List", "ডাক্তার তালিকা", fallbackIcon = Icons.Default.MedicalServices, route = "doctor"),
            SubCategoryItem(103, 1, "Ambulance", "অ্যাম্বুলেন্স সেবা", fallbackIcon = Icons.Default.Emergency, route = "ambulance"),
            SubCategoryItem(104, 1, "Diagnostic Center", "ডায়াগনস্টিক সেন্টার", fallbackIcon = Icons.Default.Biotech, route = "diagnostic"),
            SubCategoryItem(105, 1, "Blood Donor", "রক্তদাতা", fallbackIcon = Icons.Default.Bloodtype, route = "blood"),
            SubCategoryItem(106, 1, "Pharmacy", "ফার্মেসী ও ওষুধ", fallbackIcon = Icons.Default.Medication, route = "pharmacy"),

            // Category 2: Transport Services
            SubCategoryItem(201, 2, "Bus Ticket", "বাস টিকিট", fallbackIcon = Icons.Default.DirectionsBus, route = "bus"),
            SubCategoryItem(202, 2, "Train Ticket", "ট্রেন টিকিট", fallbackIcon = Icons.Default.Train, route = "train"),
            SubCategoryItem(203, 2, "Launch Ticket", "লঞ্চ টিকিট", fallbackIcon = Icons.Default.DirectionsBoat, route = "launch"),
            SubCategoryItem(204, 2, "Air Ticket", "এয়ার টিকিট", fallbackIcon = Icons.Default.Flight, route = "flight"),
            SubCategoryItem(205, 2, "Rent a Car", "গাড়ি ভাড়া", fallbackIcon = Icons.Default.DirectionsCar, route = "rentcar"),

            // Category 3: House Rent
            SubCategoryItem(301, 3, "Family House", "ফ্যামিলি বাসা", fallbackIcon = Icons.Default.House, route = "family_house"),
            SubCategoryItem(302, 3, "Bachelor Room", "ব্যাচেলর রুম", fallbackIcon = Icons.Default.Hotel, route = "bachelor_room"),
            SubCategoryItem(303, 3, "Sublet", "সাবলেট বাসা", fallbackIcon = Icons.Default.MeetingRoom, route = "sublet"),
            SubCategoryItem(304, 3, "Hostel & Mess", "হোস্টেল ও মেস", fallbackIcon = Icons.Default.Hotel, route = "hostel"),
            SubCategoryItem(305, 3, "Office & Shop Rent", "অফিস ও দোকান ভাড়া", fallbackIcon = Icons.Default.Store, route = "office_rent"),

            // Category 4: Shopping
            SubCategoryItem(401, 4, "Mobiles & Electronics", "মোবাইল ও ইলেকট্রনিক্স", fallbackIcon = Icons.Default.Smartphone, route = "electronics"),
            SubCategoryItem(402, 4, "Home & Living", "হোম ও লিভিং", fallbackIcon = Icons.Default.Chair, route = "home_living"),
            SubCategoryItem(403, 4, "Vehicles & Property", "গাড়ি ও প্রপার্টি", fallbackIcon = Icons.Default.DirectionsCar, route = "vehicles"),
            SubCategoryItem(404, 4, "Fashion & Lifestyle", "ফ্যাশন ও লাইফস্টাইল", fallbackIcon = Icons.Default.Checkroom, route = "fashion"),
            SubCategoryItem(405, 4, "Food & Farming", "খাবার ও কৃষি", fallbackIcon = Icons.Default.Agriculture, route = "grocery"),

            // Category 5: Matrimony
            SubCategoryItem(501, 5, "Groom Profiles", "পাত্রের বায়োডাটা", fallbackIcon = Icons.Default.Person, route = "groom"),
            SubCategoryItem(502, 5, "Bride Profiles", "পাত্রীর বায়োডাটা", fallbackIcon = Icons.Default.Face, route = "bride"),
            SubCategoryItem(503, 5, "Matchmakers", "ঘটক সেবা", fallbackIcon = Icons.Default.People, route = "ghatak"),

            // Category 6: Event Service
            SubCategoryItem(601, 6, "Convention Halls", "কম্যুনিটি সেন্টার", fallbackIcon = Icons.Default.Apartment, route = "hall"),
            SubCategoryItem(602, 6, "Decorators", "ডেকোরেটর", fallbackIcon = Icons.Default.Celebration, route = "decorator"),
            SubCategoryItem(603, 6, "Catering Service", "ক্যাটারিং সার্ভিস", fallbackIcon = Icons.Default.Restaurant, route = "catering"),
            SubCategoryItem(604, 6, "Photography & Video", "ফটোগ্রাফি ও ভিডিও", fallbackIcon = Icons.Default.CameraAlt, route = "photography"),
            SubCategoryItem(605, 6, "Sound & Lighting", "সাউন্ড ও লাইটিং", fallbackIcon = Icons.Default.VolumeUp, route = "sound"),

            // Category 7: Ride
            SubCategoryItem(701, 7, "Bike Ride", "বাইক রাইড", fallbackIcon = Icons.Default.TwoWheeler, route = "bike"),
            SubCategoryItem(702, 7, "Car Ride", "কার রাইড", fallbackIcon = Icons.Default.DirectionsCar, route = "car"),
            SubCategoryItem(703, 7, "Auto & CNG", "অটো ও সিএনজি", fallbackIcon = Icons.Default.TwoWheeler, route = "cng"),

            // Category 8: Courier
            SubCategoryItem(801, 8, "Parcel Delivery", "পার্সেল ডেলিভারি", fallbackIcon = Icons.Default.LocalShipping, route = "parcel"),
            SubCategoryItem(802, 8, "Document Express", "কাগজপত্র ও ডকুমেন্টস", fallbackIcon = Icons.Default.Description, route = "document"),
            SubCategoryItem(803, 8, "House Shifting", "বাসা বদল / শিফটিং", fallbackIcon = Icons.Default.LocalShipping, route = "shifting"),

            // Category 9: Mistri
            SubCategoryItem(901, 9, "Electrician", "ইলেকট্রিশিয়ান", fallbackIcon = Icons.Default.ElectricBolt, route = "electrician"),
            SubCategoryItem(902, 9, "Plumber & Sanitary", "প্লাম্বার ও স্যানিটারি", fallbackIcon = Icons.Default.Build, route = "plumber"),
            SubCategoryItem(903, 9, "AC Repair", "এসি মেরামত", fallbackIcon = Icons.Default.Build, route = "ac_repair"),
            SubCategoryItem(904, 9, "Carpenter", "কাঠ মিস্ত্রি", fallbackIcon = Icons.Default.Construction, route = "carpenter"),
            SubCategoryItem(905, 9, "Painter", "রং মিস্ত্রি", fallbackIcon = Icons.Default.Brush, route = "painter"),
            SubCategoryItem(906, 9, "Mason (Rajmistri)", "রাজমিস্ত্রি", fallbackIcon = Icons.Default.Build, route = "mason"),

            // Category 10: Emergency Service
            SubCategoryItem(1001, 10, "Police Stations", "পুলিশ স্টেশন", fallbackIcon = Icons.Default.LocalPolice, route = "police"),
            SubCategoryItem(1002, 10, "Fire Service", "ফায়ার সার্ভিস", fallbackIcon = Icons.Default.LocalFireDepartment, route = "fire"),
            SubCategoryItem(1003, 10, "Electricity Complaint", "বিদ্যুৎ অভিযোগ", fallbackIcon = Icons.Default.ElectricBolt, route = "electricity"),
            SubCategoryItem(1004, 10, "Gas Line Service", "গ্যাস লাইন সেবা", fallbackIcon = Icons.Default.LocalGasStation, route = "gas"),

            // Category 11: Tutor
            SubCategoryItem(1101, 11, "Academic Tutors", "স্কুল ও কলেজ টিউটর", fallbackIcon = Icons.Default.School, route = "school_tutor"),
            SubCategoryItem(1102, 11, "Admission Prep", "ভর্তি পরীক্ষা প্রস্তুতি", fallbackIcon = Icons.Default.MenuBook, route = "admission_tutor"),
            SubCategoryItem(1103, 11, "Quran Teacher", "কুরআন ও আরবি শিক্ষক", fallbackIcon = Icons.Default.MenuBook, route = "quran_tutor"),
            SubCategoryItem(1104, 11, "Computer & IT", "কম্পিউটার ও আইটি শিক্ষা", fallbackIcon = Icons.Default.Computer, route = "it_tutor"),

            // Category 12: Flat and Land
            SubCategoryItem(1201, 12, "Flats for Sale", "ফ্ল্যাট বিক্রি", fallbackIcon = Icons.Default.Apartment, route = "flat_sale"),
            SubCategoryItem(1202, 12, "Land & Plots for Sale", "জমি ও প্লট বিক্রি", fallbackIcon = Icons.Default.Landscape, route = "land_sale"),
            SubCategoryItem(1203, 12, "Commercial Property", "বাণিজ্যিক স্পেস", fallbackIcon = Icons.Default.Business, route = "commercial_property"),

            // Category 13: Location Based Services
            SubCategoryItem(1301, 13, "Nearby Hospitals", "কাছাকাছি হাসপাতাল", fallbackIcon = Icons.Default.LocalHospital, route = "map_hospital"),
            SubCategoryItem(1302, 13, "Nearby Police Stations", "কাছাকাছি পুলিশ স্টেশন", fallbackIcon = Icons.Default.LocalPolice, route = "map_police"),
            SubCategoryItem(1303, 13, "Nearby Banks & ATMs", "কাছাকাছি ব্যাংক ও এটিএম", fallbackIcon = Icons.Default.AccountBalance, route = "map_bank"),
            SubCategoryItem(1304, 13, "Nearby Restaurants", "কাছাকাছি রেস্তোরাঁ", fallbackIcon = Icons.Default.Restaurant, route = "map_restaurant")
        )
    }
}
