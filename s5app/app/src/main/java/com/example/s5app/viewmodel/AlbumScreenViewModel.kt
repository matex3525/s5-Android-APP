package com.example.s5app.viewmodel

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import com.example.s5app.network.ApiResult
import com.example.s5app.network.CupidApi
import com.example.s5app.network.GetGivenEventPhotosParams
import com.example.s5app.screen.AlbumImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

class AlbumScreenViewModel : ViewModel() {
    //var images = mutableStateListOf(AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage(), AlbumImage())

    val images = mutableStateListOf<AlbumImage>()

    fun addImage(albumImage: AlbumImage) {
        images.add(albumImage)
    }

    fun addImage(imageBitmap: ImageBitmap) {
        val albumImage = createAlbumImage(
            imageId = "null",
            description = "Opis zdjęcia",
            imageBitmap = imageBitmap
        )
        images.add(albumImage)
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

    private fun updateImages(listOfImages: List<AlbumImage>) {
        images.clear()
        images.addAll(listOfImages)
    }

    suspend fun getPhotosForGivenEvent(userToken: String, firstImageIndex: Int, lastImageIndex: Int): ApiResult<GetGivenEventPhotosParams> {
        return withContext(Dispatchers.IO) {
            try {
                // Wysyłamy żądanie GET do API za pomocą Retrofit
                val response = CupidApi.retrofitService.getPhotosForGivenEvent(userToken, firstImageIndex, lastImageIndex)

                // Sprawdzamy, czy operacja zakończyła się sukcesem
                if (response.success) {
                    Log.d("CupidApi", "Success: ${response.success}, photos: ${response.params.params}")

                    // Zwrócenie obiektu odpowiedzi w przypadku sukcesu
                    updateImages(response.params.params.map { AlbumImage.fromGetGivenEventPhotoParam(it) })
                    ApiResult.Success(response.params)
                } else {
                    Log.e("CupidApi", "API failure: success=false")
                    ApiResult.Error("API failure: success=false")
                }

            } catch (e: IOException) {
                // Obsługa błędów komunikacji (np. problemy z siecią)
                Log.e("CupidApi", "Network failure: ${e.message}")
                ApiResult.Error("Network failure: ${e.message}")
            } catch (e: HttpException) {
                // Obsługa błędów HTTP (np. 404, 401)
                when (e.code()) {
                    401 -> Log.e("CupidApi", "Unauthorized: ${e.message()}")
                    404 -> Log.e("CupidApi", "Not Found: ${e.message()}")
                    else -> Log.e("CupidApi", "HTTP Error: ${e.code()}, ${e.message()}")
                }
                ApiResult.Error(e.message.toString())
            } catch (e: Exception) {
                // Obsługa innych wyjątków
                Log.e("CupidApi", "Unexpected failure: ${e.message}")
                ApiResult.Error("Unexpected failure: ${e.message}")
            }
        }
    }


}