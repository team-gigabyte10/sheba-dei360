package com.barisal.cityservice.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.barisal.cityservice.data.model.MistriProviderDto
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

class MistriRepository {

    private val firestore by lazy { FirebaseFirestore.getInstance() }
    private val collectionRef by lazy { firestore.collection("mistri_providers") }

    /**
     * Uploads an image Uri to Firebase Storage under 'mistri' path.
     */
    suspend fun uploadImageToStorage(context: Context, uri: Uri, folder: String = "mistri"): Result<String> {
        return FirebaseStorageManager.uploadFile(context, uri, folder)
    }

    /**
     * Uploads multiple image Uris to Firebase Storage under 'mistri' path.
     */
    suspend fun uploadMultipleImagesToStorage(context: Context, uris: List<Uri>, folder: String = "mistri"): Result<List<String>> {
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
                    Pair((maxDimension * ratio).toInt(), height)
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
            Log.e("MistriRepository", "Base64 image compression failed: ${e.message}", e)
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
     * Saves or updates a Mistri Provider post in Firestore collection "mistri_providers".
     */
    suspend fun saveMistriProviderToFirestore(provider: MistriProviderDto): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        val docId = if (provider.id.isNotBlank()) provider.id else collectionRef.document().id
        val currentUser = FirebaseAuth.getInstance().currentUser
        val providerToSave = provider.copy(
            id = docId,
            isApproved = false,
            userId = currentUser?.uid ?: provider.userId,
            userEmail = currentUser?.email ?: provider.userEmail,
            userPhone = currentUser?.phoneNumber ?: provider.userPhone,
            userDisplayName = currentUser?.displayName ?: provider.userDisplayName
        )

        collectionRef.document(docId).set(providerToSave)
            .addOnSuccessListener {
                Log.d("MistriRepository", "Mistri provider post saved successfully with ID: $docId")
                if (continuation.isActive) {
                    continuation.resume(Result.success(true))
                }
            }
            .addOnFailureListener { e ->
                Log.e("MistriRepository", "Failed to save mistri provider post: ${e.message}", e)
                if (continuation.isActive) {
                    continuation.resume(Result.failure(e))
                }
            }
    }

    /**
     * Returns a real-time Flow of all approved mistri providers from Firestore.
     */
    fun getApprovedMistriProviders(categoryName: String? = null, zilla: String? = null): Flow<List<MistriProviderDto>> = callbackFlow {
        var query: Query = collectionRef.whereEqualTo("isApproved", true)
        
        if (!categoryName.isNullOrBlank() && categoryName != "অন্যান্য মিস্ত্রি") {
            query = query.whereEqualTo("categoryName", categoryName)
        }

        val listenerRegistration = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.e("MistriRepository", "Error fetching mistri providers: ${error.message}", error)
                trySend(emptyList())
                return@addSnapshotListener
            }

            if (snapshot != null) {
                var providers = snapshot.toObjects(MistriProviderDto::class.java)
                if (!zilla.isNullOrBlank()) {
                    providers = providers.filter { it.zilla.equals(zilla, ignoreCase = true) || it.addressBn.contains(zilla, ignoreCase = true) }
                }
                trySend(providers)
            } else {
                trySend(emptyList())
            }
        }

        awaitClose {
            listenerRegistration.remove()
        }
    }

    /**
     * Returns a real-time Flow of all unapproved mistri provider posts for Admin approval.
     */
    fun getPendingMistriProviders(): Flow<List<MistriProviderDto>> = callbackFlow {
        val listener = collectionRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("MistriRepository", "Error listening to pending mistri providers: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pendingList = snapshot.documents.mapNotNull { doc ->
                        try {
                            val item = doc.toObject(MistriProviderDto::class.java)
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
     * Approves a mistri provider post in Firestore.
     */
    suspend fun approveMistriProvider(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
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
     * Rejects/deletes an unapproved mistri provider post from Firestore.
     */
    suspend fun rejectMistriProvider(docId: String): Result<Boolean> = suspendCancellableCoroutine { continuation ->
        collectionRef.document(docId)
            .delete()
            .addOnSuccessListener {
                if (continuation.isActive) continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                if (continuation.isActive) continuation.resume(Result.failure(e))
            }
    }
}
