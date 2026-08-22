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
            "hotel" -> Icons.Default.Hotel
            "restaurant" -> Icons.Default.Restaurant
            "training_academy" -> Icons.Default.School
            "job_screen" -> Icons.Default.Work
            "domestic_help_screen" -> Icons.Default.CleaningServices
            "legal_service_screen" -> Icons.Default.Gavel
            "deed_amin_screen" -> Icons.Default.Assignment
            "hajj_umrah_screen" -> Icons.Default.Mosque
            "tour_travels_screen" -> Icons.Default.FlightTakeoff
            "money_exchange_screen" -> Icons.Default.CurrencyExchange
            "missing_found_screen" -> Icons.Default.Search
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
            CategoryItem(7, "Rent a Car", "গাড়ি ভাড়া", fallbackIcon = Icons.Default.DirectionsCar, route = "ride"),
            CategoryItem(8, "Courier", "কুরিয়ার", fallbackIcon = Icons.Default.LocalShipping, route = "courier"),
            CategoryItem(9, "Mistri", "মিস্ত্রি", fallbackIcon = Icons.Default.Construction, route = "mistri"),
            CategoryItem(10, "Emergency Service", "জরুরী সেবা", fallbackIcon = Icons.Default.Emergency, route = "emergency"),
            CategoryItem(11, "Tutor", "টিউটর", fallbackIcon = Icons.Default.School, route = "tutor"),
            CategoryItem(12, "Flat and Land", "ফ্ল্যাট ও জমি", fallbackIcon = Icons.Default.Landscape, route = "flatland"),
            CategoryItem(13, "Location Based Services", "লোকেশন ভিত্তিক সেবা", fallbackIcon = Icons.Default.LocationOn, route = "categorymap"),
            CategoryItem(14, "Hotel", "হোটেল", fallbackIcon = Icons.Default.Hotel, route = "hotel"),
            CategoryItem(15, "Restaurant", "রেস্টুরেন্ট", fallbackIcon = Icons.Default.Restaurant, route = "restaurant"),
            CategoryItem(16, "Training Academy", "ট্রেনিং একাডেমি", fallbackIcon = Icons.Default.School, route = "training_academy"),
            CategoryItem(17, "Jobs Circular", "চাকরি ও নিয়োগ", fallbackIcon = Icons.Default.Work, route = "job_screen"),
            CategoryItem(18, "Domestic Help / Maid", "গৃহকর্মী ও বুয়া", fallbackIcon = Icons.Default.CleaningServices, route = "domestic_help_screen"),
            CategoryItem(19, "Legal Services", "আইনি সেবা", fallbackIcon = Icons.Default.Gavel, route = "legal_service_screen"),
            CategoryItem(20, "Deed Writer & Surveyor", "দলিল লেখক/আমিন", fallbackIcon = Icons.Default.Assignment, route = "deed_amin_screen"),
            CategoryItem(21, "Hajj & Umrah Services", "হজ ও উমরাহ সেবা", fallbackIcon = Icons.Default.Mosque, route = "hajj_umrah_screen"),
            CategoryItem(22, "Tour & Travels", "ট্যুর ও ট্রাভেলস", fallbackIcon = Icons.Default.FlightTakeoff, route = "tour_travels_screen"),
            CategoryItem(23, "Money Exchange", "মানি এক্সচেঞ্জ", fallbackIcon = Icons.Default.CurrencyExchange, route = "money_exchange_screen"),
            CategoryItem(24, "Missing & Found", "নিখোজ বিজ্ঞপ্তি", fallbackIcon = Icons.Default.Search, route = "missing_found_screen")
        )
    }

    /**
     * Default sub-categories list for all 16 core categories (Static, non-Firestore).
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

            // Category 7: Rent a Car
            SubCategoryItem(701, 7, "Private Car", "প্রাইভেট কার", fallbackIcon = Icons.Default.DirectionsCar, route = "private_car"),
            SubCategoryItem(702, 7, "Microbus", "মাইক্রোবাস", fallbackIcon = Icons.Default.AirportShuttle, route = "microbus"),
            SubCategoryItem(703, 7, "Pickup", "পিকআপ", fallbackIcon = Icons.Default.LocalShipping, route = "pickup"),
            SubCategoryItem(704, 7, "Truck", "ট্রাক", fallbackIcon = Icons.Default.LocalShipping, route = "truck"),
            SubCategoryItem(705, 7, "Ride Sharing", "রাইড শেয়ারিং", fallbackIcon = Icons.Default.TwoWheeler, route = "ride_sharing"),
            SubCategoryItem(706, 7, "Van Vara", "ভ্যান ভাড়া", fallbackIcon = Icons.Default.Commute, route = "van_vara"),
            SubCategoryItem(707, 7, "Auto Vara", "অটো ভাড়া", fallbackIcon = Icons.Default.ElectricRickshaw, route = "auto_vara"),

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
            SubCategoryItem(1304, 13, "Nearby Restaurants", "কাছাকাছি রেস্তোরাঁ", fallbackIcon = Icons.Default.Restaurant, route = "map_restaurant"),

            // Category 14: Hotel
            SubCategoryItem(1401, 14, "Residential Hotel", "আবাসিক হোটেল", fallbackIcon = Icons.Default.Hotel, route = "res_hotel"),
            SubCategoryItem(1402, 14, "Resort & Cottage", "রিসোর্ট ও কটেজ", fallbackIcon = Icons.Default.Villa, route = "resort"),
            SubCategoryItem(1403, 14, "Rest House", "রেস্ট হাউস", fallbackIcon = Icons.Default.HomeWork, route = "rest_house"),

            // Category 15: Restaurant
            SubCategoryItem(1501, 15, "Traditional Food", "বাংলা খাবার / বিরিয়ানি", fallbackIcon = Icons.Default.RestaurantMenu, route = "bengali_food"),
            SubCategoryItem(1502, 15, "Fast Food & Cafe", "ফাস্ট ফুড ও ক্যাফে", fallbackIcon = Icons.Default.Fastfood, route = "fast_food"),
            SubCategoryItem(1503, 15, "Chinese & Thai", "চাইনিজ ও থাই", fallbackIcon = Icons.Default.RamenDining, route = "chinese_food"),
            SubCategoryItem(1504, 15, "Bakery & Sweets", "বেকারি ও মিষ্টি", fallbackIcon = Icons.Default.Cake, route = "bakery"),

            // Category 16: Training Academy
            SubCategoryItem(1601, 16, "Car Driving Training", "কার ড্রাইভিং ট্রেনিং", fallbackIcon = Icons.Default.DirectionsCar, route = "car_driving"),
            SubCategoryItem(1602, 16, "Computer Training", "কম্পিউটার ট্রেনিং", fallbackIcon = Icons.Default.Computer, route = "computer_training"),
            SubCategoryItem(1603, 16, "Technical Training", "টেকনিক্যাল ট্রেনিং", fallbackIcon = Icons.Default.Build, route = "technical_training"),
            SubCategoryItem(1604, 16, "Language Learning", "ভাষা শিক্ষা", fallbackIcon = Icons.Default.Translate, route = "language_learning"),
            SubCategoryItem(1605, 16, "Job & Career", "চাকরি ও ক্যারিয়ার", fallbackIcon = Icons.Default.Work, route = "job_career"),
            SubCategoryItem(1606, 16, "Others", "অন্যান্য", fallbackIcon = Icons.Default.MoreHoriz, route = "others"),

            // Category 17: Jobs Circular (Chakri)
            SubCategoryItem(1701, 17, "Protisthan", "প্রতিষ্ঠানে চাকরি", fallbackIcon = Icons.Default.Business, route = "protisthan"),
            SubCategoryItem(1702, 17, "Shoroom", "শো-রুমে চাকরি", fallbackIcon = Icons.Default.Storefront, route = "shoroom"),
            SubCategoryItem(1703, 17, "Dokan", "দোকানে চাকরি", fallbackIcon = Icons.Default.Store, route = "dokan"),
            SubCategoryItem(1704, 17, "Others", "অন্যান্য চাকরি", fallbackIcon = Icons.Default.Work, route = "job_others"),

            // Category 18: Domestic Help / Maid (Grihokormi / Buya)
            SubCategoryItem(1801, 18, "Full-Time Maid", "ফুল-টাইম গৃহকর্মী", fallbackIcon = Icons.Default.CleaningServices, route = "full_time_maid"),
            SubCategoryItem(1802, 18, "Part-Time Maid", "পার্ট-টাইম গৃহকর্মী", fallbackIcon = Icons.Default.CleaningServices, route = "part_time_maid"),
            SubCategoryItem(1803, 18, "Cook / Chef", "রান্নার বুয়া", fallbackIcon = Icons.Default.SoupKitchen, route = "cook_chef"),
            SubCategoryItem(1804, 18, "Baby Sitter", "শিশু দেখাশোনা", fallbackIcon = Icons.Default.ChildCare, route = "baby_sitter"),
            SubCategoryItem(1805, 18, "Elderly Care", "বয়স্ক সেবা", fallbackIcon = Icons.Default.Elderly, route = "elderly_care"),
            SubCategoryItem(1806, 18, "House Cleaner", "বাসা পরিষ্কার", fallbackIcon = Icons.Default.CleaningServices, route = "house_cleaner"),
            SubCategoryItem(1807, 18, "Others", "অন্যান্য", fallbackIcon = Icons.Default.MoreHoriz, route = "help_others"),

            // Category 19: Legal Services
            SubCategoryItem(1901, 19, "Advocate & Lawyer", "অ্যাডভোকেট ও আইনজীবী", fallbackIcon = Icons.Default.Gavel, route = "lawyer"),
            SubCategoryItem(1902, 19, "Civil Cases", "দেওয়ানী মামলা", fallbackIcon = Icons.Default.Gavel, route = "civil_lawyer"),
            SubCategoryItem(1903, 19, "Criminal Cases", "ফৌজদারী মামলা", fallbackIcon = Icons.Default.Gavel, route = "criminal_lawyer"),
            SubCategoryItem(1904, 19, "Tax & VAT", "ইনকাম ট্যাক্স ও ভ্যাট", fallbackIcon = Icons.Default.Calculate, route = "tax_lawyer"),
            SubCategoryItem(1905, 19, "Notary Public", "নোটারী পাবলিক", fallbackIcon = Icons.Default.Verified, route = "notary"),

            // Category 20: Deed Writer & Land Surveyor (Dolil Lekhok / Amin)
            SubCategoryItem(2001, 20, "Deed Writer", "দলিল লেখক", fallbackIcon = Icons.Default.Description, route = "deed_writer"),
            SubCategoryItem(2002, 20, "Land Surveyor (Amin)", "আমিন / জমি পরিমাপক", fallbackIcon = Icons.Default.SquareFoot, route = "land_surveyor"),
            SubCategoryItem(2003, 20, "Land Registry Consultant", "জমি রেজিস্ট্রেশন কনসালট্যান্ট", fallbackIcon = Icons.Default.Assignment, route = "registry_consultant"),

            // Category 21: Hajj & Umrah Services
            SubCategoryItem(2101, 21, "Hajj Package", "হজ প্যাকেজ", fallbackIcon = Icons.Default.Mosque, route = "hajj_package"),
            SubCategoryItem(2102, 21, "Umrah Package", "উমরাহ প্যাকেজ", fallbackIcon = Icons.Default.Mosque, route = "umrah_package"),
            SubCategoryItem(2103, 21, "Saudi Visa & Medical", "সৌদি ভিসা ও মেডিকেল", fallbackIcon = Icons.Default.CardTravel, route = "saudi_visa"),
            SubCategoryItem(2104, 21, "Hotel & Transport", "মাক্কাহ-মদিনা হোটেল ও পরিবহন", fallbackIcon = Icons.Default.Hotel, route = "hajj_hotel"),
            SubCategoryItem(2105, 21, "Others", "অন্যান্য", fallbackIcon = Icons.Default.MoreHoriz, route = "hajj_others"),

            // Category 22: Tour & Travels
            SubCategoryItem(2201, 22, "Air Ticket", "বিমান টিকিট", fallbackIcon = Icons.Default.Flight, route = "air_ticket"),
            SubCategoryItem(2202, 22, "Domestic Tour", "দেশীয় ট্যুর প্যাকেজ", fallbackIcon = Icons.Default.Explore, route = "domestic_tour"),
            SubCategoryItem(2203, 22, "International Tour", "আন্তর্জাতিক ট্যুর", fallbackIcon = Icons.Default.FlightTakeoff, route = "intl_tour"),
            SubCategoryItem(2204, 22, "Visa Processing", "ভিসা প্রসেসিং", fallbackIcon = Icons.Default.AssignmentInd, route = "visa_processing"),
            SubCategoryItem(2205, 22, "Others", "অন্যান্য", fallbackIcon = Icons.Default.MoreHoriz, route = "tour_others"),

            // Category 23: Money Exchange
            SubCategoryItem(2301, 23, "Currency Exchange Counter", "কারেন্সি এক্সচেঞ্জ কাউন্টার", fallbackIcon = Icons.Default.CurrencyExchange, route = "exchange_counter"),
            SubCategoryItem(2302, 23, "Western Union & MoneyGram", "ওয়েস্টার্ন ইউনিয়ন ও মানিগ্রাম", fallbackIcon = Icons.Default.AttachMoney, route = "moneygram"),
            SubCategoryItem(2303, 23, "Remittance Bank Service", "রেমিট্যান্স ব্যাংক সেবা", fallbackIcon = Icons.Default.AccountBalance, route = "remittance_bank"),
            SubCategoryItem(2304, 23, "Others", "অন্যান্য", fallbackIcon = Icons.Default.MoreHoriz, route = "exchange_others"),

            // Category 24: Missing & Found
            SubCategoryItem(2401, 24, "Missing Person", "নিখোঁজ ব্যক্তি", fallbackIcon = Icons.Default.PersonSearch, route = "missing_person"),
            SubCategoryItem(2402, 24, "Missing Child", "নিখোঁজ শিশু", fallbackIcon = Icons.Default.ChildCare, route = "missing_child"),
            SubCategoryItem(2403, 24, "Missing Elderly", "নিখোঁজ বয়স্ক", fallbackIcon = Icons.Default.Elderly, route = "missing_elderly"),
            SubCategoryItem(2404, 24, "Lost Documents / ID", "হারানো কাগজপত্র / আইডি", fallbackIcon = Icons.Default.Description, route = "lost_docs"),
            SubCategoryItem(2405, 24, "Lost Wallet / Cash", "হারানো মানিব্যাগ / টাকা", fallbackIcon = Icons.Default.AccountBalanceWallet, route = "lost_wallet"),
            SubCategoryItem(2406, 24, "Lost Electronics", "হারানো মোবাইল / ডিভাইস", fallbackIcon = Icons.Default.PhoneAndroid, route = "lost_electronics"),
            SubCategoryItem(2407, 24, "Found Item / Person", "পাওয়া গেছে (ব্যক্তি/জিনিস)", fallbackIcon = Icons.Default.CheckCircle, route = "found_item")
        )
    }
}
