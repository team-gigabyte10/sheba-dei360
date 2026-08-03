package com.barisal.cityservice.data.repository

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.barisal.cityservice.data.model.CategoryDto
import com.barisal.cityservice.data.model.CategoryItem
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

const val CITY_SERVICE_DRIVE_FOLDER_ID = "1OzKqnb1pfW1-nbt2HQQSO6hlnj2oUy3B"

class CategoryRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val functions: FirebaseFunctions = FirebaseFunctions.getInstance()
) {

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

            // Resize the bitmap to maximum of 256x256 pixels while maintaining aspect ratio
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
     * Fetches existing categories to calculate the next auto-incremented integer ID.
     */
    suspend fun getNextCategoryId(): Int {
        return try {
            val snapshot = firestore.collection("categories").get().await()
            var maxId = 13 // Default categories are 1 to 13
            for (doc in snapshot.documents) {
                val idVal = doc.get("id")
                val idInt = when (idVal) {
                    is Number -> idVal.toInt()
                    is String -> idVal.toIntOrNull() ?: 0
                    else -> 0
                }
                if (idInt > maxId) {
                    maxId = idInt
                }
            }
            maxId + 1
        } catch (e: Exception) {
            // Generate a fallback unique ID if Firestore lookup fails
            (System.currentTimeMillis() % 1000000).toInt()
        }
    }

    /**
     * Saves or updates a Category document in Firebase Firestore.
     */
    suspend fun saveCategoryToFirestore(category: CategoryDto): Result<Boolean> {
        return try {
            val docId = if (category.id != null && category.id.toString().isNotBlank()) {
                category.id.toString()
            } else {
                (System.currentTimeMillis() % 1000000).toInt().toString()
            }
            val categoryToSave = if (category.id == null || category.id.toString().isBlank()) {
                val nextId = docId.toIntOrNull() ?: docId
                category.copy(id = nextId)
            } else {
                val nextId = category.id.toString().toIntOrNull() ?: category.id
                category.copy(id = nextId)
            }
            firestore.collection("categories")
                .document(docId)
                .set(categoryToSave)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Saves or updates a SubCategory document in Firebase Firestore.
     */
    suspend fun saveSubCategoryToFirestore(subCategory: com.barisal.cityservice.data.model.SubCategoryDto): Result<Boolean> {
        return try {
            val subCatIdInt = subCategory.getIdAsInt()
            val docId = if (subCatIdInt != 0) subCatIdInt.toString() else "sub_${System.currentTimeMillis()}"
            firestore.collection("subcategories")
                .document(docId)
                .set(subCategory)
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a SubCategory document from Firebase Firestore.
     */
    suspend fun deleteSubCategoryFromFirestore(subCategoryId: Int): Result<Boolean> {
        return try {
            firestore.collection("subcategories")
                .document(subCategoryId.toString())
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Deletes a Category document from Firebase Firestore.
     */
    suspend fun deleteCategoryFromFirestore(categoryId: Int): Result<Boolean> {
        return try {
            firestore.collection("categories")
                .document(categoryId.toString())
                .delete()
                .await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Listens to real-time snapshots from Firestore collection 'subcategories' filtered by categoryId.
     */
    fun getSubCategoriesFlow(categoryId: Int): Flow<List<com.barisal.cityservice.data.model.SubCategoryItem>> = callbackFlow {
        val listener = firestore.collection("subcategories")
            .whereEqualTo("categoryId", categoryId)
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val remoteSubCats = snapshot.documents.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(com.barisal.cityservice.data.model.SubCategoryDto::class.java)
                        dto?.let {
                            com.barisal.cityservice.data.model.SubCategoryItem(
                                id = it.getIdAsInt(),
                                categoryId = it.getCategoryIdAsInt(),
                                nameEn = it.nameEn,
                                nameBn = it.nameBn,
                                iconUrl = it.iconUrl,
                                fallbackIcon = matchFallbackIcon(it.route),
                                route = it.route
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CategoryRepo", "Failed to deserialize subcategory ${doc.id}: ${e.message}")
                        null
                    }
                }
                trySend(remoteSubCats)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Listens to all real-time sub-categories across categories.
     */
    fun getAllSubCategoriesFlow(): Flow<List<com.barisal.cityservice.data.model.SubCategoryItem>> = callbackFlow {
        val listener = firestore.collection("subcategories")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val remoteSubCats = snapshot.documents.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(com.barisal.cityservice.data.model.SubCategoryDto::class.java)
                        dto?.let {
                            com.barisal.cityservice.data.model.SubCategoryItem(
                                id = it.getIdAsInt(),
                                categoryId = it.getCategoryIdAsInt(),
                                nameEn = it.nameEn,
                                nameBn = it.nameBn,
                                iconUrl = it.iconUrl,
                                fallbackIcon = matchFallbackIcon(it.route),
                                route = it.route
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CategoryRepo", "Failed to deserialize subcategory ${doc.id}: ${e.message}")
                        null
                    }
                }
                trySend(remoteSubCats)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Listens to real-time snapshots from Firestore collection 'categories'.
     * Merges defaults seamlessly if collection is empty.
     */
    fun getCategoriesFlow(): Flow<List<CategoryItem>> = callbackFlow {
        val listener = firestore.collection("categories")
            .orderBy("order", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || snapshot.isEmpty) {
                    trySend(getDefaultCategories())
                    return@addSnapshotListener
                }

                val remoteCategories = snapshot.documents.mapNotNull { doc ->
                    try {
                        val dto = doc.toObject(CategoryDto::class.java)
                        dto?.let {
                            CategoryItem(
                                id = it.getIdAsInt(),
                                nameEn = it.nameEn,
                                nameBn = it.nameBn,
                                iconUrl = it.iconUrl,
                                fallbackIcon = matchFallbackIcon(it.route),
                                route = it.route
                            )
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CategoryRepo", "Failed to deserialize category ${doc.id}: ${e.message}")
                        null
                    }
                }

                if (remoteCategories.isEmpty()) {
                    trySend(getDefaultCategories())
                } else {
                    trySend(remoteCategories)
                }
            }

        awaitClose { listener.remove() }
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
     * Default core categories list used as initial/offline fallbacks.
     */
    fun getDefaultCategories(): List<CategoryItem> {
        return listOf(
            CategoryItem(1, "Health Services", "স্বাস্থ্য সেবা", fallbackIcon = Icons.Default.MedicalServices, route = "health"),
            CategoryItem(2, "Transport Services", "যাতায়াত সেবা", fallbackIcon = Icons.Default.Commute, route = "transport"),
            CategoryItem(3, "House Rent", "বাসা ভাড়া", fallbackIcon = Icons.Default.House, route = "houserent"),
            CategoryItem(4, "Shopping", "কেনা-কাটা", fallbackIcon = Icons.Default.ShoppingCart, route = "shopping"),
            CategoryItem(5, "Matrimony", "পাত্র-পাত্রী", fallbackIcon = Icons.Default.People, route = "matrimony"),
            CategoryItem(6, "Event Service", "ইভент সার্ভিস", fallbackIcon = Icons.Default.Event, route = "event"),
            CategoryItem(7, "Ride", "রাইড", fallbackIcon = Icons.Default.TwoWheeler, route = "ride"),
            CategoryItem(8, "Courier", "কুরিয়ার", fallbackIcon = Icons.Default.LocalShipping, route = "courier"),
            CategoryItem(9, "Mistri", "মিস্ত্রি", fallbackIcon = Icons.Default.Construction, route = "mistri"),
            CategoryItem(10, "Emergency Service", "জরুরী সেবা", fallbackIcon = Icons.Default.Emergency, route = "emergency"),
            CategoryItem(11, "Tutor", "টিউটর", fallbackIcon = Icons.Default.School, route = "tutor"),
            CategoryItem(12, "Flat and Land", "ফ্ল্যাট ও জমি", fallbackIcon = Icons.Default.Landscape, route = "flatland"),
            CategoryItem(13, "Location Based Services", "লোকেশন ভিত্তিক সেবা", fallbackIcon = Icons.Default.LocationOn, route = "categorymap")
        )
    }
}
