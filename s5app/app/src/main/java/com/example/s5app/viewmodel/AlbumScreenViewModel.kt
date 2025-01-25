package com.example.s5app.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.s5app.event.AlbumScreenEvent
import com.example.s5app.network.AddPhotoToEventRequest
import com.example.s5app.network.ApiResult
import com.example.s5app.screen.AlbumImage
import com.example.s5app.use_case.EventUseCases
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.inject.Inject
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltViewModel(assistedFactory = AlbumScreenViewModel.AlbumScreenViewModelFactory::class)
class AlbumScreenViewModel @AssistedInject constructor(
    private val eventUseCases: EventUseCases,
    @Assisted private val userToken: String
) : ViewModel() {

    @AssistedFactory
    interface AlbumScreenViewModelFactory {
        fun create(userToken: String): AlbumScreenViewModel
    }

    // Prywatna mutable lista obrazów
    private val _images: SnapshotStateList<AlbumImage> = mutableStateListOf()

    // Publiczny dostęp do listy obrazów jako State
    val images: State<List<AlbumImage>> = mutableStateOf(_images)

    fun addImage(albumImage: AlbumImage) {
        _images.add(albumImage)
    }

    init {
        onEvent(AlbumScreenEvent.GetAllPhotosForGivenEvent(userToken))
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
//    fun bitmapToBase64(bitmap: Bitmap): String {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
//        val byteArray = byteArrayOutputStream.toByteArray()
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
//    }

    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64ARGB(bitmap: Bitmap): String {
//        val width = bitmap.width
//        val height = bitmap.height

//        // Pobierz piksele jako tablicę Int (format ARGB)
//        val pixelArray = IntArray(width * height)
//        bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height)
//
//        // Przekonwertuj tablicę Int na ByteArray
//        val byteBuffer = ByteBuffer.allocate(pixelArray.size * 4)
//        for (pixel in pixelArray) {
//            byteBuffer.putInt(pixel)
//        }
//        val byteArray = byteBuffer.array()

        val pixelBuffer = ByteBuffer.allocate(bitmap.byteCount)
        bitmap.copyPixelsToBuffer(pixelBuffer)
        val base64Pixels = Base64.Default.encode(pixelBuffer.array())


        // Zakoduj ByteArray na Base64
//        return Base64.encodeToString(byteArray, Base64.DEFAULT)
        return base64Pixels
    }

    @OptIn(ExperimentalEncodingApi::class)
    fun base64ARGBToBitmap(base64Pixels: String, width: Int, height: Int): Bitmap {
        val pixels = Base64.Default.decode(base64Pixels.toByteArray())
        val pixelBuffer = ByteBuffer.allocate(pixels.count())
        pixelBuffer.put(pixels)
        pixelBuffer.flip()
        val bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
        bitmap.copyPixelsFromBuffer(pixelBuffer)
        return bitmap
    }

    fun imageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
        return imageBitmap.asAndroidBitmap()
    }

    // Funkcja tworząca obiekt AlbumImage z ImageBitmap
    fun createAlbumImage(imageId: String, description: String, imageBitmap: ImageBitmap): AlbumImage {
        val bitmap = imageBitmapToBitmap(imageBitmap)
        val width = bitmap.width
        val height = bitmap.height
        val pixels = bitmapToBase64ARGB(bitmap)

        return AlbumImage(
            imageId = imageId,
            width = width,
            height = height,
            description = description,
            pixels = pixels
        )
    }

    private fun getImageData(imageBitmap: ImageBitmap, description: String): AddPhotoToEventRequest {
        // Konwertowanie ImageBitmap na Bitmap
        val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

        // Pobieranie szerokości i wysokości
        val width = bitmap.width
        val height = bitmap.height

        // Pobieranie pikseli jako String (można użyć Base64 lub innego formatu)
        val pixels = bitmapToBase64ARGB(bitmap)

        // Tworzenie obiektu ImageData
        return AddPhotoToEventRequest(
            width = width,
            height = height,
            description = description,
            pixels = pixels
        )
    }

    private fun getPixelsAsString(bitmap: Bitmap): String {
        val width = bitmap.width
        val height = bitmap.height
        val pixelArray = IntArray(width * height)
        bitmap.getPixels(pixelArray, 0, width, 0, 0, width, height)

        // Konwertowanie tablicy pikseli na String (można użyć Base64 lub innego formatu)
        return pixelArray.joinToString(separator = ",") { it.toString() }
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
                    val result = eventUseCases.getPhotosForGivenEvent(event.eventToken, 0, -1)
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            _images.clear()
                            _images.addAll(result.data.map { AlbumImage.fromGetGivenEventPhotoParam(it) })
                        }
                    }
                }
            }
            is AlbumScreenEvent.AddPhotoToEvent -> {
                viewModelScope.launch {
                    val imageData = getImageData(event.imageBitmap, "")
                    val result = eventUseCases.addPhotoToEvent(event.eventToken, imageData)
                    if (result is ApiResult.Success) {
                        withContext(Dispatchers.Main) {
                            _images.add(AlbumImage("null", imageData.width, imageData.height, imageData.description, imageData.pixels))
                        }
                    }
                }
            }
        }
    }


}