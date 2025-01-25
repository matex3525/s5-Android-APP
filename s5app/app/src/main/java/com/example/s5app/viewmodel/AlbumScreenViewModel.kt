package com.example.s5app.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.event.AlbumScreenEvent
import com.example.s5app.network.ApiResult
import com.example.s5app.screen.AlbumImage
import com.example.s5app.use_case.EventUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    private val eventUseCases: EventUseCases
) : ViewModel() {
    //var images = mutableStateListOf(AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage())

    // Prywatna mutable lista obrazów
    private val _images: SnapshotStateList<AlbumImage> = mutableStateListOf()

    // Publiczny dostęp do listy obrazów jako State
    val images: State<List<AlbumImage>> = mutableStateOf(_images)

    fun addImage(albumImage: AlbumImage) {
        _images.add(albumImage)
    }

    fun addImage(imageBitmap: ImageBitmap) {
        val albumImage = createAlbumImage(
            imageId = "null",
            description = "Opis zdjęcia",
            imageBitmap = imageBitmap
        )
        _images.add(albumImage)
    }

    // Funkcja konwertująca Bitmap na Base64
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun imageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
        return imageBitmap.asAndroidBitmap()
    }

    // Funkcja tworząca obiekt AlbumImage z ImageBitmap
    fun createAlbumImage(imageId: String, description: String, imageBitmap: ImageBitmap): AlbumImage {
        val bitmap = imageBitmapToBitmap(imageBitmap)
        val width = bitmap.width
        val height = bitmap.height
        val pixels = bitmapToBase64(bitmap)

        return AlbumImage(
            imageId = imageId,
            width = width,
            height = height,
            description = description,
            pixels = pixels
        )
    }

//    private fun updateImages(listOfImages: List<AlbumImage>) {
//        images.clear()
//        images.addAll(listOfImages)
//    }

//    suspend fun getPhotosForGivenEvent(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<GetGivenEventPhotosParams> {
//        return withContext(Dispatchers.IO) {
//            try {
//                // Wysyłamy żądanie GET do API za pomocą Retrofit
//                val response = CupidApi.retrofitService.getPhotosForGivenEvent(userToken, firstImageIndex, lastImageIndex)
//
//                // Sprawdzamy, czy operacja zakończyła się sukcesem
//                if (response.success) {
//                    Log.d("CupidApi", "Success: ${response.success}, photos: ${response.params.params}")
//
//                    // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
//                    updateImages(response.params.params.map { AlbumImage.fromGetGivenEventPhotoParam(it) })
//                    ApiResult.Success(response.params)
//                } else {
//                    Log.e("CupidApi", "API failure: success=false")
//                    ApiResult.Error("API failure: success=false")
//                }
//
//            } catch (e: IOException) {
//                // Obsługa błędów komunikacji (np. problemy z siecią)
//                Log.e("CupidApi", "Network failure: ${e.message}")
//                ApiResult.Error("Network failure: ${e.message}")
//            } catch (e: HttpException) {
//                // Obsługa błędów HTTP (np. 404, 401)
//                when (e.code()) {
//                    401 -> Log.e("CupidApi", "Unauthorized: ${e.message()}")
//                    404 -> Log.e("CupidApi", "Not Found: ${e.message()}")
//                    else -> Log.e("CupidApi", "HTTP Error: ${e.code()}, ${e.message()}")
//                }
//                ApiResult.Error(e.message.toString())
//            } catch (e: Exception) {
//                // Obsługa innych wyjątków
//                Log.e("CupidApi", "Unexpected failure: ${e.message}")
//                ApiResult.Error("Unexpected failure: ${e.message}")
//            }
//        }
//    }

    fun onEvent(event: AlbumScreenEvent) {
        when(event) {
            is AlbumScreenEvent.GetAllPhotosForGivenEvent -> {
                viewModelScope.launch {
                    val result = eventUseCases.getPhotosForGivenEvent(event.eventToken, -1, -1)
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            _images.clear()
                            _images.addAll(result.data.params.map { AlbumImage.fromGetGivenEventPhotoParam(it) })
                        }
                    }
                }
            }
        }
    }


}