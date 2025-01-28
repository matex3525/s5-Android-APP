package com.example.s5app.event

import androidx.compose.ui.graphics.ImageBitmap
import com.example.s5app.network.AddPhotoToEventRequest

sealed class AlbumScreenEvent {
    data class GetAllPhotosForGivenEvent(val eventToken: String) : AlbumScreenEvent()
    data class AddPhotoToEvent(val eventToken: String, val imageBitmap: ImageBitmap) : AlbumScreenEvent()
    data class GetPDFFileForEvent(val userToken: String) : AlbumScreenEvent()
}