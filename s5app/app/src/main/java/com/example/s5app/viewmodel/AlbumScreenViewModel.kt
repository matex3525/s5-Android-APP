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
import com.example.s5app.model.AlbumImage
import com.example.s5app.network.AddPhotoToEventRequest
import com.example.s5app.network.ApiResult
import com.example.s5app.use_case.EventUseCases
import com.example.s5app.util.BitmapUtil.bitmapToBase64ARGB
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel(assistedFactory = AlbumScreenViewModel.AlbumScreenViewModelFactory::class)
class AlbumScreenViewModel @AssistedInject constructor(
    private val eventUseCases: EventUseCases,
    @Assisted private val userToken: String
) : ViewModel() {

    @AssistedFactory
    interface AlbumScreenViewModelFactory {
        fun create(userToken: String): AlbumScreenViewModel
    }

    private val _images: SnapshotStateList<AlbumImage> = mutableStateListOf()
    val images: State<List<AlbumImage>> = mutableStateOf(_images)

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





    fun imageBitmapToBitmap(imageBitmap: ImageBitmap): Bitmap {
        return imageBitmap.asAndroidBitmap()
    }

    private fun createAlbumImage(imageId: String, description: String, imageBitmap: ImageBitmap): AlbumImage {
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
        val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

        val width = bitmap.width
        val height = bitmap.height

        val pixels = bitmapToBase64ARGB(bitmap)

        return AddPhotoToEventRequest(
            width = width,
            height = height,
            description = description,
            pixels = pixels
        )
    }

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
                            _images.add(AlbumImage(result.data.imageId, imageData.width, imageData.height, imageData.description, imageData.pixels))
                        }
                    }
                }
            }
        }
    }
}