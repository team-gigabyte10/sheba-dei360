package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.ProductDto
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume

import com.barisal.cityservice.core.utils.FirebaseStorageManager

class ShoppingRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("products") }

    /**
     * Uploads an image Uri to Firebase Storage under the 'products' path,
     * returning its HTTP download URL reference string.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "products"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple image Uris to Firebase Storage under the 'products' path,
     * returning a list of HTTP download URL reference strings.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "products"): Result<List<String>> {
        return FirebaseStorageManager.uploadMultipleFiles(context, uris, folder)
    }

    /**
     * Resizes, compresses, and encodes a selected image Uri into a Base64 data URI string.
     */
    suspend fun compressImageToBase64(context: Context, uri: Uri, maxDimension: Int = 500): Result<String> = withContext(Dispatchers.IO) {
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
                ?: return@withContext Result.failure(Exception("Cannot open image file stream"))
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                return@withContext Result.failure(Exception("Failed to decode image bitmap"))
            }

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

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true)
            if (scaledBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val bytes = outputStream.toByteArray()
            scaledBitmap.recycle()

            val base64String = Base64.encodeToString(bytes, Base64.NO_WRAP)
            val dataUri = "data:image/jpeg;base64,$base64String"
            Result.success(dataUri)
        } catch (e: Exception) {
            Log.e("ShoppingRepository", "Base64 image compression failed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Compresses multiple images to Base64 strings.
     */
    suspend fun compressMultipleImages(context: Context, uris: List<Uri>): List<String> = withContext(Dispatchers.IO) {
        uris.mapNotNull { uri ->
            val res = compressImageToBase64(context, uri)
            res.getOrNull()
        }
    }

    /**
     * Saves or updates a product post in Firestore collection "products".
     */
    suspend fun saveProductToFirestore(product: ProductDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (product.id.isNotBlank()) product.id else collectionRef.document().id
        val currentUser = FirebaseAuth.getInstance().currentUser
        val productToSave = product.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: product.userId,
            userEmail = currentUser?.email ?: product.userEmail,
            userPhone = currentUser?.phoneNumber ?: product.userPhone,
            userDisplayName = currentUser?.displayName ?: product.userDisplayName
        )

        collectionRef.document(docId)
            .set(productToSave)
            .addOnSuccessListener {
                collectionRef.document(docId).update(
                    mapOf(
                        "isApproved" to false,
                        "approved" to false
                    )
                )
                if (continuation.isActive) {
                    continuation.resume(Result.success(true))
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ShoppingRepository", "Error saving product to Firestore: ${exception.message}", exception)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(exception))
                }
            }
    }

    /**
     * Real-time flow of approved product listings from Firestore.
     */
    fun getApprovedProducts(category: String = ""): Flow<List<ProductDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ShoppingRepository", "Error listening to products: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val approvedList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(ProductDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true

                            if (isApprovedBool) {
                                if (category.isBlank() || category == "সব" || category == "All" || item?.category.equals(category, ignoreCase = true)) {
                                    item?.copy(isApproved = true)
                                } else null
                            } else null
                        } catch (e: Exception) {
                            Log.e("ShoppingRepository", "Failed to parse product document: ${e.message}")
                            null
                        }
                    }
                    trySend(approvedList)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Real-time flow of unapproved (pending) products for Admin Approval.
     */
    fun getPendingProducts(): Flow<List<ProductDto>> = callbackFlow {
        val listener = collectionRef.orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ShoppingRepository", "Error listening to pending products: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(ProductDto::class.java)
                            val isApprovedBool = doc.getBoolean("isApproved") == true ||
                                    doc.getBoolean("approved") == true ||
                                    item?.isApproved == true
                            if (!isApprovedBool) {
                                item?.copy(isApproved = false)
                            } else null
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(pendingList)
                }
            }

        awaitClose {
            listener.remove()
        }
    }

    /**
     * Approves a product post in Firestore.
     */
    suspend fun approveProduct(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .update(
                mapOf(
                    "isApproved" to true,
                    "approved" to true
                )
            )
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Rejects/deletes an unapproved product post from Firestore.
     */
    suspend fun rejectProduct(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }

    /**
     * Fetches a single product by document ID in real time.
     */
    fun getProductById(productId: String): Flow<ProductDto?> = callbackFlow {
        val listener = collectionRef.document(productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null || !snapshot.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                val dto = snapshot.toObject(ProductDto::class.java)
                trySend(dto)
            }
        awaitClose { listener.remove() }
    }
}
